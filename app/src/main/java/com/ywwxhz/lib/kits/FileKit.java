package com.ywwxhz.lib.kits;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by ywwxhz on 2014/10/17.
 */
public class FileKit {

    /**
     * 复制文件(以超快的速度复制文件)
     *
     * @param srcFile     源文件File
     * @param destDir     目标目录File
     * @param newFileName 新文件名
     * @return 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
     */
    public static long copyFile(String srcFile, File destDir, String newFileName) {
        long copySizes = 0;
        if (!destDir.exists()) {
            if(!destDir.mkdirs()) {
                System.out.println("无法建立文件夹");
                return -1;
            }
        }
        if (newFileName == null) {
            System.out.println("文件名为null");
            return -1;
        }

        FileInputStream fsin = null;
        FileOutputStream fsout = null;
        try {
            fsin = new FileInputStream(srcFile);
            fsout = new FileOutputStream(new File(destDir, newFileName));
            FileChannel fcin = fsin.getChannel();
            FileChannel fcout = fsout.getChannel();
            long size = fcin.size();
            fcin.transferTo(0, fcin.size(), fcout);
            fcin.close();
            fcout.close();
            copySizes = size;
        } catch (Exception e) {
            e.printStackTrace();
            copySizes = -1;
        } finally {
            if (fsin != null)
                try {
                    fsin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    copySizes = -1;
                }
            if (fsout != null)
                try {
                    fsout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    copySizes = -1;
                }
        }
        return copySizes;
    }


    public static String buildFilePath(String path) {
        return Environment.getExternalStorageDirectory().getPath() + File.separator + path;
    }

    public static boolean checkExternalStorage() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getFolderSize(File file){
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return size;
    }

    public static long getFolderSize(String path){
        File file = new File(path);
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return size;
    }

    public static boolean writeFile(String path, String fileName, String content) {
        File pathf = new File(path);
        if (!pathf.exists()) {
            if (!pathf.mkdirs()) {
                return false;
            }
        }
        return writeFile(pathf, fileName, content);
    }

    public static boolean writeFile(File path, String fileName, String content) {
        return writeFile(new File(path, fileName), content);
    }

    public static boolean writeFile(String fileName, String content) {
        return writeFile(new File(fileName), content);
    }

    public static boolean writeFile(File file, String content) {
        return writeFile(file, false, content);
    }

    public static boolean writeFile(File file, boolean append, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file, append), 1024);
            out.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static String getFileContent(String fileName) {
        return getFileContent(new File(fileName));
    }

    public static String getFileContent(File path, String fileName) {
        return getFileContent(new File(path, fileName));
    }

    public static String getFileContent(File file) {
        if (!file.exists())
            return null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            StringBuilder readString = new StringBuilder();
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                readString.append(currentLine);
            }
            return readString.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static String writeBitmap(String path, String filename, Bitmap bitmap, Bitmap.CompressFormat formate) throws Exception {
        File outfile = new File(buildFilePath(path));
        // 如果文件不存在，则创建一个新文件
        if (!outfile.isDirectory()) {
            if (!outfile.mkdirs()) {
                throw new RuntimeException("Can't make dirs");
            }
        }
        String suffix;
        switch (formate) {
            case JPEG:
                suffix = ".jpg";
                break;
            case PNG:
                suffix = ".png";
                break;
            case WEBP:
                suffix = ".webp";
                break;
            default:
                suffix = "";
                break;
        }
        String fname = outfile + "/" + filename + suffix;
        long startTime = System.currentTimeMillis();
        FileOutputStream fos = new FileOutputStream(fname);
        bitmap.compress(formate, 90, fos);
        long endTime = System.currentTimeMillis();
        System.out.println("compress image cost: " + (endTime - startTime)
                + "ms");
        fos.flush();
        fos.close();
        bitmap.recycle();
        return fname;
    }
}
