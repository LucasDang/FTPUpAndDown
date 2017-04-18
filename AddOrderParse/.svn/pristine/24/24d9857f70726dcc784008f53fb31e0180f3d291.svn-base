package com.add.utils;

import java.util.Map;

/**
 * Created by Kuajing on 2017/3/14.
 */
public class MapObject {

    public static Object mapToObject(Map<String, String> map, Class<?> beanClass) throws Exception {
        if (map == null)
            return null;

        Object obj = beanClass.newInstance();

        org.apache.commons.beanutils.BeanUtils.populate(obj, map);

        return obj;
    }

    public static Map<?, ?> objectToMap(Object obj) {
        if(obj == null)
            return null;

        return new org.apache.commons.beanutils.BeanMap(obj);
    }

}
