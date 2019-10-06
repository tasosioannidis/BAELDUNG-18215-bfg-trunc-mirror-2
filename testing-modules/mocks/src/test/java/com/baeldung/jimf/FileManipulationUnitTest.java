package com.baeldung.jimf;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileManipulationUnitTest implements FileTestProvider {

    private static Stream<Arguments> provideFileSystem() {
        return Stream.of(
          Arguments.of(Jimfs.newFileSystem(Configuration.unix())),
          Arguments.of(Jimfs.newFileSystem(Configuration.windows())),
          Arguments.of(Jimfs.newFileSystem(Configuration.osX())));
    }

    private final FileManipulation fileManipulation = new FileManipulation();

    @ParameterizedTest
    @DisplayName("Should create a file on a file system")
    @MethodSource("provideFileSystem")
    void shouldCreateFile(final FileSystem fileSystem) throws Exception {
        final Path origin = fileSystem.getPath(RESOURCE_FILE_NAME);
        Files.copy(getResourceFilePath(), origin);
        final Path destination = fileSystem.getPath("newDirectory", RESOURCE_FILE_NAME);

        fileManipulation.move(origin, destination);

        assertFalse(Files.exists(origin));
        assertTrue(Files.exists(destination));
    }
}