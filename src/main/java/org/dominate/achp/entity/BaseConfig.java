package org.dominate.achp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 基础配置
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("base_config")
public class BaseConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 每日请求次数限制，0 不启用
     */
    @TableField("daily_request_limit")
    private Integer dailyRequestLimit;

    /**
     * 多少秒频率限制，0 不启用
     */
    @TableField("freq_second_limit")
    private Integer freqSecondLimit;

    /**
     * 对话模型ID
     */
    @TableField("model_id")
    private String modelId;

    @TableField("max_result_tokens")
    private Integer maxResultTokens;

    @TableField("temperature")
    private BigDecimal temperature;

    @TableField("set_system")
    private String setSystem;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("create_by")
    private Integer createBy;

    @TableField("update_by")
    private Integer updateBy;

    /**
     * 是否删除
     */
    @TableField("is_del")
    private Boolean del;


}
