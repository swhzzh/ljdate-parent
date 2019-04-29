package com.whu.common.redis;

public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
