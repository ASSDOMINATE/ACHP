package org.dominate.achp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dominate.achp.entity.dto.ChatDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 聊天内容
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("chat_content")
public class ChatContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 上一内容ID
     */
    @TableField("last_id")
    private Integer lastId;

    /**
     * Chat-GPT 模型ID
     */
    @TableField("model_id")
    private String modelId;

    /**
     * 问句
     */
    @TableField("sentence")
    private String sentence;

    /**
     * 回复
     */
    @TableField("reply")
    private String reply;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField("is_del")
    private Boolean del;

    public ChatContent() {
    }

    public ChatContent(ChatDTO chat, String reply, int lastId) {
        this.modelId = chat.getModelId();
        this.sentence = chat.getSentence();
        this.reply = reply;
        this.lastId = lastId;
    }
}
