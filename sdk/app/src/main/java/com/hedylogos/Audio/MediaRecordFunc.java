package com.hedylogos.Audio;

import android.media.AudioFormat;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

public class MediaRecordFunc {

	private static MediaRecordFunc mInstance;
	private boolean isRecord = false;

	private MediaRecorder mMediaRecorder;

	private MediaRecordFunc() {
	}

	public synchronized static MediaRecordFunc getInstance() {
		if (mInstance == null)
			mInstance = new MediaRecordFunc();
		return mInstance;
	}

	public int startRecordAndFile() {
		// 判断是否有外部存储设备sdcard
		if (AudioFileFunc.isSdcardExit()) {
			if (isRecord) {
				return 1002;
			} else {
				if (mMediaRecorder == null)
					createMediaRecord();

				try {
					mMediaRecorder.prepare();
					mMediaRecorder.start();
					// 让录制状态为true
					isRecord = true;
					return 1000;
				} catch (IOException ex) {
					ex.printStackTrace();
					return 1004;
				}
			}

		} else {
			return 1003;
		}
	}

	private void createMediaRecord() {
		mMediaRecorder = new MediaRecorder();

		/* setAudioSource/setVedioSource */
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风

		/*
		 * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
		 * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
		 */
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);

		/* 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		//mMediaRecorder.setAudioChannels(AudioFormat.CHANNEL_IN_MONO);
		mMediaRecorder.setAudioSamplingRate(8000);
		mMediaRecorder.setAudioEncodingBitRate(AudioFormat.ENCODING_PCM_8BIT);
		/* 设置输出文件的路径 */
		File file = new File(AudioFileFunc.getAMRFilePath());
		if (file.exists()) {
			file.delete();
		}
		mMediaRecorder.setOutputFile(AudioFileFunc.getAMRFilePath());
	}

	public void stopRecordAndFile() {
		close();
	}

	public long getRecordFileSize() {
		return AudioFileFunc.getFileSize(AudioFileFunc.getAMRFilePath());
	}

	private void close() {
		if (mMediaRecorder != null) {
			System.out.println("stopRecord");
			isRecord = false;
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}
}
