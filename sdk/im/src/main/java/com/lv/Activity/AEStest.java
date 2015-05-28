package com.lv.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.lv.R;
import com.lv.Utils.CryptUtils;

/**
 * Created by q on 2015/4/18.
 */
public class AEStest extends Activity {
    TextView result;
    TextView aes;
    private static final String KEY16="wewew12123gjyrs5";
    private static final String KEY32="wewew12123gjyrs5wewew12123gjyrs5";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ase_layout);
        aes = (TextView) findViewById(R.id.aes_tv);
        result = (TextView) findViewById(R.id.result_tv);
        try {
            String after = CryptUtils.encrypt("haha", KEY32);
            result.setText("加密后："+after);
            String encrypt=CryptUtils.desEncrypt(after,KEY32);
            aes.setText("解密后："+encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
