package com.example.administrator.androidpermission;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by zhoufan on 2018/2/1.
 * 权限检测
 */

public class PermissionHelper {

    private Object mObject;
    private int mRequestCode;
    private String[] mRequestPermission;

    private PermissionHelper(Object object) {
        this.mObject = object;
    }

    public static void requestPermission(Activity activity, int requestCode, String[] permissions) {
        PermissionHelper.with(activity).requestCode(requestCode).
                requestPermission(permissions).request();
    }

    public static void requestPermission(Fragment fragment, int requestCode, String[] permissions) {
        PermissionHelper.with(fragment).requestCode(requestCode).
                requestPermission(permissions).request();
    }

    public static PermissionHelper with(Activity activity) {
        return new PermissionHelper(activity);
    }

    public static PermissionHelper with(Fragment fragment) {
        return new PermissionHelper(fragment);
    }

    public PermissionHelper requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public PermissionHelper requestPermission(String... permissions) {
        this.mRequestPermission = permissions;
        return this;
    }

    public void request() {
        // 3.2 首先判断当前的版本是不是6.0 及以上
        if (!PermissionUtils.isOverMarshmallow()) {
            // 3.3 如果不是6.0以上  那么直接执行方法   反射获取执行方法
            // 执行什么方法并不确定 那么我们只能采用注解的方式给方法打一个标记，
            // 然后通过反射去执行。  注解 + 反射  执行Activity里面的callPhone
            PermissionUtils.executeSucceedMethod(mObject, mRequestCode);
            return;
        }

        // 3.3 如果是6.0以上  那么首先需要判断权限是否授予
        // 需要申请的权限中 获取没有授予过得权限
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(mObject, mRequestPermission);

        // 3.3.1 如果授予了 那么我们直接执行方法   反射获取执行方法
        if (deniedPermissions.size() == 0) {
            // 全部都是授予过的
            PermissionUtils.executeSucceedMethod(mObject, mRequestCode);
        } else {
            // 3.3.2 如果没有授予 那么我们就申请权限  申请权限
            ActivityCompat.requestPermissions(PermissionUtils.getActivity(mObject),
                    deniedPermissions.toArray(new String[deniedPermissions.size()]),
                    mRequestCode);
        }
    }

    /**
     * 处理申请权限的回调
     */
    public static void requestPermissionsResult(Object object,int requestCode, String[] permissions) {
        // 再次获取没有授予的权限
        List<String> deniedPermissions = PermissionUtils.getDeniedPermissions(object,permissions);
        if(deniedPermissions.size() == 0){
            // 权限用户都同意授予了
            PermissionUtils.executeSucceedMethod(object,requestCode);
        }else{
            // 你申请的权限中 有用户不同意的
            PermissionUtils.executeFailMethod(object,requestCode);
        }
    }
}
