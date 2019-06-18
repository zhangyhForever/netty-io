package com.forever.serviceimpl;

import com.forever.service.IRpcService;

/**
 * @Description:
 * @Author: zhang
 * @Date: 2019/6/18
 */
public class RpcServiceimpl implements IRpcService {
    public int add(int a, int b) {
        return a + b;
    }

    public int sub(int a, int b) {
        return a - b;
    }

    public int mult(int a, int b) {
        return a * b;
    }

    public int div(int a, int b) {
        return a / b;
    }
}
