create table t_danmaku
(
    id          bigint                       not null comment '主键'
        primary key,
    user_id     bigint                       not null comment '用户ID',
    video_id    bigint                       not null comment '视频ID',
    time        double     default 0         not null comment '相对时间',
    position    char       default '0'       not null comment '弹幕位置',
    color       char(7)    default '#ffffff' not null comment '弹幕颜色',
    nick        varchar(20)                  not null comment '用户昵称',
    content     varchar(255)                 not null comment '弹幕内容',
    create_time datetime                     null comment '创建时间',
    update_time datetime                     null comment '更新时间',
    is_delete   tinyint(1) default 0         not null comment '是否删除'
)
    comment '弹幕表';

create index idx_user_video_id
    on t_danmaku (user_id, video_id);

create index idx_video_id
    on t_danmaku (video_id);

create table t_refresh_token
(
    id            bigint               not null comment '主键'
        primary key,
    user_id       bigint               null comment '用户ID',
    refresh_token varchar(255)         null comment '刷新令牌',
    create_time   datetime             null comment '创建时间',
    update_time   datetime             null comment '更新时间',
    is_delete     tinyint(1) default 0 not null comment '是否删除'
)
    comment '刷新令牌表' row_format = DYNAMIC;

create index user_id_index
    on t_refresh_token (user_id);

create table t_tag
(
    id          bigint               not null comment '主键'
        primary key,
    name        varchar(50)          not null comment '标签名',
    create_time datetime             null comment '创建时间',
    update_time datetime             null comment '更新时间',
    is_delete   tinyint(1) default 0 not null comment '是否删除'
)
    comment '视频标签表';

create table t_user
(
    id          bigint                            not null comment '主键'
        primary key,
    email       varchar(100)                      not null comment '邮箱',
    password    varchar(255)                      not null comment '密码',
    salt        varchar(50)                       not null comment '盐值',
    nick        varchar(50)                       not null comment '昵称',
    avatar      varchar(255)                      not null comment '头像',
    sign        varchar(255) default ''           not null comment '个性签名',
    gender      char(2)      default '2'          null comment '性别，0女，1男，2-未知',
    birth       date         default '2000-01-01' not null comment '生日',
    create_time datetime                          null comment '创建时间',
    update_time datetime                          null comment '更新时间',
    is_delete   tinyint(1)   default 0            not null comment '是否删除'
)
    comment '用户表' row_format = DYNAMIC;

create table t_user_subscription
(
    id          bigint               not null comment '主键'
        primary key,
    user_id     bigint               not null comment '用户ID',
    author_id   bigint               not null comment '被订阅用户ID',
    create_time datetime             null comment '创建时间',
    update_time datetime             null comment '更新时间',
    is_delete   tinyint(1) default 0 not null comment '是否删除'
)
    comment '用户订阅表';

create index idx_author_id
    on t_user_subscription (author_id);

create index idx_user_id
    on t_user_subscription (user_id);

create table t_video
(
    id          bigint                  not null comment '主键'
        primary key,
    user_id     bigint                  null comment '用户ID',
    video_id    varchar(50)             null comment '阿里云视频ID',
    MD5         char(128)               null comment '预留字段，视频MD5',
    title       varchar(50)             null comment '视频标题',
    description text                    null comment '视频描述',
    count       bigint       default 0  not null comment '观看次数',
    cover       varchar(255) default '' not null comment '视频封面url',
    duration    float unsigned          null comment '视频时长，服务端自动同步',
    area        smallint unsigned       null comment '视频分区，默认 0-生活分区',
    status      smallint                null comment '视频状态（0-上传中，1-待审核，2-已发布，3-已下架）',
    create_time datetime                null comment '创建时间',
    update_time datetime                null comment '更新时间',
    is_delete   tinyint(1)   default 0  not null comment '是否删除'
)
    comment '视频表' row_format = DYNAMIC;

create index index_user_video
    on t_video (user_id, video_id);

create index index_video_id
    on t_video (video_id);

create table t_video_collection
(
    id          bigint               not null comment '主键'
        primary key,
    user_id     bigint               not null comment '用户ID',
    video_id    bigint               not null comment '视频ID',
    create_time datetime             null comment '创建时间',
    update_time datetime             null comment '更新时间',
    is_delete   tinyint(1) default 0 not null comment '是否删除'
)
    comment '视频收藏表';

create index idx_user_video_id
    on t_video_collection (user_id, video_id);

create table t_video_comment
(
    id          bigint                  not null comment '主键'
        primary key,
    user_id     bigint                  not null comment '用户ID',
    video_id    bigint                  not null comment '视频ID',
    content     varchar(255) default '' not null comment '评论内容',
    create_time datetime                null comment '创建时间',
    update_time datetime                null comment '更新时间',
    is_delete   tinyint(1)   default 0  not null comment '是否删除'
)
    comment '视频评论表';

create index idx_user_video_id
    on t_video_comment (user_id, video_id);

create index idx_video_id
    on t_video_comment (video_id);

create table t_video_history
(
    id          bigint               not null comment '主键'
        primary key,
    user_id     bigint               not null comment '用户ID',
    video_id    bigint               not null comment '视频ID',
    time        double unsigned      not null comment '视频时间位置',
    create_time datetime             null comment '创建时间',
    update_time datetime             null comment '更新时间',
    is_delete   tinyint(1) default 0 not null comment '是否删除'
)
    comment '视频观看历史记录';

create index idx_user_video
    on t_video_history (user_id, video_id);

create table t_video_like
(
    id          bigint               not null comment '主键'
        primary key,
    user_id     bigint               not null comment '用户ID',
    video_id    bigint               not null comment '视频ID',
    create_time datetime             null comment '创建时间',
    update_time datetime             null comment '更新时间',
    is_delete   tinyint(1) default 0 not null comment '是否删除'
)
    comment '视频点赞表';

create index idx_user_video_id
    on t_video_like (user_id, video_id);

create table t_video_tag
(
    id          bigint               not null comment '主键'
        primary key,
    video_id    bigint               not null comment '用户ID',
    tag_id      bigint               not null comment '标签ID',
    create_time datetime             null comment '创建时间',
    update_time datetime             null comment '更新时间',
    is_delete   tinyint(1) default 0 not null comment '是否删除'
)
    comment '视频标签关联表';

create index idx_tag_id
    on t_video_tag (tag_id);

create index idx_video_id
    on t_video_tag (video_id);

