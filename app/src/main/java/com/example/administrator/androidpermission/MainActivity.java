package com.example.administrator.androidpermission;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * android 6.0以上的权限管理
 */
public class MainActivity extends AppCompatActivity {

    // 打电话权限申请的请求码
    private static final int CALL_PHONE_REQUEST_CODE = 0x0011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void phoneClick(View view) {
        PermissionHelper.with(this).requestCode(CALL_PHONE_REQUEST_CODE)
                .requestPermission(Manifest.permission.CALL_PHONE).request();
    }

    @PermissionSucceed(requestCode = CALL_PHONE_REQUEST_CODE)
    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:13701659446");
        intent.setData(data);
        startActivity(intent);
    }

    @PermissionFailed(requestCode = CALL_PHONE_REQUEST_CODE)
    private void callPhoneFail() {
        Toast.makeText(this, "您拒绝了拨打电话",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionHelper.requestPermissionsResult(this,
                CALL_PHONE_REQUEST_CODE, permissions);
    }
}
