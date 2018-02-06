package com.example.administrator.androidpermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoufan on 2018/2/1.
 * 权限检测辅助类
 */

public class PermissionUtils {

    // 判断当前的targetSdkVersion是否大于22
    public static boolean isOverMarshmallow() {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion > 22) {
            return true;
        }
        return false;
    }

    // 当targetSdkVersion小于23的时候 通过反射直接执行方法
    public static void executeSucceedMethod(Object object, int requestCode) {
        // 获取class中多有的方法
        Method[] methods = object.getClass().getDeclaredMethods();
        // 遍历找我们打了标记的方法
        for (Method method : methods) {
            // 获取该方法上面有没有打这个成功的标记
            PermissionSucceed succeedMethod = method.getAnnotation(PermissionSucceed.class);
            if (succeedMethod != null) {
                // 代表该方法打了标记
                // 并且我们的请求码必须 requestCode 一样
                int methodCode = succeedMethod.requestCode();
                if (methodCode == requestCode) {
                    // 这个就是我们要找的成功方法
                    // 反射执行该方法
                    executeMethod(object, method);
                }
            }
        }
    }

    // 当权限申请失败的时候
    public static void executeFailMethod(Object object, int requestCode) {
        // 获取class中多有的方法
        Method[] methods = object.getClass().getDeclaredMethods();
        // 遍历找我们打了标记的方法
        for (Method method : methods) {
            // 获取该方法上面有没有打这个成功的标记
            PermissionFailed failMethod = method.getAnnotation(PermissionFailed.class);
            if (failMethod != null) {
                // 代表该方法打了标记
                // 并且我们的请求码必须 requestCode 一样
                int methodCode = failMethod.requestCode();
                if (methodCode == requestCode) {
                    // 这个就是我们要找的失败方法
                    // 反射执行该方法
                    executeMethod(object, method);
                }
            }
        }
    }

    /**
     * 反射执行该方法
     */
    private static void executeMethod(Object reflectObject, Method method) {
        // 反射执行方法  第一个是传该方法是属于哪个类   第二个参数是反射方法的参数
        try {
            method.setAccessible(true); // 允许执行私有方法
            method.invoke(reflectObject, new Object[]{});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //需要申请的权限中 获取没有授予过得权限
    public static List<String> getDeniedPermissions(Object mObject, String[] requestPermission) {
        List<String> list = new ArrayList<>();
        for (String permission : requestPermission) {
            if (ContextCompat.checkSelfPermission(getContext(mObject), permission) != PackageManager.PERMISSION_GRANTED) { // 需要
                list.add(permission);
            }
        }
        return list;
    }

    /**
     * 申请权限
     */
    public static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            return fragment.getActivity();
        }
        return null;
    }

    /**
     * 通过对象获取上下文
     *
     * @param object
     * @return
     */
    private static Context getContext(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            return fragment.getActivity();
        } else if (object instanceof View) {
            View view = (View) object;
            return view.getContext();
        }
        return null;
    }


}
