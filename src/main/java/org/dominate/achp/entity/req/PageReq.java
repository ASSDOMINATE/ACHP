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
public class PageReq {

    @Min(value = 1, message = "分页数不能小于 1")
    private Integer page;

    @Min(value = 1, message = "分页大小最小 1")
    @Max(value = 200, message = "分页大小最大 200")
    private Integer size;

    public static PageReq defaultPage() {
        PageReq page = new PageReq();
        page.setPage(1);
        page.setSize(200);
        return page;
    }

    public int getIndex() {
        if (page < 1) {
            page = 1;
        }
        if(size < 1){
            size = 1;
        }
        return (page - 1) * size;
    }
}
