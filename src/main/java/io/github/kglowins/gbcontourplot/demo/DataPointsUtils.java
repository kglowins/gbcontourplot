package io.github.kglowins.gbcontourplot.demo;

import io.github.kglowins.gbcontourplot.grid.Function2DValue;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
class DataPointsUtils {

    private static final String WHITESPACE = "\\s+";

    private DataPointsUtils() {
    }

    static List<Function2DValue> readDataPoints(String resource) {
        try {
            var resourcePathAndFs = getResourcePathAndFs(resource);
            List<Function2DValue> dataPoints = Files.lines(resourcePathAndFs.getPath())
                .map(line -> {
                    String[] words = line.trim().split(WHITESPACE);
                    double x = Double.parseDouble(words[0]);
                    double y = Double.parseDouble(words[1]);
                    double f = Double.parseDouble(words[4]);
                    return Function2DValue.of(x, y, f);
                })
                .collect(toList());
            resourcePathAndFs.getFs().close();
            return dataPoints;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static PathAndFs getResourcePathAndFs(String resource) {
        try {
            var resourceUri = DataPointsUtils.class.getClassLoader().getResource(resource).toURI();
            var fs = createFileSystem(resourceUri);
            return new PathAndFs(Paths.get(resourceUri), fs);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileSystem createFileSystem(URI resourceUri) {
        Map<String, String> env = Map.of("create", "true");
        try {
            return FileSystems.newFileSystem(resourceUri, env);
        } catch (IOException e) {
            log.error("Failed to createFileSystem", e);
            throw new UncheckedIOException(e);
        }
    }

    @Value
    @AllArgsConstructor
    private static class PathAndFs {
        private Path path;
        private FileSystem fs;
    }
}
