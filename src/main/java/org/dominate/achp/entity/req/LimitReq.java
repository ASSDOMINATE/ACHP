package org.dominate.achp.entity.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页请求类
 * 使用 Validation
 *
 * @author dominate
 * @since 2022/02/25
 */
@Data
public class LimitReq {

    @Min(value = 0, message = "分页位置不能小于 0")
    private Integer index;

    @Min(value = 1, message = "分页大小最小 1")
    @Max(value = 1000, message = "分页大小最大 1000")
    private Integer size;
}
