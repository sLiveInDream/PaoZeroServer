package com.paozero.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestBusiness {
    @Autowired
    private JobGroupBusiness jobGroupBusiness;

    public void test(){
        jobGroupBusiness.test();
    }
}
