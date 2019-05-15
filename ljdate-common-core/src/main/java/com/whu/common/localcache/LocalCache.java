package com.whu.common.localcache;

import java.awt.event.KeyListener;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

public class LocalCache {
    private static final ConcurrentHashMap<String, SoftReference<LocalCacheElement>> cacheMap = new ConcurrentHashMap<>();
    private static final DelayQueue<DelayElement<String>> delayQueue = new DelayQueue<>();
    private static final Thread thread;

    private LocalCache(){}

    static {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("clear begin");
                expiredClear();
                System.out.println("clear end");
            }
        });

        thread.setDaemon(true);
        thread.setName("ClearCache");
        thread.start();
    }

    /**
     * 添加永久缓存
     *
     * @param key
     * @param obj
     */
    public static void put(String key, Object obj){
        SoftReference<LocalCacheElement> softReference = new SoftReference<>(new LocalCacheElement(key, obj));
        obj = null;
        cacheMap.put(key, softReference);
    }

    /**
     * 添加有限期缓存
     *
     * @param key
     * @param obj
     * @param effectiveTime
     * @param timeUnit
     */
    public static void put(String key, Object obj, long effectiveTime, TimeUnit timeUnit){
        delayQueue.remove(new DelayElement<String>(key, -1L));
        long nanoSeconds = TimeUnit.NANOSECONDS.convert(effectiveTime, timeUnit);
        delayQueue.put(new DelayElement<String>(key, nanoSeconds));
        SoftReference<LocalCacheElement> softReference = new SoftReference<>(new LocalCacheElement(key, obj, effectiveTime, timeUnit));
        obj = null;
        cacheMap.put(key, softReference);
    }

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    public static Object get(String key){
        SoftReference<LocalCacheElement> softReference = cacheMap.get(key);
        if (softReference == null){
            System.out.println("缓存不存在, key : "+ key);
            return null;
        }

        LocalCacheElement localCacheElement = softReference.get();
        if (localCacheElement == null){
            System.out.println("缓存已被jvm回收, key : " + key);
            return null;
        }
        return localCacheElement.getCacheObj();
    }

    /**
     * 是否存在key对应缓存
     *
     * @param key
     * @return
     */
    public static boolean containsKey(String key){
        return cacheMap.containsKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public static void remove(String key){
        cacheMap.remove(key);
        delayQueue.remove(new DelayElement<>(key, -1L));
    }

    /**
     *
     *
     *
     * @param fuzzyKey
     */
    public static void fuzzyRemove(String fuzzyKey){
        for (String s : cacheMap.keySet()) {
            if (s.contains(fuzzyKey)){
                remove(s);
            }
        }
    }

    public static void clear(){
        cacheMap.clear();
        delayQueue.clear();
    }

    private static void expiredClear(){
        while (true){
            try {
                DelayElement<String> delayElement = delayQueue.take();
                System.out.println("清理缓存 : " + delayElement);
                if (delayElement != null){
                    cacheMap.remove(delayElement.getElement());
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取缓存清理线程状态
     * @return
     */
    public static Thread.State clearThreadState() {
        return thread.getState();
    }
    /**
     * 统计
     */
    public static void statistics() {

        System.out.println("缓存数量:" + cacheMap.size());

        for(Map.Entry<String, SoftReference<LocalCacheElement>> entry : cacheMap.entrySet()){
            String key=entry.getKey();
            LocalCacheElement cacheElement=entry.getValue().get();
            //System.out.format("Key:{},创建时间:{},过期时间:{},命中次数:{},缓存对象:{}", key,cacheElement.getCreateTime(),cacheElement.getEffectiveTime(),cacheElement.getHitCount(),cacheElement.getCacheObj());
        }
    }
    public static void main(String[] args) throws InterruptedException {
        String user1 = "user1";
        String user2 = "user2";

        LocalCache.put("1", user1,2L,TimeUnit.SECONDS);
        LocalCache.put("2", user2,5L,TimeUnit.SECONDS);
        LocalCache.put("3", user2);
        user1=null;
        user2=null;
        LocalCache.get("1");
        LocalCache.get("1");
        LocalCache.get("3");
        statistics();
//		System.out.println(delayQueue.contains(new DelayElement("1",-1L)));
//		delayQueue.remove(new DelayElement("1",-1L));
//		System.out.println(user1+":"+user2);
//		System.out.println( ((TawSystemUser)LocalCache.get("1")).getUserid());

        while(1==1) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println(cacheMap.size());
            System.out.println( clearThreadState());
        }
    }
}
