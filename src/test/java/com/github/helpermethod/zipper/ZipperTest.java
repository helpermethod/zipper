package com.github.helpermethod.zipper;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ZipperTest {
    @Nested
    class createZipFile {
        @MethodSource("com.github.helpermethod.zipper.ZipperTest$createZipFile#zipfileEntries")
        @ParameterizedTest
        void should_create_a_zip_file(Consumer<RootNode> block, List<String> entries, @TempDir Path tempDir) throws IOException {
            var location = tempDir.resolve("test.zip");

            Zipper.createZipFile(location, block);

            try (var zipFile = new ZipFile(location.toFile())) {
                assertThat(Collections.list(zipFile.entries()))
                    .extracting("name")
                    .isEqualTo(entries);
            }
        }

        static Stream<Arguments> zipfileEntries() {
            return Stream.of(
                arguments(
                    (Consumer<RootNode>) r -> r.file("test.txt", "test"),
                    List.of("test.txt")
                ),
                arguments(
                    (Consumer<RootNode>) r -> r.directory("test", d -> {}),
                    List.of("test/")
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.directory("a", d ->
                            d.file("test.txt", "test")
                        ),
                    List.of("a/", "a/test.txt")
                )
            );
        }
    }

    @Nested
    static class newZipEntryIterator {

    }
}
