package com.night.myapplication;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HotFix {
    private static final String TAG = "HotFix";
    private static HotFix instance;

    private HotFix() {
    }

    public static HotFix getInstance() {
        if (instance == null) {
            instance = new HotFix();
        }
        return instance;
    }


    public void hotInstallDexByPath(Application application, String path) {
        this.hotInstallDexByPath(application, new File(path));
    }

    public void hotInstallDexByPath(Application application, File file) {
        ClassLoader classLoader = application.getClassLoader();
        List<File> fileList = new ArrayList<>();
        if (file.exists()) {
            fileList.add(file);
        }

        File cacheDir = application.getCacheDir();
        Log.e(TAG, "cacheDir-path=" + cacheDir.getPath());
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//23
                //mumu模拟器是23
                V23.install(classLoader, fileList, cacheDir);
            } else {
                Log.e(TAG, "SDK_INI-currentVersion=" + Build.VERSION.SDK_INT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final class V23 {
        private static void install(ClassLoader loader, List<File> additionalClassPathEntries, File optimizedDirectory)
                throws IllegalAccessException, InvocationTargetException {
            //1.找到 pathList----->BaseDexClassLoader类的私有静态属性private final DexPathList pathList;
            Field pathList = ReflectUtils.findField(loader, "pathList");
            Object dexPathList = pathList.get(loader);//DexPathList类型
            //2.从 pathList找到 makePathElements 方法并执行
            List<IOException> suppressedExceptions = new ArrayList<>();
            //这个是我们手动生成的fixedexclassElements
            Object[] fixedexclassElements = makePathElements(dexPathList, additionalClassPathEntries, optimizedDirectory, suppressedExceptions);
            //3.需要将fixedexclassElements添加的BaseDexClassLoader类的private final DexPathList pathList;->DexPathList类中的private final Element[] dexElements;
            ReflectUtils.expandFieldArray(dexPathList, "dexElements", fixedexclassElements);
            //类加载器自动执行：4.classLoader->findClass()方法执行，会遍历pathList这里面的dex元素，找到fix的class文件，从而达到热修复

            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makePathElement", e);
                }
            }
        }

        /**
         * 将dex转为dexElements，使用DexPathList里的makePathElements方法具体如下
         * makePathElements( List<File> files, File optimizedDirectory, List<IOException> suppressedExceptions)
         * 通过反射方法执行
         */
        private static Object[] makePathElements(Object dexPathList, List<File> files, File optimizedDirectory,
                                                 List<IOException> suppressedExceptions) throws InvocationTargetException,
                IllegalAccessException {
            Method makePathElements = ReflectUtils.findMethod(dexPathList,
                    "makePathElements",
                    List.class,
                    File.class,
                    List.class);
            return (Object[]) makePathElements.invoke(dexPathList, files, optimizedDirectory, suppressedExceptions);
        }
    }

}
