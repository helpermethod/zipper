package io.github.helpermethod.zipper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipInputStream;

public class Zipper implements Iterable<ZipperEntry>, AutoCloseable {
    private final ZipInputStream zipInputStream;

    public Zipper(InputStream inputStream) {
        this.zipInputStream = new ZipInputStream(inputStream);
    }

    @Override
    public Iterator<ZipperEntry> iterator() {
        try {
            return new ZipInputStreamIterator(zipInputStream);
        } catch (IOException e) {
            throw new ZipperException(e);
        }
    }

    public Stream<ZipperEntry> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public void close() {
        try {
            zipInputStream.close();
        } catch (IOException e) {
            throw new ZipperException(e);
        }
    }
}
