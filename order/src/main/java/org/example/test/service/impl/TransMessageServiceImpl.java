package org.example.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.test.dao.TransMessageDao;
import org.example.test.entity.TransMessage;
import org.example.test.service.TransMessageService;
import org.springframework.stereotype.Service;

/**
 * (TransMessage)表服务实现类
 *
 * @author makejava
 * @since 2023-06-27 12:29:33
 */
@Service("transMessageService")
public class TransMessageServiceImpl extends ServiceImpl<TransMessageDao, TransMessage> implements TransMessageService {

}

