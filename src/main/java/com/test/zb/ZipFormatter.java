package com.test.zb;

import org.apache.commons.compress.archivers.zip.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;

class ZipFormatter {
    // Environment variables
    // Change them to customize
    public static final String iFile = "in.zip";
    public static final String oFile = "out.zip";
    public static final int compressLevel = 9;
    public static final int compressMethod = ZipMethod.DEFLATED.getCode();
    public static final boolean useZip64 = true;
    public static final String encoding = "UTF-8";
    public static final String comments = "Created by ZipFormatter";
    public static long staticTime = -1;
    public static long staticUncompressedSize = -1;
    public static long staticCompressedSize = -1;

    public static void main(String[] args) throws Exception {
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(new FileInputStream(iFile)); ZipArchiveOutputStream zos = new ZipArchiveOutputStream(new FileOutputStream(oFile))) {
            zos.setUseZip64(useZip64 ? Zip64Mode.Always : Zip64Mode.Never);
            zos.setEncoding(encoding);
            zos.setLevel(compressLevel);
            zos.setMethod(compressMethod);
            zos.setComment(comments);
            zos.setFallbackToUTF8(true);
            for (ZipArchiveEntry e = zis.getNextZipEntry(); e != null; e = zis.getNextZipEntry()) {
                final ZipArchiveEntry entry = new ZipArchiveEntry(e);
                entry.setMethod(compressMethod);
                entry.setTime(staticTime < 0 ? e.getTime() : staticTime);
                entry.setSize(staticUncompressedSize < 0 ? e.getSize() : staticUncompressedSize);
                entry.setCompressedSize(staticCompressedSize < 0 ? e.getCompressedSize() : staticCompressedSize);
                zos.putArchiveEntry(entry);
                ZbGenerator.transferStream(zis, zos);
                zos.closeArchiveEntry();
            }
            zos.flush();
        }
    }
}