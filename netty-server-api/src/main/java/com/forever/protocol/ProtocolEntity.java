package com.forever.protocol;

import lombok.Data;

/**
 * @Description:
 * @Author: zhang
 * @Date: 2019/6/18
 */

@Data
public class ProtocolEntity {

    private String className;
    private String methodName;
    private Class<?>[] paramsType;
    private Object[] prarams;
}
