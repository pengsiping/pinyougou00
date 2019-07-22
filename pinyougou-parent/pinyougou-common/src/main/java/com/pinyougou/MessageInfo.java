package com.pinyougou;

public class MessageInfo<T> {

    public static final int METHOD_ADD=1;
    public static final int METHOD_UPDATE=2;
    public static final int METHOD_DELETE=3;

    private Object context;

    private String topic;

    private String tags;

    private String keys;

    private int method;  //要执行的方法

    public MessageInfo() {
    }

    public MessageInfo( String topic, String tags, String keys, int method,Object context) {
        this.context = context;
        this.topic = topic;
        this.tags = tags;
        this.keys = keys;
        this.method = method;
    }

    public static int getMethodAdd() {
        return METHOD_ADD;
    }

    public static int getMethodUpdate() {
        return METHOD_UPDATE;
    }

    public static int getMethodDelete() {
        return METHOD_DELETE;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "context=" + context +
                ", topic='" + topic + '\'' +
                ", tags='" + tags + '\'' +
                ", keys='" + keys + '\'' +
                ", method=" + method +
                '}';
    }

}
