package io.github.helpermethod.zipper;

import java.io.InputStream;
import java.util.zip.ZipEntry;

public record ZipperEntry(ZipEntry entry, InputStream inputStream) {}
