package org.wilczewski.controlcenter;

import java.io.IOException;

public interface IControlerCenter {
    void assignRetentionBasin(int port, String host) throws IOException;
}
