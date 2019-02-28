package com.talkfun.cloudlive.updateappmodule.util;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;


public class FileUtils {
    /**
     * 复制文件
     * @param fromFile
     * @param toFile
     */
    public static void copyFile(File fromFile, File toFile) {

        if (!fromFile.exists())
            return;
        if (!fromFile.isFile())
            return;
        if (!fromFile.canRead())
            return;

        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists()) {
            toFile.delete();
        }
        try {
            java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();

        } catch (Exception ex) {
            Log.e("readfile", ex.getMessage());
        }
    }
}
