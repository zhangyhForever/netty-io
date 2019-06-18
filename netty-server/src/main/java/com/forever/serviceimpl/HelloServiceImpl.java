package com.forever.serviceimpl;

import com.forever.service.IHelloService;

/**
 * @Description:
 * @Author: zhang
 * @Date: 2019/6/18
 */
public class HelloServiceImpl implements IHelloService {
    public String hello(String name) {
        return "hello " + name;
    }
}
