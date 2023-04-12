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
 * 对话场景关联
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("chat_scene_relate")
public class ChatSceneRelate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 场景ID
     */
    @TableField("scene_id")
    private Integer sceneId;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Integer categoryId;

    @TableField("category_type")
    private Integer categoryType;

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
