package com.hedylogos.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by q on 2015/4/14.
 */
public class PictureUtil {
    public static final String IMAGE_UNSPECIFIED = "image/*";
    private static int maxWidth = 720;
    private static int maxHeight = 1080;
    /**
     * 将图片存储至SD卡，需判断是否装有SD卡、是否可读写、是否有空间，否则提示出错
     *
     * @param ctx      上下文
     * @param bitmap   要存储的照片
     * @param quality  压缩照片的质量，0至100，100最佳，一般80-90
     * @param filePath 存储的路径
     * @param filename 照片的名称
     * @return
     */
    public static boolean save_picture(Context ctx, Bitmap bitmap, int quality, String filePath, String filename) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] data = baos.toByteArray();
/*
        if (!Common.checkSDStatus(data.length/1024/1024)) {
            Toast.makeText(ctx, "您的储存卡有错误", Toast.LENGTH_SHORT).show();
            return false;
        }
*/
        try {
            File destDir = new File(filePath);
            if (!destDir.exists())
                destDir.mkdirs();

            String path = filePath + "/" + filename;
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 获得圆角图片的方法
     *
     * @param bitmap  需处理的图片
     * @param roundPx 圆角的弧率
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(output, rect, rect, paint);

        return output;
    }

    /**
     * 图片中绘入GPS和时间等文字
     *
     * @param bitmap   需处理的图片
     * @param datetime 时间
     * @param lat      经度
     * @param lng      纬度
     * @return
     */
    public static Bitmap getGpsBitmap(Bitmap bitmap, String datetime, String lat, String lng) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        /* 把位图写进画布canvas类 */
        Canvas canvas = new Canvas(output);
        /* 画布的区域 */
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        /* 喷漆Paint类 */
        Paint paint = new Paint();
        paint.setAntiAlias(true);//消除锯齿
        paint.setColor(Color.RED);//着色
        paint.setTextSize(16);//字体大小
        canvas.drawText("经度:" + lng, 10, 20, paint);
        canvas.drawText("纬度:" + lat, 10, 38, paint);
        canvas.drawText("时间:" + datetime, 10, 56, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 裁切图片
     *
     * @param originFile 源文件
     * @param TargetFile 目标文件
     * @param aspect     宽高比例，如果为null，则不限制
     * @param output     输出分辨率
     * @return
     */
    public static Intent startPhotoZoom(File originFile, File TargetFile, int[] aspect, int[] output) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(originFile), IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);

        if (null != output) {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(originFile.getPath(), op);

            int jpgWidth = op.outWidth;
            int jpgHeight = op.outHeight;

            if (jpgWidth > output[0] && jpgHeight > output[1]) {
                intent.putExtra("outputX", output[0]);
                intent.putExtra("outputY", output[1]);
            }
        }

        if (null != aspect) {
            intent.putExtra("aspectX", aspect[0]);
            intent.putExtra("aspectY", aspect[1]);
        }

        if (!TargetFile.exists())
            try {
                TargetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(TargetFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        return intent;
    }

    /**
     * 从相册中选择一张照片之后，获取该照片的绝对路径
     *
     * @param ctx
     * @param photoUri
     * @return
     */
    public static String getPickPhotoPath(Context ctx, Uri photoUri) {
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(photoUri, null, null, null, null);
            cursor.moveToFirst();
            String imgPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

            return imgPath;
        } catch (Exception e) {
            return "";
        } finally {
            cursor.close();
        }
    }

    public static File getPickPhotoFile(Context ctx, Uri photoUri) {
        String imgPath = getPickPhotoPath(ctx, photoUri);
        if (!TextUtils.isEmpty(imgPath))
            return new File(imgPath);
        else
            return null;
    }

    /**
     * 压缩图片大小，避免图片过大，保持比例不变，宽或高不超过XX个像素
     * @param newName    新的文件名称
     * @param filePath   原文件全路径，包含文件名
     * @param attachPath 处理过后，文件存放的位置
     * @param attachPath 新的文件全路径
     */
    public static String compressPixelPhotos(final Context ctx, final String newName, final String filePath,
                                             final String attachPath,int simplesize) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, op);

        int jpgWidth = op.outWidth;
        int jpgHeight = op.outHeight;

        if (jpgWidth > simplesize || jpgHeight > simplesize) {
            int wSendRatio = (int) Math.ceil(jpgWidth / (float)simplesize);
            int hSendRatio = (int) Math.ceil(jpgHeight / (float)simplesize);
            if (wSendRatio > 1 && hSendRatio > 1) {
                op.inSampleSize = wSendRatio > hSendRatio ? wSendRatio : hSendRatio;
            }
            op.inJustDecodeBounds = false;
            Bitmap b = BitmapFactory.decodeFile(filePath, op);

            if (!save_picture(ctx, b, 90, attachPath, newName)) {
                copyFile(filePath, attachPath + File.separator + newName);
            }

            if (b != null && !b.isRecycled())
                b.recycle();

        } else {
            copyFile(filePath, attachPath + File.separator + newName);
        }

        return attachPath + File.separator + newName;
    }

    /**
     * 检查图片分辨率大小，是否需要压缩
     *
     * @param ctx
     * @param filePath
     * @return
     */
    public static boolean compressPixelPhotosCheck(final Context ctx, final String filePath,int simplesize) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, op);

        if (op.outWidth > simplesize || op.outHeight > simplesize) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param filename     文件名，全路径
     * @param jpgGetWidth  照片宽
     * @param jpgGetHeight 照片高
     * @return
     */
    public static Bitmap decodeFile(String filename, int jpgGetWidth, int jpgGetHeight) {
        Bitmap b = null;
        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, op);

            int jpgWidth = op.outWidth;
            int jpgHeight = op.outHeight;

            int wSendRatio = (int) Math.ceil(jpgWidth / Double.valueOf(jpgGetWidth));
            int hSendRatio = (int) Math.ceil(jpgHeight / Double.valueOf(jpgGetHeight));
            if (wSendRatio > 1 && hSendRatio > 1) {
                op.inSampleSize = wSendRatio > hSendRatio ? wSendRatio : hSendRatio;
            }
            op.inJustDecodeBounds = false;
            b = BitmapFactory.decodeFile(filename, op);

        } catch (Exception e) {
        }
        return b;
    }
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (!oldfile.exists()) { //文件不存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }
    public  static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }
    public static boolean saveBitmapToJpegFile(Bitmap bitmap, String filePath, int quality) {
        try {
            FileOutputStream fileOutStr = new FileOutputStream(filePath);
            BufferedOutputStream bufOutStr = new BufferedOutputStream(fileOutStr);
            resizeBitmap(bitmap).compress(Bitmap.CompressFormat.JPEG, quality, bufOutStr);
            bufOutStr.flush();
            bufOutStr.close();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }
    public static Bitmap resizeBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > maxWidth) {
                int pWidth = maxWidth;
                int pHeight = maxWidth * height / width;
                Bitmap result = Bitmap.createScaledBitmap(bitmap, pWidth, pHeight, false);
                bitmap.recycle();
                return result;
            }
            if (height > maxHeight) {
                int pHeight = maxHeight;
                int pWidth = maxHeight * width / height;
                Bitmap result = Bitmap.createScaledBitmap(bitmap, pWidth, pHeight, false);
                bitmap.recycle();
                return result;
            }
        }
        return bitmap;
    }

}
