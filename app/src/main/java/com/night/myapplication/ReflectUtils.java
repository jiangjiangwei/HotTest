package com.night.myapplication;

import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectUtils {
    private static final String TAG = "ReflectUtils";


    /**
     * 从 instance 到其父类 找 name 属性
     *
     * @param instance
     * @param name
     * @return
     */
    public static Field findField(Object instance, String name) {
//        for (Class<?> aClass = instance.getClass(); aClass != null; aClass = aClass.getSuperclass()) {
//            try {
//                //class dalvik.system.PathClassLoader
//                //class dalvik.system.BaseDexClassLoader
//                Log.e(TAG,"aClass="+aClass);
//                Field declaredField = aClass.getDeclaredField(name);
//                Log.e(TAG, "declaredField=" + declaredField);
//                if (!declaredField.isAccessible()) {
//                    declaredField.setAccessible(true);
//                }
//                return declaredField;
//            } catch (Exception e) {
//
//            }
//        }
//        return null;
        Field fieldByClazz = findFieldByClazz(instance.getClass(), name);
        Log.e(TAG, "fieldByClazz=" + fieldByClazz);
        return fieldByClazz;
    }

    public static Field findFieldByClazz(Class clazz, String name) {
        if (clazz != null) {
            Class<?> aClass = clazz;
            try {
                Field declaredField = aClass.getDeclaredField(name);
                Log.e(TAG, "declaredField=" + declaredField);
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                Log.e(TAG, "declaredField=" + 1111);
                return declaredField;
            } catch (Exception e) {
                //递归继续查找父类
                Log.e(TAG, "declaredField=" + 2222);
//                e.printStackTrace();
            }
        }
        return clazz == null ? null : findFieldByClazz(clazz.getSuperclass(), name);
    }


    public static Method findMethod(Object instance, String methodName, Class<?>... parameterType) {
        if (instance != null) {
            return findMethodByClazz(instance.getClass(), methodName, parameterType);
        }
        return null;
    }

    public static Method findMethodByClazz(Class clazz, String methodName, Class<?>... parameterType) {
        if (clazz != null) {
            Class<?> aClass = clazz;
            try {
                Method declaredMethod = aClass.getDeclaredMethod(methodName, parameterType);
                if (!declaredMethod.isAccessible()) {
                    declaredMethod.setAccessible(true);
                }
                return declaredMethod;
            } catch (NoSuchMethodException e) {
                //递归继续查找父类
                findMethodByClazz(aClass.getSuperclass(), methodName);
            }
        }
        return clazz == null ? null : findMethodByClazz(clazz.getSuperclass(), methodName);
    }


    /**
     * 数组扩容,拷贝
     */
    public static void expandFieldArray(Object instance, String fieldName, Object[] fixedElements) throws IllegalAccessException {
        //拿到 classloader中的dexelements 数组
        Field dexElementsField = findField(instance, fieldName);
        //DexPathList类中的private final Element[] dexElements;
        Object[] dexElements = (Object[]) dexElementsField.get(instance);

        //合并数组
        Object[] newElements = (Object[]) Array.newInstance(dexElements.getClass().getComponentType(),
                dexElements.length + fixedElements.length);
        //拷贝新数组
        System.arraycopy(fixedElements, 0, newElements, 0, fixedElements.length);
        System.arraycopy(dexElements, 0, newElements, fixedElements.length, dexElements.length);

        //修改 classLoader中 pathList的 dexelements
        dexElementsField.set(instance, newElements);
    }
}
