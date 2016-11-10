package edu.kit.ipd.parse.multiasr.asr.revise;

import edu.kit.ipd.parse.multiasr.asr.AbstractASR;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.function.Consumer;

import static org.junit.Assert.fail;

/**
 * Created by Me on 05.04.16.
 */
public class AbstractASRTest {
    protected void forAll(Consumer<Path> consumer) throws URISyntaxException, IOException {
        URL resource = this.getClass().getClassLoader().getResource("speeches/");

        if(resource == null) {
            fail();
        } else {
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{flac,wav}");

            Path dir = Paths.get(resource.toURI());
            if(Files.isDirectory(dir)) {
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
                for (Path path : directoryStream) {
                    if(pathMatcher.matches(path.getFileName())) {
                        consumer.accept(path);
                    }
                }
                directoryStream.close();
            } else {
                fail();
            }
        }
    }

    protected void forCount(Consumer<Path> consumer, int count) throws URISyntaxException, IOException {
        URL resource = this.getClass().getClassLoader().getResource("speeches/");

        int i = 0;
        if(resource == null) {
            fail();
        } else {
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{flac,wav}");

            Path dir = Paths.get(resource.toURI());
            if(Files.isDirectory(dir)) {
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir);
                for (Path path : directoryStream) {
                    if(pathMatcher.matches(path.getFileName())) {
                        if(++i > count) {
                            return;
                        }
                        consumer.accept(path);
                    }
                }
                directoryStream.close();
            } else {
                fail();
            }
        }
    }

    public static DirectoryStream<Path> getSpeeches() throws URISyntaxException, IOException {
        URL resource = AbstractASR.class.getClassLoader().getResource("speeches/");
        if(resource == null) {
            fail();
        } else {
            final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{flac,wav}");

            DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path entry) throws IOException {
                    return pathMatcher.matches(entry.getFileName());
                }
            };


            Path dir = Paths.get(resource.toURI());
            if(Files.isDirectory(dir)) {
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, filter);
                return directoryStream;
            } else {
                fail();
            }
        }
        return null;
    }
}
