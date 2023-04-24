package org.dominate.achp.entity.req;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * ID请求
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
public class IdsReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> ids;

}
