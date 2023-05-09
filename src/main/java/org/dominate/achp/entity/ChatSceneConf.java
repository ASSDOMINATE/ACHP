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
 * 对话场景配置，把场景项拼接为文字的配置
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("chat_scene_conf")
public class ChatSceneConf implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 场景ID
     */
    @TableField("scene_id")
    private Integer sceneId;

    /**
     * 拼接的场景项ID，可为空文字不能同时为空，只拼接文字
     */
    @TableField("item_id")
    private Integer itemId;

    /**
     * 拼接在场景项前的文字，可为空场景不能同时为空，只拼接场景项
     */
    @TableField("start")
    private String start;

    /**
     * 拼接在场景项后的文字，可为空场景不能同时为空，只拼接场景项
     */
    @TableField("end")
    private String end;
    /**
     * 排序数
     */
    @TableField("seq")
    private Integer seq;

    @TableField("item_type")
    private Integer itemType;

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
