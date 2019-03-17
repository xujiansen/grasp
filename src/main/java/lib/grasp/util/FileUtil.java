package lib.grasp.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.rooten.util.Utilities;
import okhttp3.Response;

/**
 * 文件(夹)增删改查,大小
 */
public class FileUtil {

    /**
     * 判断目录或文件是否存在
     */
    public static boolean fileExists(final String strFile) {
        try {
            if (TextUtils.isEmpty(strFile)) return false;

            final File filePath = new File(strFile);
            return filePath.exists();
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 保证指定的目录存在
     */
    public static boolean ensurePathExists(final String strPath) {
        try {
            final File filePath = new File(strPath);
            boolean exists = filePath.exists();
            if (!exists) {
                return filePath.mkdirs();
            }
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 保证指定的文件存在
     */
    public static boolean ensureFileExists(final String strPath) {
        try {
            final File filePath = new File(strPath);
            boolean exists = filePath.exists();
            if (!exists) {
                return filePath.createNewFile();
            }
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 创建目录
     */
    public static void createPath(final String strPath) {
        final File filePath = new File(strPath);
        if (filePath.exists()) return;
        filePath.mkdirs();
    }

    /**
     * 列举目录下指定扩展名的文件名（全路径），没有指定扩展名则列举所有文件
     */
    public static ArrayList<String> listFile(final String path, final String ext) {
        ArrayList<String> arrFiles = new ArrayList<>();
        if (path == null || !fileExists(path)) {
            return arrFiles;
        }

        File filePath = new File(path);
        if (!filePath.canRead()) return arrFiles;

        FilenameFilter filter = null;
        if (ext != null && ext.length() > 0 && !ext.equalsIgnoreCase("*.*")) {
            String[] arrExt = ext.split("\\.");
            if (arrExt == null || arrExt.length == 0)
                return arrFiles;

            final String strExt = arrExt[arrExt.length - 1];
            filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    String[] arrName = filename.split("\\.");
                    if (arrName == null || arrName.length < 2)
                        return false;
                    return arrName[arrName.length - 1].equalsIgnoreCase(strExt);
                }
            };
        }

        String list[] = filePath.list(filter);
        if (list == null) return arrFiles;
        for (String str : list) {
            arrFiles.add(filePath.getPath() + "/" + str);
        }
        return arrFiles;
    }

    /**
     * 清除目录下指定扩展名的文件，没有指定扩展名清除所有文件
     */
    public static void cleanDir(final String path, final String ext) {
        ArrayList<String> arr = listFile(path, ext);
        for (String str : arr) {
            delFile(str);
        }
        arr = null;
    }

    /**
     * 删除文件
     */
    public static boolean delFile(final String filename) {
        if (TextUtils.isEmpty(filename)) return true;
        File file = new File(filename);

        if (!file.exists()) return true;
        if (!file.isFile()) return false;
        return file.delete();
    }

    /**
     * 删除文件
     */
    public static boolean delFile(final File file) {
        if (file == null) return false;
        return delFile(file.toString());
    }

    /**
     * 列举目录下指定扩展名的文件名（全路径），没有指定扩展名则列举所有文件
     */
    public static ArrayList<String> listFileNoExt(final String path, final String ext) {
        ArrayList<String> arrFiles = new ArrayList<>();
        if (path == null || !fileExists(path)) {
            return arrFiles;
        }

        File filePath = new File(path);
        if (!filePath.canRead()) return arrFiles;

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return !filename.endsWith(ext);
            }
        };

        String list[] = filePath.list(filter);
        if (list == null) return arrFiles;
        for (String str : list) {
            arrFiles.add(filePath.getPath() + "/" + str);
        }
        return arrFiles;
    }

    /**
     * 列举目录下指定扩展名的文件数
     */
    public static int getFileCount(final String path, final String ext) {
        int count = 0;
        if (path == null || !fileExists(path)) {
            return count;
        }

        File filePath = new File(path);
        if (!filePath.canRead()) return count;

        FilenameFilter filter = null;
        if (ext != null && ext.length() > 0) {
            String[] arrExt = ext.split("\\.");
            if (arrExt == null || arrExt.length == 0)
                return count;

            final String strExt = arrExt[arrExt.length - 1];
            filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    String[] arrName = filename.split("\\.");
                    if (arrName == null || arrName.length < 2)
                        return false;
                    return arrName[arrName.length - 1].equalsIgnoreCase(strExt);
                }
            };
        }

        String list[] = filePath.list(filter);
        if (list == null) return count;
        return list.length;
    }


    /**
     * 把源目录下指定扩展名的文件移到目标目录下，没有指定扩展名移动所有文件
     */
    public static boolean moveFile2Dir(final String src, final String des, final String ext) {
        if (!ensurePathExists(des)) return false;

        boolean ret = true;
        File newPath = new File(des);
        ArrayList<String> arr = listFile(src, ext);
        for (String str : arr) {
            File file = new File(str);
            File newfile = new File(newPath.getAbsolutePath() + "/" + file.getName());
            newfile.delete();                // 删除已经存在的文件
            ret = file.renameTo(newfile);
            if (!ret) break;
        }
        arr = null;
        return ret;
    }

    /**
     * 删除文件
     */
    public static boolean delDir(final String dir) {
        if (dir == null) return true;
        File file = new File(dir);

        if (!file.exists()) return true;
        if (!file.isDirectory()) return false;
        return file.delete();
    }

