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
 * 对话场景
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("chat_scene")
public class ChatScene implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 描述
     */
    @TableField("desr")
    private String desr;

    /**
     * 提醒
     */
    @TableField("notice")
    private String notice;

    @TableField("content_id")
    private Integer contentId;

    /**
     * 查看数量
     */
    @TableField("read_count")
    private Integer readCount;

    /**
     * 发送数量
     */
    @TableField("send_count")
    private Integer sendCount;

    /**
     * 会话数量
     */
    @TableField("chat_count")
    private Integer chatCount;

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
