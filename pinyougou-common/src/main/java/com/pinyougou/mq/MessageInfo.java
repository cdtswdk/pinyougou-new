package com.pinyougou.mq;

import java.io.Serializable;

public class MessageInfo implements Serializable {
    private static final long serialVersionUID = -4206122703453814687L;

    //method:1|2|3->增加|修改|删除
    private int method;

    //要发送的内容
    private Object context;

    //1|2|3->增加|修改|删除
    public static final int METHOD_ADD = 1;
    public static final int METHOD_UPDATE = 2;
    public static final int METHOD_DELETE = 3;

    public MessageInfo() {
    }

    public MessageInfo(int method, Object context) {
        this.method = method;
        this.context = context;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "method=" + method +
                ", context=" + context +
                '}';
    }
}
