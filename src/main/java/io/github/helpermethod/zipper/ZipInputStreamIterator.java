package io.github.helpermethod.zipper;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class ZipInputStreamIterator implements Iterator<ZipperEntry> {
    private final ZipInputStream zipInputStream;
    private ZipEntry zipEntry;

    ZipInputStreamIterator(ZipInputStream zipInputStream) {
        this.zipInputStream = zipInputStream;
    }

    @Override
    public boolean hasNext() {
        if (zipEntry != null) return true;

        try {
            zipEntry = zipInputStream.getNextEntry();
        } catch (IOException e) {
            throw new ZipperException(e);
        }

        return zipEntry != null;
    }

    @Override
    public ZipperEntry next() {
        if (!hasNext()) throw new NoSuchElementException();

        var zipperEntry = new ZipperEntry(zipEntry, new UnclosableInputStream(zipInputStream));

        zipEntry = null;

        return zipperEntry;
    }
}
