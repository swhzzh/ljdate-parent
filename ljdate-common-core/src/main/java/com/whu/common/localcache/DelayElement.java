package com.whu.common.localcache;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DelayElement<T> implements Delayed {

    /*base nano second time*/
    public static final long NANO_ORIGIN = System.nanoTime();

    /**
     * return nano second time offset by origin
     *
     * @return
     */
    final static long now(){
        return System.nanoTime() - NANO_ORIGIN;
    }

    public static final AtomicLong sequencer = new AtomicLong(0);

    private long sequenceNumber;

    private final long time;

    private final T element;

    public DelayElement(T submit, long timeout){
        this.time = now() + timeout;
        this.element = submit;
        this.sequenceNumber = sequencer.getAndIncrement();
    }

    public T getElement(){
        return this.element;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(time - now(), TimeUnit.NANOSECONDS);
    }


    @Override
    public int compareTo(Delayed o) {
        if (o == this){
            return 0;
        }

        if (o instanceof DelayElement){
            DelayElement x = (DelayElement) o;
            long diff = time - x.time;
            if (diff < 0){
                return  -1;
            }
            else if (diff > 0){
                return 1;
            }
            else if (sequenceNumber < x.sequenceNumber){
                return -1;
            }
            else {
                return 1;
            }
        }
        long d = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return  d == 0 ? 0 : d < 0 ? -1 : 1;
    }


    @Override
    public int hashCode() {
        final  int prime = 31;
        int result = 1;
        result  = prime * result + (element == null ? 0 : element.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        DelayElement other = (DelayElement) obj;
        if (element == null){
            if (other.element != null){
                return false;
            }
        }
        else if (! element.equals(other.element)){
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static void main(String[] args) {
        DelayElement<Object> d2=new DelayElement<Object>(new Object(),9000000000L);
        DelayElement d3=new DelayElement("d12@",9000000000L);
        System.out.println(TimeUnit.SECONDS.convert((System.nanoTime()+9000000000L)-System.nanoTime(),TimeUnit.NANOSECONDS));
        System.out.println(d2.sequenceNumber);
        System.out.println(d2.getDelay(TimeUnit.NANOSECONDS));
    }

}
