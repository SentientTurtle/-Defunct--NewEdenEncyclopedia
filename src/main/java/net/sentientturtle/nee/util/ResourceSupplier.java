package net.sentientturtle.nee.util;

import java.io.IOException;

public interface ResourceSupplier {
    byte[] get() throws IOException;
}
