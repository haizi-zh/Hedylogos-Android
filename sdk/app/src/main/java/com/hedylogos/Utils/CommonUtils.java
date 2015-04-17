package com.hedylogos.Utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by q on 2015/4/17.
 */
public class CommonUtils {
    private Vibrator vibrator;

    public static void Phonevibrator(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 10, 100, 1000};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);
        // vibrator.cancel();
    }
}
