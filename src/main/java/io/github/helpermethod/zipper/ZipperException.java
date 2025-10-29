package io.github.helpermethod.zipper;

public class ZipperException extends RuntimeException {
    public ZipperException(Exception exception) {
        super(exception);
    }
}
