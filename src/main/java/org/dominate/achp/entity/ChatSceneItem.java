package org.dominate.achp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 对话场景项
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("chat_scene_item")
public class ChatSceneItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("scene_id")
    private Integer sceneId;

    /**
     * 类型 输入框 单选 多选 字数限制
     */
    @TableField("type")
    private Integer type;

    /**
     * 内容，根据type有不同的定义
     */
    @TableField("content")
    private String content;

    @TableField("title")
    private String title;

    /**
     * 排序数
     */
    @TableField("seq")
    private Integer seq;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField("is_del")
    private Boolean del;

    @TableField("create_by")
    private Integer createBy;

    @TableField("update_by")
    private Integer updateBy;


}
