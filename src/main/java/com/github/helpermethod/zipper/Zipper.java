package com.github.helpermethod.zipper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper {

    private Zipper() {
    }

    public static Iterable<ZipEntryIterator.Entry> newZipEntryIterable(ZipInputStream zipInputStream) {
        return () -> new ZipEntryIterator(zipInputStream);
    }

    public static Stream<ZipEntryIterator.Entry> newZipEntryStream(ZipInputStream zipInputStream) {
        return StreamSupport.stream(newZipEntryIterable(zipInputStream).spliterator(), false);
    }

    public static Path createZipFile(Path path, Consumer<RootNode> block) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(path))) {
            ZipFileVisitor zipFileVisitor = new ZipFileVisitor(zipOutputStream);

            RootNode rootNode = new RootNode();
            block.accept(rootNode);

            zipFileVisitor.visit(rootNode);
        }

        return path;
    }
}
