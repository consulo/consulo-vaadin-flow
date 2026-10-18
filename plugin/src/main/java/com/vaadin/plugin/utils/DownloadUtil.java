/*
 * Copyright 2024-2026 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.plugin.utils;

import consulo.http.HttpMethod;
import consulo.http.HttpRequestBuilderFactory;
import consulo.logging.Logger;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class DownloadUtil {
    private static final Logger LOG = Logger.getInstance(DownloadUtil.class);

    /**
     * The scripts of the Maven wrapper; a zip extracted by the JDK loses their executable bit.
     */
    private static final List<String> EXECUTABLES = List.of("mvnw");

    private DownloadUtil() {
    }

    /**
     * Downloads the project zip from {@code url} and extracts it into {@code outputDir}.
     */
    public static Path download(HttpRequestBuilderFactory httpRequestBuilderFactory, String url, Path outputDir) {
        LOG.info("Downloading " + url);
        try {
            Path tempFile = Files.createTempFile("project", ".zip");
            try {
                httpRequestBuilderFactory.newBuilder(url, HttpMethod.GET).saveToFile(tempFile, null);
                extract(tempFile, outputDir);
            }
            finally {
                Files.deleteIfExists(tempFile);
            }
            return outputDir;
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * The project zip contains a single root directory; its contents go straight into the project directory. The IntelliJ
     * plugin extracts the archive as it is and then moves the contents of the root directory up.
     */
    public static void extract(Path zip, Path outputDir) throws IOException {
        Path normalizedOutputDir = outputDir.toAbsolutePath().normalize();
        try (ZipFile zipFile = new ZipFile(zip.toFile())) {
            String root = getZipRootFolder(zipFile);
            for (ZipEntry entry : zipFile.stream().toList()) {
                String name = entry.getName();
                if (root != null && name.startsWith(root + "/")) {
                    name = name.substring(root.length() + 1);
                }
                if (name.isEmpty()) {
                    continue;
                }
                Path target = normalizedOutputDir.resolve(name).normalize();
                if (!target.startsWith(normalizedOutputDir)) {
                    throw new IOException("Entry " + entry.getName() + " is outside of " + outputDir);
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                    continue;
                }
                Files.createDirectories(target.getParent());
                try (InputStream inputStream = zipFile.getInputStream(entry)) {
                    Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
                }
                if (EXECUTABLES.contains(target.getFileName().toString())) {
                    target.toFile().setExecutable(true);
                }
            }
        }
    }

    /**
     * Finds root directory in zip archive if present
     */
    private static @Nullable String getZipRootFolder(ZipFile zipFile) {
        for (ZipEntry zipEntry : zipFile.stream().toList()) {
            // we do not necessarily get a separate entry for the subdirectory when the file in the ZIP archive is placed in a
            // subdirectory, so we need to check if the slash is found anywhere in the path
            int indexOf = zipEntry.getName().indexOf('/');
            if (indexOf >= 0) {
                return zipEntry.getName().substring(0, indexOf);
            }
        }
        return null;
    }
}
