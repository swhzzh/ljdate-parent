package com.whu.common.redis;

public class PostKey extends BasePrefix{
    public static final int TOKEN_EXPIRE = 3600 * 24;
    private PostKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static PostKey getById = new PostKey(TOKEN_EXPIRE , "id");
}
