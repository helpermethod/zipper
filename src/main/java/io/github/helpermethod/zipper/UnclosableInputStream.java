package io.github.helpermethod.zipper;

import java.io.FilterInputStream;
import java.io.InputStream;

class UnclosableInputStream extends FilterInputStream {
    protected UnclosableInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void close() {}
}
