package com.alex.utils;

import com.alex.io.InputStream;
import com.alex.io.OutputStream;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Utils {

    private Utils() {
    }

    public static byte[] cryptRSA(byte[] data, BigInteger exponent, BigInteger modulus) {
        return new BigInteger(data).modPow(exponent, modulus).toByteArray();
    }

    public static String getJagString(InputStream stream) {
        int first = stream.readUnsignedByte();
        if (first == 0) {
            return "";
        }
        return (char) first + getString(stream);
    }

    private static String getString(InputStream stream) {
        StringBuilder bldr = new StringBuilder();
        int b;
        while (true) {
            b = stream.readUnsignedByte();
            if (b == 0) break;
            bldr.append((char) b);
        }
        return bldr.toString();
    }

    public static byte[] getArchivePacketData(int indexId, int archiveId, byte[] archive) {
        OutputStream stream = new OutputStream(archive.length + 4);

        stream.writeByte(indexId);
        stream.writeShort(archiveId);
        stream.writeByte(0);
        stream.writeInt(archive.length);

        int offset = 8;

        for (int i = 0; i < archive.length; i++) {
            if (offset == 512) {
                stream.writeByte(-1);
                offset = 1;
            }

            stream.writeByte(archive[i]);
            offset++;
        }

        byte[] data = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(data, 0, data.length);
        return data;
    }

    public static int readMedium(ByteBuffer buffer) {
        return ((buffer.get() & 0xFF) << 16) | ((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
    }

    public static void writeMedium(ByteBuffer buffer, int value) {
        buffer.put((byte) (value >> 16));
        buffer.put((byte) (value >> 8));
        buffer.put((byte) value);
    }

    public static int getNameHash(String name) {
        return name.toLowerCase(Locale.getDefault()).hashCode();
    }

    public static String cache_path = "../Server/data/cache/";
    public static String dump_path = cache_path + File.separator + "backup/";

    public static void backupCache() {
        File cacheDir = new File(cache_path);
        if (!cacheDir.exists() || !cacheDir.isDirectory()) {
            return;
        }

        try {
            File backupFile = new File(dump_path, "cache_backup_" + System.currentTimeMillis() + ".zip");
            if (backupFile.getParentFile() != null) {
                backupFile.getParentFile().mkdirs();
            }

            try (ZipOutputStream zipOut = new ZipOutputStream(
                    new BufferedOutputStream(new FileOutputStream(backupFile)))) {
                zipDirectory(cacheDir, cacheDir, zipOut);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void zipDirectory(File rootDir, File sourceDir, ZipOutputStream zipOut) throws IOException {
        File[] files = sourceDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(rootDir, file, zipOut);
            } else {
                String entryName = rootDir.toURI().relativize(file.toURI()).getPath().replace("\\", "/");
                zipOut.putNextEntry(new ZipEntry(entryName));
                try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = input.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, len);
                    }
                }
                zipOut.closeEntry();
            }
        }
    }
}
