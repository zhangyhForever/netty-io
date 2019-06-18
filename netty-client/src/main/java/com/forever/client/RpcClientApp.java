package com.forever.client;

import com.forever.client.proxy.RpcClient;
import com.forever.service.IHelloService;

/**
 * @Description:
 * @Author: zhang
 * @Date: 2019/6/18
 */
public class RpcClientApp {

    public static void main(String[] args) {
        IHelloService helloService = RpcClient.create(IHelloService.class);
        helloService.hello("forever");
    }
}
