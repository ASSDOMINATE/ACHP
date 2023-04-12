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
 * 对话记录，关联用户-会话组-会话内容-会话场景
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("chat_record")
public class ChatRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号ID
     */
    @TableField("account_id")
    private Integer accountId;

    /**
     * 会话内容ID
     */
    @TableField("content_id")
    private Integer contentId;

    /**
     * 会话组ID
     */
    @TableField("group_id")
    private String groupId;

    /**
     * 会话场景，0代表无场景
     */
    @TableField("scene_id")
    private Integer sceneId;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("is_del")
    private Boolean del;


}
