package com.study.mydanmakuvideo.modules.video.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mydanmakuvideo.common.constant.RedisKeys;
import com.study.mydanmakuvideo.common.enums.ReturnCodeEnums;
import com.study.mydanmakuvideo.common.enums.VideoAreaEnum;
import com.study.mydanmakuvideo.common.enums.VideoStatusEnum;
import com.study.mydanmakuvideo.common.exception.ApiException;
import com.study.mydanmakuvideo.common.utils.JwtUtil;
import com.study.mydanmakuvideo.modules.danmaku.service.IDanmakuService;
import com.study.mydanmakuvideo.modules.oss.service.IOssService;
import com.study.mydanmakuvideo.modules.oss.service.IVodService;
import com.study.mydanmakuvideo.modules.video.mapper.VideoMapper;
import com.study.mydanmakuvideo.modules.video.model.dto.AiReviewJobDTO;
import com.study.mydanmakuvideo.modules.video.model.dto.TagDTO;
import com.study.mydanmakuvideo.modules.video.model.dto.UploadVideoDTO;
import com.study.mydanmakuvideo.modules.video.model.dto.UserPreference;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoDTO;
import com.study.mydanmakuvideo.modules.video.model.dto.VideoLikeInfoDTO;
import com.study.mydanmakuvideo.modules.video.model.entity.VideoEntity;
import com.study.mydanmakuvideo.modules.video.model.param.VideoQueryParam;
import com.study.mydanmakuvideo.modules.video.repository.VideoRepository;
import com.study.mydanmakuvideo.modules.video.service.IVideoCollectionService;
import com.study.mydanmakuvideo.modules.video.service.IVideoLikeService;
import com.study.mydanmakuvideo.modules.video.service.IVideoService;
import com.study.mydanmakuvideo.modules.video.service.IVideoTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频表 服务实现类
 * </p>
 *
 * @author ocean
 * 
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl extends ServiceImpl<VideoMapper, VideoEntity> implements IVideoService {

    private final IVodService vodService;
    private final IOssService ossService;
    private final IDanmakuService danmakuService;
    private final RedisTemplate<String, String> redisTemplate;
    private final IVideoLikeService videoLikeService;
    private final IVideoCollectionService videoCollectionService;
    private final IVideoTagService videoTagService;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final VideoRepository videoRepository;

    private final ThreadPoolExecutor videoUploadExecutor = new ThreadPoolExecutor(3, 10,
            5, TimeUnit.MINUTES, new LinkedBlockingDeque<>(),
            ThreadUtil.createThreadFactory("upload-video-executor"));

    @Override
    public VideoDTO getVideoDetail(Long id) throws ClientException {
        String key = RedisKeys.VIDEO_DETAIL + id;
        String json = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, VideoDTO.class);
        }

        VideoDTO videoDTO = baseMapper.getVideoDetail(id);
        Assert.notNull(videoDTO, "不存在的视频ID：{}", id);

        if (videoDTO.getStatus().equals(VideoStatusEnum.PUBLISHED)) {
            String videoPlayUrl = vodService.getVideoPlayUrl(videoDTO.getVideoId());
            videoDTO.setPlayUrl(videoPlayUrl);
        }

        long count = danmakuService.countByVideoId(id);
        videoDTO.setDanmakus(count);

        Long likeCount = videoLikeService.getLikeCount(id);
        videoDTO.setLike(likeCount);

        List<TagDTO> tags = videoTagService.listByVideoId(id);
        videoDTO.setTags(tags);
        // 阿里云视频播放地址默认失效时长 60 分钟
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(videoDTO),
                55, TimeUnit.MINUTES);
        return videoDTO;
    }

    @Override
    public IPage<VideoDTO> pageVideo(IPage<VideoDTO> page, VideoQueryParam param) {
        return baseMapper.pageVideo(page, param);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadVideo(UploadVideoDTO videoDTO) throws IOException {
        Long userId = videoDTO.getUserId();
        String uploadKey = RedisKeys.VIDEO_UPLOAD + userId;
        String value = redisTemplate.opsForValue().get(uploadKey);
        if (value != null && Integer.parseInt(value) >= 2) {
            log.error("用户今日上传视频已经超过上限：{}", userId);
            throw new ApiException(ReturnCodeEnums.UPLOAD_VIDEO_LIMIT);
        }

        MultipartFile videoFile = videoDTO.getVideoFile();

        // 复用相同的视频文件
        VideoEntity videoEntity = baseMapper.getByMd5(videoDTO.getMd5());
        VideoEntity newVideo = new VideoEntity();
        Long id = IdUtil.getSnowflakeNextId();
        newVideo.setId(id);
        MultipartFile coverFile = videoDTO.getCoverFile();
        String coverUrl = ossService.uploadFile(coverFile);

        if (videoEntity != null) {
            String videoId = videoEntity.getVideoId();
            Float duration = videoEntity.getDuration();
            // 保存视频信息
            newVideo = new VideoEntity()
                    .setVideoId(videoId)
                    .setDuration(duration)
                    .setStatus(videoEntity.getStatus());
        } else {
            Path tempFile = Files.createTempFile("tmp-", videoFile.getOriginalFilename());
            File file = tempFile.toFile();
            videoFile.transferTo(file);
            videoUploadExecutor.execute(() -> uploadVideo(videoDTO, file, coverUrl, id));
            newVideo.setStatus(VideoStatusEnum.UPLOADING);
        }
        newVideo
                .setTitle(videoDTO.getTitle())
                .setCover(coverUrl)
                .setMd5(videoDTO.getMd5())
                .setDescription(videoDTO.getDescription())
                .setArea(videoDTO.getArea())
                .setUserId(videoDTO.getUserId());
        this.save(newVideo);

        if (videoEntity != null) {
            this.saveToEsById(newVideo.getId());
        }

        if (CollUtil.isNotEmpty(videoDTO.getTags())) {
            videoTagService.saveTags(newVideo.getId(), videoDTO.getTags());
        }

        redisTemplate.opsForValue().increment(uploadKey);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ZonedDateTime zonedDateTime = tomorrow.atStartOfDay(ZoneId.systemDefault());
        Date expired = Date.from(zonedDateTime.toInstant());
        redisTemplate.expireAt(uploadKey, expired);

        return newVideo.getId();
    }

    private void uploadVideo(UploadVideoDTO videoDTO, File videoFile, String coverUrl, Long id) {
        try {
            log.info("开始上传视频，视频ID：{}", id);
            String videoId = vodService.upload(videoDTO.getTitle(), videoFile.getName(), coverUrl, Files.newInputStream(videoFile.toPath()));
            boolean update = this.lambdaUpdate().eq(VideoEntity::getId, id)
                    .set(VideoEntity::getVideoId, videoId)
                    .set(VideoEntity::getStatus, VideoStatusEnum.TO_BE_REVIEWED)
                    .update();
            log.info("视频：{}上传成功，{}", id, update);
            // 提交 AI 审核
            String jobId = vodService.putAiReview(videoId);
            // 提交到 Redis 任务队列
            AiReviewJobDTO jobDTO = new AiReviewJobDTO(jobId, id, videoId);
            redisTemplate.opsForList().rightPush(RedisKeys.AI_REVIEW, JSONUtil.toJsonStr(jobDTO));
            redisTemplate.delete(RedisKeys.VIDEO_DETAIL + id);
        } catch (IOException e) {
            log.error("用户：{}上传的视频：{}上传视频！", videoDTO.getUserId(), videoDTO.getVideoFile().getOriginalFilename());
            throw new RuntimeException(e);
        } catch (ClientException e) {
            log.error("获取视频详情失败，视频ID：{}", id);
            throw new RuntimeException(e);
        } finally {
            videoFile.delete();
        }
    }

    @Override
    public void incrViewCounts(Long id) {
        String key = RedisKeys.VIEW_COUNTS + id;
        if (redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().increment(RedisKeys.VIEW_COUNTS + id);
        } else {
            Long count = this.lambdaQuery()
                    .select(VideoEntity::getCount)
                    .eq(VideoEntity::getId, id)
                    .one().getCount();
            redisTemplate.opsForValue().set(key, (count + 1) + "");
        }
    }

    @Override
    public VideoLikeInfoDTO getVideoLikeInfo(Long videoId) {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        boolean isLike = videoLikeService.isLikeVideo(userId, videoId);
        boolean isCollect = videoCollectionService.isCollect(userId, videoId);
        VideoLikeInfoDTO videoLikeInfoDTO = new VideoLikeInfoDTO();
        videoLikeInfoDTO.setIsLike(isLike);
        videoLikeInfoDTO.setIsCollect(isCollect);
        return videoLikeInfoDTO;
    }

    @Override
    public List<VideoDTO> recommendVideosUserBased() throws TasteException {
        Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
        Long count = baseMapper.getPreferenceCountByUserId(userId);
        if (count < 5) {
            log.info("userId：{} 偏好视频数据太少", userId);
            return getRandomVideo();
        }
        DataModel dataModel = getDataModel();
        UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

        List<RecommendedItem> recommend = recommender.recommend(userId, 50);
        if (recommend.isEmpty()) {
            log.info("userId：{} 可推荐视频数据太少", userId);
            return getRandomVideo();
        }
        log.info("向 userId：{} 推荐视频，执行 Mahout 推荐算法", userId);
        List<Long> videoIds = recommend.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
        return Optional.ofNullable(getRandomVideo(videoIds)).orElseGet(this::getRandomVideo);
    }

    private List<VideoDTO> getRandomVideo(List<Long> videoIds) {
        return baseMapper.getRandomVideo(videoIds);
    }

    private List<VideoDTO> getRandomVideo() {
        return baseMapper.getRandomVideo(Collections.emptyList());
    }

    @Override
    public List<VideoDTO> recommendVideoItemBased(Long videoId) throws TasteException {
        try {
            Long count = baseMapper.getPreferenceCountByVideoId(videoId);
            if (count < 5) {
                log.info("videoId：{} 偏好视频数据太少", videoId);
                return getRandomVideo();
            }

            DataModel dataModel = getDataModel();
            ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
            GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel, similarity);

            List<RecommendedItem> recommendedItems;
            Long userId = JwtUtil.LOGIN_USER_HANDLER.get();
            if (userId != null) {
                log.info("向用户：{}提供基于视频：{}的推荐", userId, videoId);
                recommendedItems = recommender.recommend(userId, 50);
            } else {
                log.info("向游客用户提供基于视频：{}的推荐", videoId);
                recommendedItems = recommender.mostSimilarItems(videoId, 50);
            }

            if (recommendedItems.isEmpty()) {
                log.info("videoId：{} 可推荐视频数据太少，随机推荐视频", videoId);
                return getRandomVideo();
            }
            log.info("推荐 videoId：{} 相关视频，执行 Mahout 推荐算法", videoId);
            List<Long> videoIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
            return getRandomVideo(videoIds);
        } catch (NoSuchItemException e) {
            log.info("视频：{}相关数据稀少，随机推荐视频", videoId);
            return getRandomVideo();
        }
    }

    @Override
    public VideoDTO getVideoNotPlayUrl(Long videoId) {
        VideoDTO videoDTO = baseMapper.getVideoDetail(videoId);
        List<TagDTO> tags = videoTagService.listByVideoId(videoId);
        videoDTO.setTags(tags);
        return videoDTO;
    }

    @Override
    public List<VideoDTO> userVideos(Long userId) {
        Long currentUserId = JwtUtil.LOGIN_USER_HANDLER.get();
        return this.lambdaQuery()
                .eq(VideoEntity::getUserId, userId)
                // 用户查询自己的视频列表，查询所有视频状态，否则，只查询已发布视频
                .eq(currentUserId != null && !NumberUtil.equals(currentUserId, userId), VideoEntity::getStatus, VideoStatusEnum.PUBLISHED)
                .orderByDesc(VideoEntity::getCreateTime)
                .list()
                .stream()
                .map(i -> BeanUtil.toBean(i, VideoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public IPage<VideoDTO> queryVideosByEs(Page<VideoDTO> page, VideoQueryParam param) {
        BoolQueryBuilder queryParam = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("status", VideoStatusEnum.PUBLISHED));
        List<HighlightBuilder.Field> highlights = new ArrayList<>();
        String keyword = param.getKeyword();
        if (StrUtil.isNotBlank(keyword)) {
            String[] keywords = keyword.split(" ");
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery();
            for (String k : keywords) {
                keywordQuery.should(QueryBuilders.multiMatchQuery(k, "title", "nick", "tags.name"));
            }
            queryParam.must(keywordQuery);
            /*queryParam.should(QueryBuilders.matchPhraseQuery("title", keyword))
                    .should(QueryBuilders.matchPhraseQuery("nick", keyword));*/
            highlights.add(new HighlightBuilder.Field("title").preTags("<strong class=\"keyword\">").postTags("</strong>"));
            highlights.add(new HighlightBuilder.Field("nick").preTags("<strong class=\"keyword\">").postTags("</strong>"));
        }
        VideoAreaEnum area = param.getArea();
        if (area != null) {
            queryParam.must(QueryBuilders.matchQuery("area", area));
        }

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.functionScoreQuery(queryParam, ScoreFunctionBuilders.randomFunction().seed(param.getSeed()).setField("_seq_no")))
                .withPageable(PageRequest.of((int) (page.getCurrent() - 1), (int) page.getSize()))
                .withHighlightFields(highlights)
                .build();

        SearchHits<VideoDTO> search = elasticsearchRestTemplate.search(query, VideoDTO.class);
        long totalHits = search.getTotalHits();
        page.setTotal(totalHits);
        page.setPages(totalHits / page.getSize());
        List<VideoDTO> records = new ArrayList<>();
        for (SearchHit<VideoDTO> searchHit : search.getSearchHits()) {
            VideoDTO content = searchHit.getContent();
            List<String> title = searchHit.getHighlightField("title");
            List<String> nick = searchHit.getHighlightField("nick");
            if (CollUtil.isNotEmpty(title)) {
                content.setTitle(title.get(0));
            }
            if (CollUtil.isNotEmpty(nick)) {
                content.setNick(nick.get(0));
            }
            records.add(content);
        }
        page.setRecords(records);
        return page;
    }

    @Override
    public void saveToEsById(Long videoId) {
        VideoDTO videoDTO = this.getVideoNotPlayUrl(videoId);
        videoRepository.save(videoDTO);
    }

    @Override
    public void saveToEsByIdBatch(Set<Long> videoIds) {
        for (Long videoId : videoIds) {
            this.saveToEsById(videoId);
        }
    }

    private DataModel getDataModel() {
        List<UserPreference> userPreferences = baseMapper.getPreferenceData();

        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();

        List<List<UserPreference>> userPreferencesGroupByUserId = CollUtil.groupByField(userPreferences, "userId");
        for (List<UserPreference> preferences : userPreferencesGroupByUserId) {
            PreferenceArray preferenceArray = new GenericUserPreferenceArray(preferences);
            fastByIdMap.put(preferences.get(0).getUserID(), preferenceArray);
        }

        return new GenericDataModel(fastByIdMap);
    }

}
