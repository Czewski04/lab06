package org.wilczewski.retentionbasin;

public interface IRetentionBasin {
    int getWaterDischarge();
    double getFillingPercentage();
    void setWaterDischarge(int waterDischarge);
    void setWaterInflow(int waterInflow, int port);
    void assignRiverSection(int port, String host);
}
