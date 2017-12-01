package com.haokan.pubic.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.haokan.pubic.clipimage.ClipImgManager;
import com.haokan.pubic.logsys.LogHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class FileUtil {
    public static final String TAG = "FileUtil";
    protected static final int BUFFER_SIZE = 4096;

    public abstract static class ProgressListener {
        public void onStart(long total){

        }
        public void onProgress(long current, long total){

        }
        public void onSuccess(){

        }
        public void onFailure(Exception e){

        }
    }

    /**
     * 给下载文件用的,将一个流存文件, 并且可监听进度
     * @param inputStream
     * @param file
     * @param totalSize
     * @param listener
     * @return
     */
    public static boolean writeInputStreamToFile(InputStream inputStream, File file, long totalSize, ProgressListener listener) {
        if (inputStream == null || file == null) {
            LogHelper.d(TAG, "writeInputStreamToFile inputStream or file == null");
            return false;
        }
        if (listener != null) {
            listener.onStart(totalSize);
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, false);
            byte[] tmp = new byte[BUFFER_SIZE];
            int size = 0, count;
            while ((count = inputStream.read(tmp)) != -1) {
                size += count;
                outputStream.write(tmp, 0, count);
                if (listener != null) {
                    listener.onProgress(size, totalSize);
                }
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailure(e);
            }
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                LogHelper.w("wangzixu", "writeInputStreamToFile Cannot flush output stream");
            }
        }
        if (listener != null) {
            listener.onSuccess();
        }
        return true;
    }

    /**
     * 获取一个文件夹的大小，单位字节
     */
    public static long getFolderSize(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        try {
            if (file.isFile()) {
                return file.length();
            }
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    size = size + getFolderSize(f);
                }
            }
        } catch (Exception e) {
            LogHelper.e(TAG, "getFolderSize exception");
            e.printStackTrace();
        }
        return size;
    }

    public static void deleteFile(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                deleteContents(file, true);
            } else {
                try {
                    if (!file.delete()) {
                        throw new IOException("failed to delete file: " + file);
                    }
                } catch (Exception e) {
                    LogHelper.e(TAG, "getFolderSize exception");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除指定目录下的所有内容
     */
    public static void deleteContents(File dir, boolean deleteSelf) {
        if (dir == null || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteContents(file, true);
                    if (deleteSelf) {
                        file.delete();
                    }
                } else {
                    file.delete();
                }
            }
        } catch (Exception e) {
            LogHelper.e(TAG, "getFolderSize exception");
            e.printStackTrace();
        }
    }

    /**
     * 获取格式化的文件大小数字
     */
    public static String getFormatSize(double size) {
        double kb = size / 1024;
        if (kb < 1) {
            return size + "Byte";
        }
        DecimalFormat format = new DecimalFormat(".00");//必须保留两位小数，不够0补零
        double mb = kb / 1024;
        if (mb < 1) {
            return format.format(kb) + "KB";
        }

        double gb = mb / 1024;
        if (gb < 1) {
            return format.format(mb) + "MB";
        }

        double tb = gb / 1024;
        if (tb < 1) {
            return format.format(gb) + "GB";
        }
        return format.format(gb) + "TB";
    }
    /**
     * 保存bitmap到本地
     */
    public static boolean saveBitmapToFile(Context context, final Bitmap source, File f, final boolean notifySystem) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        boolean success = false;
        if (notifySystem) {
            // 把文件插入到系统图库
            try {
                ContentResolver cr = context.getContentResolver();

                long timeMillis = System.currentTimeMillis();

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, f.getAbsolutePath());
                values.put(MediaStore.Images.Media.TITLE, f.getName());
                values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, f.getName());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "好看锁屏");
//                values.put(MediaStore.Images.Media.DATE_ADDED, timeMillis/1000);
//                values.put(MediaStore.Images.Media.DATE_TAKEN, timeMillis);
//                values.put(MediaStore.Images.Media.DATE_MODIFIED, timeMillis/1000);
                values.put(MediaStore.Images.ImageColumns.WIDTH, source.getWidth());
                values.put(MediaStore.Images.ImageColumns.HEIGHT, source.getHeight());

                Uri url = null;
                try {
//                    url = Uri.parse(MediaStore.Images.Media.insertImage(cr, source, f.getName(), null));

                    url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    if (source != null) {
                        OutputStream imageOut = cr.openOutputStream(url);
                        try {
                            source.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
                        } finally {
                            imageOut.close();
                        }

//                        long id = ContentUris.parseId(url);
                        // Wait until MINI_KIND thumbnail is generated.
//                        Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
//                                MediaStore.Images.Thumbnails.MINI_KIND, null);
//                        // This is for backward compatibility.
//                        Bitmap microThumb = MediaStore.Images.Thumbnails.StoreThumbnail(cr, miniThumb, id, 50F, 50F,
//                                MediaStore.Images.Thumbnails.MICRO_KIND);
                        success = true;
                    } else {
                        Log.e("wangzixu", "插入图片 Failed to create thumbnail, removing original");
                        cr.delete(url, null, null);
                        url = null;
                    }
                } catch (Exception e) {
                    Log.e("wangzixu", "插入图片 Failed to insert image", e);
                    if (url != null) {
                        cr.delete(url, null, null);
                        url = null;
                    }
                }

                LogHelper.d("wangzixu", "插入图片 uri = " + url + ", path = " + new ClipImgManager().getPath(context, url));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            //                intent.setData(Uri.fromFile(f));
            //                context.sendBroadcast(intent);
        } else {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (fos == null) {
                return false;
            }

            source.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            try {
                fos.flush();
                fos.close();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (notifySystem) {
                    MediaScannerConnection.scanFile(context,
                            new String[] { f.getAbsolutePath() }, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.v("wangzixu", "插入图片 file " + path
                                            + " was scanned seccessfully: " + uri);
                                }
                            });
                }
            }
        }

        return success;
    }

    public static void moveFile(File fileFrom, File fileTo) {
        FileInputStream ins = null;
        FileOutputStream out = null;

        try {
            if (!fileFrom.exists()) {
                fileFrom.createNewFile();
            }

            if (!fileTo.exists()) {
                fileTo.createNewFile();
            }

            ins = new FileInputStream(fileFrom);
            out = new FileOutputStream(fileTo);
            byte[] b = new byte[2048];
            int n;
            while((n=ins.read(b))!=-1){
                out.write(b, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
