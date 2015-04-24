package com.lv.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lv.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by q on 2015/4/18.
 */
public class LoginActivity extends Activity implements View.OnClickListener{
    private EditText login_username;
    private EditText login_password;
    private Button user_login_button;
    private Button user_register_button;
    private ProgressDialog dialog;
    private static final String url="http://hedy.zephyre.me/users/login";
    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Intent intent =new Intent();
                    intent.setClass(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initview();
    }
    private void initview() {
        login_username=(EditText)findViewById(R.id.login_username);
        login_password=(EditText)findViewById(R.id.login_password);
        user_login_button=(Button)findViewById(R.id.user_login_button);
        user_register_button=(Button)findViewById(R.id.user_register_button);
        user_login_button.setOnClickListener(this);
        user_register_button.setOnClickListener(this);
        login_username.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String username=login_username.getText().toString().trim();
                    if(username.length()<4){
                        Toast.makeText(LoginActivity.this, "用户名不能小于4个字符", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        login_password.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String password=login_password.getText().toString().trim();
                    if(password.length()<4){
                        Toast.makeText(LoginActivity.this, "密码不能小于4个字符", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }
    @Override
    public void onClick(View v) {
        Intent intent =new Intent();
        switch (v.getId()){
            case R.id.user_login_button:
                intent.setClass(LoginActivity.this,MainActivity.class);
                //getApplicationContext().
                // SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                //String qwe=sharedPreferences.getString("username","");
                login(login_username.getText().toString());
                // System.out.println("username:"+qwe);
                break;
            case R.id.user_register_button:
                break;
            default:
                break;
        }

    }
    private void login(final String username) {
        new  Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject obj =new JSONObject();
                JSONArray array=new JSONArray();
                try {
                    obj.put("userId",Long.parseLong(username));
                    obj.put("regId","45c10d381af0f4998032d933a00f3c6e");
                    System.out.println("login:"+array.toString());
                    HttpPost post = new HttpPost(url);
                    HttpResponse httpResponse = null;
                        StringEntity entity = new StringEntity(obj.toString(),
                                HTTP.UTF_8);
                        entity.setContentType("application/json");
                        post.setEntity(entity);
                        httpResponse = new DefaultHttpClient().execute(post);
                    final int code=httpResponse.getStatusLine().getStatusCode();
                   System.out.println("Status code:"+code);
                    if (code==200){
                        System.out.println("登陆成功！");
                        handler.sendEmptyMessage(1);
                    }
                    else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this,"登陆失败："+code,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}