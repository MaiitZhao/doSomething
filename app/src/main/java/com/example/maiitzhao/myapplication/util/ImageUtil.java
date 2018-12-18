package com.example.maiitzhao.myapplication.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mahuihuang on 15/4/7.
 */
public class ImageUtil {

    public static final String TAG = ImageUtil.class.getSimpleName();


    /***************************************BLUR START****************************************/
    /**
     * 模糊处理图片，默认先缩小（默认缩放为原图的1/8），再模糊处理再放大（高效率）
     *
     * @param bm
     * @param view
     * @return
     */
    public static Bitmap fastBlur(Bitmap bm, View view) {
        return fastBlur(bm, view, true, 8);
    }

    /**
     * 模糊处理后，为view设置为background，默认先缩小（默认缩放为原图的1/8），再模糊处理再放大（高效率）
     *
     * @param context
     * @param bm
     * @param view
     */
    public static void fastBlurSetBg(Context context, Bitmap bm, View view) {
        fastBlurSetBg(context, bm, view, true, 8);
    }

    /**
     * 模糊处理图片
     *
     * @param bm
     * @param view
     * @param downScale
     * @param scaleF
     * @return
     */
    public static Bitmap fastBlur(Bitmap bm, View view, boolean downScale, float scaleF) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 1;
        float radius = 20;
        if (downScale) {
            scaleFactor = scaleF;
            radius = 2;
        }

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        // 处理bitmap缩放的时候，就可以达到双缓冲的效果，模糊处理的过程就更加顺畅了。
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bm, 0, 0, paint);

        overlay = doBlur(overlay, (int) radius, true);
        Log.d(TAG, "fast blur takes: " + (System.currentTimeMillis() - startMs + "ms"));
        return overlay;

    }

    public static Bitmap fastBlur(Bitmap bm, float scaleFactor, float radius, int width, int height) {
        long startMs = System.currentTimeMillis();
        scaleFactor = scaleFactor > 0 ? scaleFactor : 1;
        radius = radius > 0 ? radius : 20;

        Bitmap overlay = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
//        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        // 处理bitmap缩放的时候，就可以达到双缓冲的效果，模糊处理的过程就更加顺畅了。
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bm, 0, 0, paint);

        overlay = doBlur(overlay, (int) radius, true);
        Log.d(TAG, "fast blur takes: " + (System.currentTimeMillis() - startMs + "ms"));
        return overlay;
    }

    public static Bitmap fastBlur(Bitmap bm, float scaleFactor, float radius) {
        return fastBlur(bm, scaleFactor, radius, bm.getWidth(), bm.getHeight());
    }

    /**
     * 模糊处理后，为view设置为background
     *
     * @param context
     * @param bm
     * @param view
     * @param downScale
     * @param scaleFactor
     */
    @TargetApi(Build.VERSION_CODES.DONUT)
    public static void fastBlurSetBg(Context context, Bitmap bm, View view, boolean downScale, float scaleFactor) {
        Bitmap overlay = fastBlur(bm, view, downScale, scaleFactor);
        if (null == overlay) {
            Log.e(TAG, "fast blur error(result[overlay] is null)");
            return;
        }
        view.setBackgroundDrawable(new BitmapDrawable(context.getResources(), overlay));
    }
    /***************************************BLUR END****************************************/


    /***************************************图片压缩计算 BEGIN****************************************/
    /**
     * 把bitmap转换成String
     *
     * @param filePath
     * @return
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static String bitmapToString(String filePath, int w, int h) {

        Bitmap bm = getSmallBitmap(filePath, w, h);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        String str = Base64.encodeToString(b, Base64.DEFAULT);
        IOUtil.recycleBitmap(bm);
        return str;

    }

    /**
     * 计算图片的缩放值
     *
     * @param o
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options o,
                                            int reqWidth, int reqHeight) {

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp <= reqWidth || height_tmp <= reqHeight) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;

        }

        return scale;
    }


    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param
     * @return
     */

    public static Bitmap getSmallBitmap(String filePath, int w, int h) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, w, h);
//        options.inSampleSize = computeSampleSize(options, -1, w * h);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

//        Bitmap b = BitmapFactory.decodeFile(filePath, options);

