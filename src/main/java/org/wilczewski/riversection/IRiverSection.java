package org.wilczewski.riversection;

public interface IRiverSection {
    void setRealDischarge(int realDischarge);
    void setRainfall(int rainfall);
    void assignsRetentionBasin(int port, String host);
}
