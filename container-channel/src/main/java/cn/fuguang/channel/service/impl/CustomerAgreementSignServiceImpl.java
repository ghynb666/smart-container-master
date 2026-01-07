package cn.fuguang.channel.service.impl;

import cn.fuguang.channel.mapper.CustomerAgreementSignMapper;
import cn.fuguang.channel.service.CustomerAgreementSignService;
import cn.fuguang.entity.CustomerAgreementSignEntity;
import cn.fuguang.exception.ContainerException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CustomerAgreementSignServiceImpl implements CustomerAgreementSignService {


    @Resource
    private CustomerAgreementSignMapper customerAgreementSignMapper;


    @Override
    public void saveEntity(CustomerAgreementSignEntity customerAgreementSignEntity) {
        try {
            customerAgreementSignMapper.insert(customerAgreementSignEntity);
        } catch (Exception e) {
            throw ContainerException.DATABASE_INERT_ERROR;
        }
    }
}
