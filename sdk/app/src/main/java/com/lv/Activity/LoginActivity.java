package com.lv.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lv.R;
import com.lv.Utils.CommonUtils;
import com.lv.Utils.Config;
import com.lv.im.IMClient;
import com.lv.user.LoginSuccessListener;
import com.lv.user.User;

/**
 * Created by q on 2015/4/18.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText login_username;
    private EditText login_password;
    private Button user_login_button;
    private Button user_register_button;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_layout);
        IMClient.getInstance().init(this);
        initview();
    }

    private void initview() {
        login_username = (EditText) findViewById(R.id.login_username);
        login_password = (EditText) findViewById(R.id.login_password);
        user_login_button = (Button) findViewById(R.id.user_login_button);
        user_register_button = (Button) findViewById(R.id.user_register_button);
        user_login_button.setOnClickListener(this);
        user_register_button.setOnClickListener(this);
        login_username.setOnFocusChangeListener((v, hasFocus) -> {
            {
                if (!hasFocus) {
                    String username = login_username.getText().toString().trim();
                    if (username.length() < 4) {
                        Toast.makeText(LoginActivity.this, "用户名不能小于4个字符", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        login_password.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = login_password.getText().toString().trim();
                if (password.length() < 4) {
                    Toast.makeText(LoginActivity.this, "密码不能小于4个字符", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_login_button:
                if (CommonUtils.isFastDoubleClick()) return;
                System.out.println("click");
                dialog = ProgressDialog.show(LoginActivity.this, "", "");
                User.login(login_username.getText().toString(), new LoginSuccessListener() {
                    @Override
                    public void OnSuccess() {
                        dialog.dismiss();
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "登陆成功 ");
                        }
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }

                    @Override
                    public void OnFailed(int code) {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "登陆失败：" + code, Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.user_register_button:
                break;
            default:
                break;
        }
    }
}
