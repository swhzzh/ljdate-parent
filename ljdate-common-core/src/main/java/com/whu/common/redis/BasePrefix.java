package com.whu.common.redis;

public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;
    private String prefix;


    // 0表示永不过期
    public BasePrefix(String prefix){
        this(0,prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {
        return this.expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = this.getClass().getSimpleName();
        return className + ":" + this.prefix;
    }
}
