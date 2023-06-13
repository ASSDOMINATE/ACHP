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
 * 对话场景分类
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("chat_scene_category")
public class ChatSceneCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类类型 角色/场景
     */
    @TableField("type")
    private Integer type;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 描述
     */
    @TableField("desr")
    private String desr;

    @TableField("seq")
    private Integer seq;

    @TableField("info")
    private String info;

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
