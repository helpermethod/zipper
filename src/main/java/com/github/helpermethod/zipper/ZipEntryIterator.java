package com.github.helpermethod.zipper;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class ZipEntryIterator implements Iterator<ZipEntryIterator.Entry> {

    private final ZipInputStream zipInputStream;
    private ZipEntry zipEntry;

    ZipEntryIterator(ZipInputStream zipInputStream) {
        this.zipInputStream = zipInputStream;
    }

    public boolean hasNext() {
        try {
            zipEntry = zipInputStream.getNextEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return zipEntry != null;
    }

    public ZipEntryIterator.Entry next() {
        return new ZipEntryIterator.Entry(
            zipEntry,
            new FilterInputStream(zipInputStream) {
                @Override
                public void close() {}
            }
        );
    }

    public static class Entry {

        private final ZipEntry zipEntry;
        private final InputStream zipEntryInputStream;

        private Entry(ZipEntry zipEntry, InputStream zipEntryInputStream) {
            this.zipEntry = zipEntry;
            this.zipEntryInputStream = zipEntryInputStream;
        }

        public ZipEntry zipEntry() {
            return zipEntry;
        }

        public InputStream zipEntryInputStream() {
            return zipEntryInputStream;
        }
    }
}
