package com.github.helpermethod.zipper;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zipper implements Iterable<Zipper.ZipFileEntry>, AutoCloseable {
    private final ZipInputStream zipInputStream;

    private Zipper(ZipInputStream zipInputStream) {
        this.zipInputStream = zipInputStream;
    }

    public static Zipper from(InputStream inputStream) {
        return new Zipper(new ZipInputStream(inputStream));
    }

    public static Zipper from(Path path) throws IOException {
        return from(path.toUri());
    }

    public static Zipper from(URL url) throws IOException {
        return from(url.openStream());
    }

    public static Zipper from(URI uri) throws IOException {
        return from(uri.toURL());
    }

    public static Zipper from(File file) {
        try {
            return from(new ZipInputStream(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterator<ZipFileEntry> iterator() {
        try {
            return new ZipStreamIterator(zipInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream<ZipFileEntry> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    @Override
    public void close() throws Exception {
        zipInputStream.close();
    }

    private static class ZipStreamIterator implements Iterator<ZipFileEntry> {
        private final ZipInputStream zipInputStream;
        private ZipEntry zipEntry;

        private ZipStreamIterator(ZipInputStream zipInputStream) throws IOException {
            this.zipInputStream = zipInputStream;
            this.zipEntry = zipInputStream.getNextEntry();
        }

        public boolean hasNext() {
            return zipEntry != null;
        }

        public ZipFileEntry next() {
            try {
                zipEntry = zipInputStream.getNextEntry();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return new ZipFileEntry(zipEntry, new FilterInputStream(zipInputStream) {
                @Override
                public void close() {
                }
            });
        }
    }

    public static class ZipFileEntry {
        private final ZipEntry zipEntry;
        private final InputStream inputStream;

        private ZipFileEntry(ZipEntry zipEntry, InputStream inputStream) {
            this.zipEntry = zipEntry;
            this.inputStream = inputStream;
        }

        public ZipEntry entry() {
            return zipEntry;
        }

        public InputStream inputStream() {
            return inputStream;
        }
    }
}
