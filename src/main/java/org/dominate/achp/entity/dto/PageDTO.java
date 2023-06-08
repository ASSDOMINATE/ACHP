package org.dominate.achp.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer page;

    private Integer size;

    private Long total;

    public PageDTO() {
        this.page = 0;
        this.size = 0;
        this.total = 0L;
    }

    public PageDTO(Integer page, Integer size, Long total) {
        this.page = page;
        this.size = size;
        this.total = total;
    }
}