//        OperatePic.zoomBitmap(b, w, h);
        Bitmap resultBm = null;
        try {
            Bitmap proBm = decodeFile(filePath, options);
            if (null == proBm) {
                return null;
            }
            resultBm = formatCameraPictureOriginal(filePath, proBm);
        } catch (Throwable ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return resultBm;
    }

    public static Bitmap getSmallBitmapZoom(String filePath, int w, int h) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, w, h);
////        options.inSampleSize = computeSampleSize(options, -1, w * h);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//
//        Bitmap b = BitmapFactory.decodeFile(filePath, options);
        Bitmap smallBm = getSmallBitmap(filePath, w, h);
        Bitmap zoomBm = zoomBitmap(smallBm, w, h);
        IOUtil.recycleBitmap(smallBm);
        return zoomBm;
    }

    public static Bitmap getSmallBitmapQuality(String filePath, int w, int h, int quality) {
        Bitmap bm = getSmallBitmap(filePath, w, h);

        if (null == bm || quality >= 100 || quality <= 0) {
            return bm;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        IOUtil.recycleBitmap(bm);
        return bitmap;
    }


    /**
     * 压缩到指定大小容量
     *
     * @param image
     * @param size
     * @return
     */
    public static ByteArrayInputStream compressImage(Bitmap image, int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > size) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return isBm;
    }

    public static void compressImage2SD(File file, String srcPath, float ww, float hh, int size) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        decodeFile(srcPath, newOpts);//此时返回bm为空

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 800f;//这里设置高度为800f
//        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;
        Bitmap bitmap = decodeFile(srcPath, newOpts);
        if (null == bitmap) {
            return;
        }

        bitmap = formatCameraPictureOriginal(srcPath, bitmap); // 保证图片方向正常

        ByteArrayInputStream isBm = compressImage(bitmap, size);//把压缩后的数据baos存放到ByteArrayInputStream中
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] buffer = new byte[isBm.available()];
            isBm.read(buffer);
            os.write(buffer, 0, buffer.length);
            os.flush();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            IOUtil.closeIO(isBm, os);
            IOUtil.recycleBitmap(bitmap);
        }

    }

    public static void compressImage2SD(File file, String srcPath, int size) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        decodeFile(srcPath, newOpts);//此时返回bm为空
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 800f;//这里设置高度为800f
//        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;
        Bitmap bitmap = decodeFile(srcPath, newOpts);
        if (null == bitmap) {
            return;
        }

        bitmap = formatCameraPictureOriginal(srcPath, bitmap); // 保证图片方向正常

        ByteArrayInputStream isBm = compressImage(bitmap, size);//把压缩后的数据baos存放到ByteArrayInputStream中
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] buffer = new byte[isBm.available()];
            isBm.read(buffer);
            os.write(buffer, 0, buffer.length);
            os.flush();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            IOUtil.closeIO(isBm, os);
            IOUtil.recycleBitmap(bitmap);
        }

    }

    /**
     * 把图片数组压缩一下放在文件中
     *
     * @param bms
     * @param srcPath
     * @param ww
     * @param hh
     * @param size
     */
    public static void compressImage2SD(byte[] bms, String srcPath, float ww, float hh, int size) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        decodeByte(bms, newOpts);//此时返回bm为空

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 800f;//这里设置高度为800f
//        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;
        Bitmap bitmap = decodeByte(bms, newOpts);
        if (null == bitmap) {
            return;
        }

        //bitmap = formatCameraPictureOriginal(srcPath, bitmap); // 保证图片方向正常

        ByteArrayInputStream isBm = compressImage(bitmap, size);//把压缩后的数据baos存放到ByteArrayInputStream中
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(srcPath));
            byte[] buffer = new byte[isBm.available()];
            isBm.read(buffer);
            os.write(buffer, 0, buffer.length);
            os.flush();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            IOUtil.closeIO(isBm, os);
            IOUtil.recycleBitmap(bitmap);
        }

    }

    /**
     * Compress image
     *
     * @param filePath
     * @param ww
     * @param hh
     * @param size
     * @return
     */
    public static InputStream compressImage(String filePath, float ww, float hh, int size) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        decodeFile(filePath, newOpts);//此时返回bm为空

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 800f;//这里设置高度为800f
//        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;

        Bitmap bitmap = decodeFile(filePath, newOpts);
        if (null == bitmap) {
            return null;
        }
        InputStream resultIs = compressImage(bitmap, size);
        IOUtil.recycleBitmap(bitmap);
        return resultIs;
    }


    public static Bitmap getCompressedImage(String srcPath, float ww, float hh, int size) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        decodeFile(srcPath, newOpts);//此时返回bm为空

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 800f;//这里设置高度为800f
//        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;

        Bitmap bitmap = null;
        ByteArrayInputStream commpressedBm = null;
        Bitmap resultBm = null;
        try {
            bitmap = decodeFile(srcPath, newOpts);
            if (null == bitmap) {
                return null;
            }
            commpressedBm = compressImage(bitmap, size);
            resultBm = BitmapFactory.decodeStream(
                    commpressedBm, // 缩小到指定容量
                    null, null);//把ByteArrayInputStream数据生成图片
        } finally {
            IOUtil.recycleBitmap(bitmap);
            IOUtil.closeIO(commpressedBm);
        }
        return resultBm;
    }

    /***************************************图片压缩计算 END****************************************/
    /***********************************传入bitmap 按比例大小以及质量压缩**********************************/
    //图片按比例大小压缩
    public static Bitmap compBitmapByScale(Bitmap image, int screenWidth, int realHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//不生成bitmap
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        //图片宽高
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        //屏幕宽高
        float width = 480 * realHeight / screenWidth;//高度不定的情况
        float height = 480f;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > width) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (w / width);
        } else if (w < h && h > height) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (h / height);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;//设置缩放比例
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            isBm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressBitmapByQuality(bitmap);//压缩好比例大小后再进行质量压缩
    }

    //传入bitmap进行质量压缩
    public static Bitmap compressBitmapByQuality(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片

        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            isBm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /***************************************圆角/投影羽化/倒影 BEGIN****************************************/
    /**
     * 对图片进行圆角处理
     *
     * @param bitmap  要处理的Bitmap对象
     * @param roundPx 圆角半径设置
     * @return Bitmap对象
     * @author com.tiantian
     */
    public static Bitmap roundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 图片加投影 羽化效果
     *
     * @param originalBitmap
     * @return
     */
    public static Bitmap shadowBitmap(Bitmap originalBitmap) {
//		Bitmap originalBitmap = drawableToBitmap(drawable);
        BlurMaskFilter blurFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);

		/*
         * int[] offsetXY = new int[2]; Bitmap shadowImage =
		 * originalBitmap.extractAlpha(shadowPaint, offsetXY);
		 *
		 * Bitmap bmp=shadowImage.copy(Config.ARGB_8888, true); Canvas c = new
		 * Canvas(bmp); c.drawBitmap(originalBitmap, -offsetXY[0], -offsetXY[1],
		 * null); return shadowImage;
		 */
        final int w = originalBitmap.getWidth();
        final int h = originalBitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w + 20, h + 20, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawBitmap(originalBitmap, 10, 10, shadowPaint);
        c.drawBitmap(originalBitmap, 10, 10, null);
        return bmp;
    }

    /**
     * 对图片进行倒影处理
     *
     * @param bitmap
     * @return
     * @author com.tiantian
     */
    public static Bitmap reflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
                h / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }


    /***************************************圆角/投影羽化/倒影 END****************************************/

    /***********************************图片基本处理（缩放/转换）BEGIN*************************************/

    /**
     * 放缩图片处理
     *
     * @param bitmap 要放缩的Bitmap对象
     * @param width  放缩后的宽度
     * @param height 放缩后的高度
     * @return 放缩后的Bitmap对象
     * @author com.tiantian
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * 放缩图片处理
     *
     * @param bitmap      要放缩的Bitmap对象
     * @param widthScale  放缩率
     * @param heightScale 放缩率
     * @return 放缩后的Bitmap对象
     * @author com.tiantian
     */
    public static Bitmap zoomBitmapScale(Bitmap bitmap, float widthScale, float heightScale) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = widthScale;
        float scaleHeight = heightScale;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * Drawable缩放
     *
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // drawable转换成bitmap
        Bitmap oldbmp = drawable2Bitmap(drawable);
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        IOUtil.recycleBitmap(oldbmp);
        return new BitmapDrawable(newbmp);
    }

    /**
     * 将Bitmap转化为Drawable
     *
     * @param bitmap
     * @return
     * @author com.tiantian
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     * @author com.tiantian
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /***********************************图片基本处理（缩放/转换）BEGIN*************************************/


    /***********************************图片基本基本信息 BEGIN*************************************/

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static int readPictureDegreeFromExif(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImage(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        IOUtil.recycleBitmap(bitmap);
        return resizedBitmap;
    }

    /**
     * 处理相机照片旋转角度
     *
     * @param path 用于获取原图的信息
     * @return 原图的bitmap（可以是被压缩过的）
     */
    public static Bitmap formatCameraPictureOriginal(String path, Bitmap bitmap) {
        /**
         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
         */
        int degree = ImageUtil.readPictureDegreeFromExif(path);
        if (0 == degree) {
            return bitmap;
        }
        /**
         * 把图片旋转为正的方向
         */
        Bitmap newbitmap = rotaingImage(degree, bitmap);
        IOUtil.recycleBitmap(bitmap);
        return newbitmap;
    }

    /**
     * 处理相机照片旋转角度
     *
     * @param path
     * @return
     */
    public static Bitmap formatCameraPicture(String path) {
        /**
         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
         */
        int degree = ImageUtil.readPictureDegreeFromExif(path);
        BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
        opts.inSampleSize = 2;
        Bitmap cbitmap = decodeFile(path, opts);
        if (null == cbitmap) {
            return null;
        }
        /**
         * 把图片旋转为正的方向
         */
        Bitmap newbitmap = rotaingImage(degree, cbitmap);
        IOUtil.recycleBitmap(cbitmap);
        return newbitmap;
    }

    /**
     * 压缩图片、旋转后保存到指定路径
     *
     * @param data         要保存的字节数组
     * @param filepath     保存的路径
     * @param width,height 图片宽高
     * @param size,angle   压缩的大小，图片旋转的角度
     */

    public static void compressedRotateSavePath(byte[] data, String filepath, float width, float height, int size, int
            angle) {
        ImageUtil.compressImage2SD(data, filepath, width, height, size);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap oldBitmap = ImageUtil.decodeFile(filepath, opts);
        Bitmap newBitmap = ImageUtil.rotaingImage(angle, oldBitmap);
        byte[] newData = ImageUtil.bmpToByteArray(newBitmap, true);
        saveBitmapWityBytes(filepath, newData);
        IOUtil.recycleBitmap(newBitmap, oldBitmap);
    }

    /***********************************图片基本基本信息 END*************************************/


    /***********************************图片初始化 BEGIN*************************************/
    /**
     * 从Resource中获取Drawable，并初始化bound
     *
     * @param context
     * @param drawableResId
     * @param bound
     * @return
     */
    public static Drawable getResourceDrawableBounded(Context context, int drawableResId, int bound) {
        Drawable drawable = null;
        try {
            drawable = context.getResources().getDrawable(drawableResId);
            drawable.setBounds(0, 0, bound, bound);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return drawable;
    }

    /**
     * ********************************图片初始化 END************************************
     */

    public static Bitmap decodeFile(String pathName, BitmapFactory.Options options) {
        Bitmap resultBm = null;
        try {
            resultBm = BitmapFactory.decodeFile(pathName, options);
        } catch (Throwable throwable) {
            Log.e(TAG, throwable.getMessage(), throwable);
        }
        return resultBm;
    }

    private static Bitmap decodeByte(byte[] data, BitmapFactory.Options options) {
        Bitmap resultBm = null;
        try {
            resultBm = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (Throwable throwable) {
            Log.e(TAG, throwable.getMessage(), throwable);
        }
        return resultBm;
    }


    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }


    /**
     * 将bitmap转换成byte数组
     *
     * @param bmp
     * @param needRecycle
     * @return
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public static int saveBitmapWityBytes(String filePath, byte[] bitmapBytes) {
        File file;
        FileOutputStream out = null;
        ByteArrayInputStream bais = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            bais = new ByteArrayInputStream(bitmapBytes);
            out = new FileOutputStream(file.getPath());
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bais.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            IOUtil.closeIO(bais);
            IOUtil.closeIO(out);
        }
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获得圆角图片
     *
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, w, h);
            final RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }
}
