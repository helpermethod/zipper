package io.github.helpermethod.zipper;

import static io.github.helpermethod.zipforge.ZipForge.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.helpermethod.zipforge.NodeGroup;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ZipperTests {
    static List<Arguments> parameters = List.of(arguments((NodeGroup) () -> {
        file("a.txt", "a");
        file("b.txt", "b");
        directory("c", () -> {
            file("d.txt", "d");
        });
    }));

    @FieldSource("parameters")
    @ParameterizedTest
    void should_iterate_over_zip_entries(NodeGroup nodeGroup, @TempDir Path tempDir) throws IOException {
        var zipFile = createZipFile(tempDir.resolve("test.zip"), nodeGroup);

        try (var zipper = new Zipper(Files.newInputStream(zipFile))) {
            var zipFilenames = zipper.stream().map(e -> e.entry().getName()).toList();

            assertThat(List.of("a.txt", "b.txt", "c/", "c/d.txt")).isEqualTo(zipFilenames);
        }
    }

    @FieldSource("parameters")
    @ParameterizedTest
    void should_iterate_over_zip_contents(NodeGroup nodeGroup, @TempDir Path tempDir) throws IOException {
        var zipFile = createZipFile(tempDir.resolve("test.zip"), nodeGroup);

        try (var zipper = new Zipper(Files.newInputStream(zipFile))) {
            var zipContents = zipper.stream()
                    .filter(not(e -> e.entry().isDirectory()))
                    .map(e -> {
                        try (var inputStream = e.inputStream()) {
                            return inputStream.readAllBytes();
                        } catch (IOException ex) {
                            throw new AssertionError(ex);
                        }
                    })
                    .toList();

            assertThat(List.of("a".getBytes(UTF_8), "b".getBytes(UTF_8), "d".getBytes(UTF_8)))
                    .containsExactlyElementsOf(zipContents);
        }
    }
}
