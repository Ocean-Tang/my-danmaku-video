package com.study.mydanmakuvideo.modules.video.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.study.mydanmakuvideo.common.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 视频标签表
 * </p>
 *
 * @author ocean
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_tag")
@ApiModel(value = "TagEntity对象", description = "视频标签表")
public class TagEntity extends BaseEntity<TagEntity> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("标签名")
    private String name;

    @Override
    public Serializable pkVal() {
        return null;
    }
}
