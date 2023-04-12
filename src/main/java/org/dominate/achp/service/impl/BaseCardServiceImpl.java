package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dominate.achp.common.enums.State;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.dto.CardDTO;
import org.dominate.achp.entity.wrapper.CardWrapper;
import org.dominate.achp.mapper.BaseCardMapper;
import org.dominate.achp.service.IBaseCardService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 付费卡密 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class BaseCardServiceImpl extends ServiceImpl<BaseCardMapper, BaseCard> implements IBaseCardService {

    @Override
    public List<CardDTO> enableList() {
        QueryWrapper<BaseCard> query = new QueryWrapper<>();
        query.lambda().eq(BaseCard::getState, State.ENABLE.getCode());
        return CardWrapper.build().entityCardDTO(list(query));
    }
}