    /**
     * 删除文件
     */
    public static boolean delDir(final File file) {
        if (file == null) return true;
        if (!file.exists()) return true;
        if (!file.isDirectory()) return false;
        return file.delete();
    }

    //删除文件夹
    //param folderPath 文件夹完整绝对路径
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除指定文件夹下所有文件
    //param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
//				delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 移动文件
     */
    public static boolean moveFile(final String srcName, final String newName) {
        if (srcName == null || newName == null) return false;
        File file = new File(srcName);
        if (!file.exists() || !file.isFile() ||
                !file.canRead() || !file.canWrite()) {
            return false;
        }

        File newFile = new File(newName);
        return file.renameTo(newFile);
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(final String srcName, final String desName) {
        if (srcName == null || desName == null) return false;

        File src = new File(srcName);
        if (!src.exists() || !src.isFile()) return false;

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(desName);

            byte[] data = new byte[8 * 1024];
            int len = fis.read(data);
            while (len != -1) {
                fos.write(data, 0, len);
                len = fis.read(data);
            }
            fis.close();
            fos.close();
            return true;
        } catch (Exception e) {
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }
        return false;
    }

    /**
     * 重命名文件
     */
    public static boolean renameFile(final String oldName, final String newName) {
        if (oldName == null || newName == null) return false;
        if (oldName.equalsIgnoreCase(newName)) return true;

        File file = new File(oldName);
        if (!file.exists() || !file.canRead() || !file.canWrite()) {
            return false;
        }

        File newFile = new File(newName);
        return file.renameTo(newFile);
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void saveBytetoFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                // 判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null && fos != null) {
                try {
                    bos.flush();
                    fos.flush();
                    bos.close();
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    // 追加保存文字到手机
    public static void appendStrToFile(Context context, String fileName, String textContent){
        StringBuffer oldStr = new StringBuffer(readStrFromFile(context, fileName));
        StringBuffer newStr = oldStr.append("\n").append(textContent);
        saveStrToFile(context, fileName, newStr.toString());
    }

    // 保存文字到手机
    public static void saveStrToFile(Context context, String fileName, String textContent) {
        FileOutputStream outStream = null;
        try {
            outStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outStream.write(textContent.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outStream != null) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 从手机读取文字
    public static String readStrFromFile(Context context, String fileName) {
        byte[] content = null;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] b = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (fis.read(b) != -1) {
                byteArrayOutputStream.write(b);
            }
            content = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (content != null) {
            return new String(content);
        }
        return "";
    }

    // 删除文件到手机
    public static void deleteFile(Context context, String fileName) {
        try {
            context.deleteFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getExtName(File f) {
        if (f == null || !f.exists()) {
            return "";
        }

        String filename = f.getName();
        try {
            return filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        } catch (Exception e) {
        }
        return "";
    }

    public static String getExtNameByPath(String filepath) {
        File file = new File(filepath);
        return getExtName(file);
    }

    public static String getExtName(String filename) {
        if (Utilities.isEmpty(filename)) {
            return "";
        }

        try {
            return filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        } catch (Exception e) {
        }
        return "";
    }

    public static String getFileSize(Context cxt, long size) {
        return Formatter.formatFileSize(cxt, size); // 自动递进--B/KB/MB
    }

    public static String getFileSize(Context cxt, String path) {
        try {
            File file = new File(path);
            return getFileSize(cxt, file.length());
        } catch (Exception e) {
            return "";
        }
    }

    public static long getFileSize(String path) {
        try {
            File file = new File(path);
            return file.length();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getFileName(String text) {
        try {
            File file = new File(text);
            return file.getName();
        } catch (Exception e) {
            return "位置文件名";
        }
    }

    public static String getFormatStoreSize(Context cxt, long size) {
        return Formatter.formatFileSize(cxt, size); // 自动递进--B/KB/MB
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExists(final String strFile) {
        try {
            final File filePath = new File(strFile);
            return filePath.exists() && filePath.isFile();
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExists(final File file) {
        try {
            return file.exists() && file.isFile();
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    public static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    /**
     * 文件转base64字符串
     * @param file
     * @return
     */
    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.NO_WRAP);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return base64;
    }

    /**
     * base64字符串转文件
     * @param base64
     * @return
     */
    public static File base64ToFile(String base64) {
        File file = null;
        String fileName = "/Petssions/record/testFile.amr";
        FileOutputStream out = null;
        try {
            // 解码，然后将字节转换为文件
            file = new File(Environment.getExternalStorageDirectory(), fileName);
            if (!file.exists())
                file.createNewFile();
            byte[] bytes = Base64.decode(base64, Base64.NO_WRAP);// 将字符串转换为byte数组
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            out = new FileOutputStream(file);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (out!= null) {
                    out.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return file;
    }

    public static void saveOkHttpFile(Response response, File destFile) throws EOFException{
        if(FileUtil.isFileExists(destFile)) FileUtil.delFile(destFile);
        if(FileUtil.fileExists(destFile.getAbsolutePath())) FileUtil.delDir(destFile);
        String path = destFile.getParent();
        FileUtil.ensurePathExists(path);

        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;

        //储存下载文件的目录
        try {
            is = response.body().byteStream();
            fos = new FileOutputStream(destFile);
            while ((len = is.read(buf)) != -1) fos.write(buf, 0, len);
            fos.flush();
        } catch (Exception e) {
            throw new EOFException("读错啦");
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                throw new EOFException("读完关闭失败拉");
            }
        }
    }
}
