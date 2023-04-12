package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.dto.CardDTO;

import java.util.List;

/**
 * <p>
 * 付费卡密 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IBaseCardService extends IService<BaseCard> {

    List<CardDTO> enableList();
}
