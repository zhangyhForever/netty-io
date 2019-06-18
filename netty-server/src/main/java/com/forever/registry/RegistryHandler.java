package com.forever.registry;

import com.forever.protocol.ProtocolEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: zhang
 * @Date: 2019/6/18
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    private List<String> classNames = new ArrayList<String>();

    private Map<String, Object> registryMap = new HashMap<String, Object>();

    public RegistryHandler(){
        scannerClass("com.forever.serviceimpl");
        registry();
    }

    private void registry() {
        if(classNames.isEmpty()){
            return;
        }
        for (String className: classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(), clazz.newInstance());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void scannerClass(String packageName) {
        URL url = this.getClass().getResource("/"+packageName.replaceAll("\\.", "/"));
        File file = new File(url.getFile());
        for(File f : file.listFiles()){
            if(f.isDirectory()){
                scannerClass(packageName + "." + f.getName());
            }else{
                classNames.add(packageName+"."+f.getName().replaceAll(".class","").trim());
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        ProtocolEntity request = (ProtocolEntity)msg;
        if(registryMap.containsKey(request.getClassName())){
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParamsType());
            result = method.invoke(clazz, request.getPrarams());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("server is Exception");
    }
}
