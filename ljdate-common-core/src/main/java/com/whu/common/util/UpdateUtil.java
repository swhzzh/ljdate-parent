package com.whu.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class UpdateUtil {

    /**
     * 将目标实体中为空的null属性从源实体类复制到目标类实体中
     *
     * @param src 源实体
     * @param target 目标实体
     */
    public static void copyProperties(Object src, Object target){
        BeanUtils.copyProperties(src, target, getNullProperties(target));
    }


    /**
     * 寻找非空属性
     *
     * @param src
     * @return
     */
    private static String[] getNullProperties(Object src){
        BeanWrapper beanWrapper = new BeanWrapperImpl(src);
        PropertyDescriptor[] descriptors = beanWrapper.getPropertyDescriptors();

        Set<String> nonEmptyName = new HashSet<>();
        for (PropertyDescriptor descriptor : descriptors) {
            Object value = beanWrapper.getPropertyValue(descriptor.getName());
            if (value != null){
                nonEmptyName.add(descriptor.getName());
            }
        }
        String[] result = new String[nonEmptyName.size()];
        return nonEmptyName.toArray(result);
    }
}
