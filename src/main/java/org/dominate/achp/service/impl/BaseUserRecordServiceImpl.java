package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dominate.achp.entity.BaseUserRecord;
import org.dominate.achp.mapper.BaseUserRecordMapper;
import org.dominate.achp.service.IBaseUserRecordService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户基础记录 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class BaseUserRecordServiceImpl extends ServiceImpl<BaseUserRecordMapper, BaseUserRecord> implements IBaseUserRecordService {

}
