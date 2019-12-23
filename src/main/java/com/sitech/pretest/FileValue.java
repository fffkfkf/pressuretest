package com.sitech.pretest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author gmq serviceName.addr
 * @date 2019/12/23
 * 版权：Copyright 2000-2001 si-tech.com.cn  All Rights Reserved.
 */
@Component
@PropertySource("classpath:common.properties")
public class FileValue {
    @Value("${thread.count}")
    private int count;

    @Value("${serviceName.addr}")
    private String addr;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}




