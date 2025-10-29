package io.github.helpermethod.zipper;

import io.github.helpermethod.zipforge.NodeGroup;
import static io.github.helpermethod.zipforge.ZipForge.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static java.util.function.Predicate.not;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ZipperTests {
    static List<Arguments> parameters = List.of(
        arguments((NodeGroup) () -> {
            file("a.txt", "a");
            file("b.txt", "b");
            directory("c", () -> {
                file("d.txt", "d");
            });
        })
    );

    @FieldSource("parameters")
    @ParameterizedTest
    void should_iterate_over_zip_entries(NodeGroup nodeGroup, @TempDir Path tempDir) throws IOException {
        createZipFile(tempDir.resolve("test.zip"), nodeGroup);

        var zipFilenames =
                new Zipper(Files.newInputStream(tempDir.resolve("test.zip")))
                        .stream()
                        .map(e -> e.entry().getName())
                        .toList();

        assertThat(List.of("a.txt", "b.txt", "c/", "c/d.txt")).isEqualTo(zipFilenames);
    }

    @FieldSource("parameters")
    @ParameterizedTest
    void should_iterate_over_zip_contents(NodeGroup nodeGroup, @TempDir Path tempDir) throws IOException {
        createZipFile(tempDir.resolve("test.zip"), nodeGroup);

        var zipContents =
                new Zipper(Files.newInputStream(tempDir.resolve("test.zip")))
                        .stream()
                        .filter(not(e -> e.entry().isDirectory()))
                        .map(e -> {
                            try {
                                return e.inputStream().readAllBytes();
                            } catch (IOException ex) {
                                throw new AssertionError(ex);
                            }
                        })
                        .toList();

        assertThat(List.of("a".getBytes(UTF_8), "b".getBytes(UTF_8), "d".getBytes(UTF_8))).containsExactlyElementsOf(zipContents);
    }
}
