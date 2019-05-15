package com.whu.common.localcache;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class LocalCacheElement {
    /*key*/
    private String key;
    /*缓存对象*/
    private Object cacheObj;
    /*有效时间*/
    private long effectiveTime;
    /*时间单位*/
    private TimeUnit timeUnit;
    /*创建时间*/
    private long createTime;
    /*命中次数*/
    private final LongAdder hitCount = new LongAdder();

    public LocalCacheElement(String key, Object cacheObj, long effectiveTime, TimeUnit timeUnit) {
        this.key = key;
        this.cacheObj = cacheObj;
        this.effectiveTime = effectiveTime;
        this.timeUnit = timeUnit;
        this.createTime = System.currentTimeMillis();
    }

    public LocalCacheElement(String key, Object cacheObj) {
        this(key, cacheObj, -1L, null);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getCacheObj() {
        hitCount.increment();
        return cacheObj;
    }

    public void setCacheObj(Object cacheObj) {
        this.cacheObj = cacheObj;
    }

    public long getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(long effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public LongAdder getHitCount() {
        return hitCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof LocalCacheElement)){
            return false;
        }
        LocalCacheElement element = (LocalCacheElement) o;
        if (key == null || element.getKey() == null){
            return false;
        }
        return key.equals(element.getKey());
    }

    @Override
    public int hashCode() {
        if (key == null){
            return "".hashCode();
        }
        return key.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static void main(String[] args) {
        LocalCacheElement element = new LocalCacheElement("1","a",1, TimeUnit.SECONDS);
        System.out.println(element.getKey());
        System.out.println(element.getCacheObj());
        System.out.println(element.getCreateTime());
        System.out.println(element.getHitCount());
    }
}
