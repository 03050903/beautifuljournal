package com.i2mobi.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class IOUtil {
    private final static int NULL_LENGTH = -1;

    private final static long MAX_SPACE = 8 * 1024 * 1024;

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private IOUtil() {

    }

    public static String concatPath(String... paths) {
        StringBuilder concatenatedPath = new StringBuilder();
        for (int i = 0; i < paths.length - 1; ++i) {
            concatenatedPath.append(paths[i]);
            if (!endsWithSeparator(paths[i])) {
                concatenatedPath.append(File.separator);
            }
        }
        concatenatedPath.append(paths[paths.length - 1]);
        return concatenatedPath.toString();
    }

    private static boolean endsWithSeparator(String path) {
        return path.endsWith("/") || path.endsWith("\\");
    }

    public static String getCanonicalPath(File file) {
        if (file == null) {
            return null;
        }
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        } catch (NoSuchElementException e) {
            return file.getAbsolutePath();
        }
    }

    public static void copyFile(File srcFile, File destFile) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean success = false;
        try {
            fis = new FileInputStream(srcFile);
        } catch (FileNotFoundException e) {
            throw new IOException(e.getMessage());
        }

        try {
            File parentFile = destFile.getAbsoluteFile().getParentFile();
            if ((parentFile != null) && (!parentFile.exists())) {
                makeDirs(parentFile);
            }

            fos = new FileOutputStream(destFile);
            int readCount;
            byte[] buffer = new byte[1024];
            while ((readCount = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, readCount);
            }
            closeStream(fis);
            closeStream(fos);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(fis);
            closeStream(fos);
            if (!success) {
                destFile.delete();
            }
        }
    }

    public static void copyFile(InputStream srcFileStream, File destFile)
            throws IOException {
        InputStream fis = null;
        FileOutputStream fos = null;
        boolean success = false;
        fis = srcFileStream;
        try {
            File parentFile = destFile.getAbsoluteFile().getParentFile();
            if ((parentFile != null) && (!parentFile.exists())) {
                makeDirs(parentFile);
            }

            fos = new FileOutputStream(destFile);
            int readCount;
            byte[] buffer = new byte[1024];
            while ((readCount = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, readCount);
            }
            closeStream(fis);
            closeStream(fos);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(fis);
            closeStream(fos);
            if (!success) {
                destFile.delete();
            }
        }
    }

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    public static void makeDirs(File dir) throws IOException {
        if (dir.exists()) {
            return;
        }
        boolean success = dir.mkdirs();
        if (!success) {
            throw new IOException("cannot create folder "
                    + dir.getAbsolutePath());
        }
    }

    public static void createFile(String filename) throws IOException {
        File file = new File(filename);
        File dir = file.getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }
        file.createNewFile();
    }

    public static String getPostfix(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(lastDotIndex + 1);
    }

    public static void delete(String path) {
        delete(new File(path));
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delete(f);
            }
        }
        file.delete();
    }

    public static void deleteFilesInDir(String path) {
        deleteFilesInDir(new File(path));
    }

    public static void deleteFilesInDir(File file) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            IOUtil.delete(f);
        }
    }

    public static void writeInt(ByteArrayOutputStream baos, int value) {
        baos.write(value);
        baos.write(value >> 8);
        baos.write(value >> 16);
        baos.write(value >> 24);
    }

    public static int readInt(ByteArrayInputStream bais) {
        int b1 = bais.read();
        int b2 = bais.read();
        int b3 = bais.read();
        int b4 = bais.read();
        return b1 + (b2 << 8) + (b3 << 16) + (b4 << 24);
    }

    public static void writeBytes(ByteArrayOutputStream baos, byte[] bytes) {
        if (bytes == null) {
            writeNull(baos);
        } else {
            writeInt(baos, bytes.length);
            baos.write(bytes, 0, bytes.length);
        }
    }

    public static byte[] readBytes(ByteArrayInputStream bais) {
        int length = readInt(bais);
        if (length == NULL_LENGTH) {
            return null;
        }
        byte[] bytes = new byte[length];
        bais.read(bytes, 0, bytes.length);
        return bytes;
    }

    public static void writeNull(ByteArrayOutputStream baos) {
        writeInt(baos, NULL_LENGTH);
    }

    public static String getTmpDir() {
        String defaultTmpDir = "/sdcard/.servotmp";
        if (isAndroid()) {
            return defaultTmpDir;
        }
        String tmpDir = System.getProperty("java.io.tmpdir", defaultTmpDir);
        return IOUtil.getCanonicalPath(new File(tmpDir));
    }

    public static BufferedReader getBufferedFileReader(File file)
            throws IOException {
        return new BufferedReader(new FileReader(file), DEFAULT_BUFFER_SIZE);
    }

    public static boolean isAndroid() {
        return "Dalvik".equalsIgnoreCase(System.getProperty("java.vm.name"));
    }

    public static long getUsedSpace(File dir) {
        if ((dir == null) || (!dir.exists())) {
            return 0;
        }

        long length = 0;
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                length += getUsedSpace(file);
            }
        } else {
            length = dir.length();
        }
        return length;
    }

    public static boolean isExceedLimitation(String filename) {
        return (getUsedSpace(new File(filename)) > MAX_SPACE);
    }
}
