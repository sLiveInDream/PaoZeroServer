package com.paozero.game;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Date;

@Service
public class JobGroupBusiness {
    @Autowired
    private JobGroupRepository jobGroupRepository;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
    public void test(){
        System.out.println("tran is active:" + TransactionSynchronizationManager.isActualTransactionActive());
        JobGroup jobGroup = jobGroupRepository.findById(5);
        System.out.println(jobGroup);

        JobGroup jobGroup1 = new JobGroup("test", "thisIsTitle", 0,"abc",new Date());
        jobGroupRepository.save(jobGroup1);

        System.out.println("save jobGroup");
        System.out.println(jobGroup1);
    }

    public void testOut(){
        test();
    }

    public void testTran(){
        JobGroupBusiness jobGroupBusiness = (JobGroupBusiness) AopContext.currentProxy();
        jobGroupBusiness.test();
    }
}
