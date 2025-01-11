package org.wilczewski.riversection;

import java.io.IOException;

public interface IRiverSection {
    void setRealDischarge(int realDischarge);
    void setRainfall(int rainfall) throws IOException;
    void assignsRetentionBasin(int port, String host);
}
