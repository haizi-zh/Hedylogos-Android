package com.lv.Audio;

import android.media.MediaRecorder;
import android.os.Environment;

import com.lv.Utils.TimeUtils;

import java.io.File;

public class AudioFileFunc {
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;

    public final static int AUDIO_SAMPLE_RATE = 44100; // 44.1KHz,普遍使用的频率
    // 录音输出文件
    //private final static String AUDIO_RAW_FILENAME = "RawAudio.raw";
    //private final static String AUDIO_WAV_FILENAME = "FinalAudio.wav";
    public final static String AUDIO_AMR_FILENAME = "Audio.amr";

    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static String getAMRFilePath() {
        File sd=Environment.getExternalStorageDirectory();
        String path=sd.getPath()+"/SDK/audio";
        File file=new File(path);
        if(!file.exists())
            file.mkdir();
        String mAudioAMRPath = "";
        if (isSdcardExit()) {
            String fileBasePath = Environment.getExternalStorageDirectory()
                    .toString();
            mAudioAMRPath = fileBasePath + "/SDK/audio/" + TimeUtils.getTimestamp()+"_"+AUDIO_AMR_FILENAME;
        }
        return mAudioAMRPath;
    }

    public static long getFileSize(String path) {
        File mFile = new File(path);
        if (!mFile.exists())
            return -1;
        return mFile.length();
    }
}
