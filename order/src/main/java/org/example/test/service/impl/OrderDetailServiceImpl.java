package org.example.test.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.test.dao.OrderDetailDao;
import org.example.test.entity.OrderDetail;
import org.example.test.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * (OrderDetail)表服务实现类
 *
 * @author makejava
 * @since 2023-06-23 21:09:07
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {

}

