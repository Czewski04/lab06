package org.wilczewski.controlcenter;

public class RetentionBasinMapItem {
    private String host;
    private double fillingPercentage;
    private int waterDischargeValve;

    public RetentionBasinMapItem(String host) {
        this.host = host;
        this.fillingPercentage = 0.0;
        this.waterDischargeValve = 0;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public double getFillingPercentage() {
        return fillingPercentage;
    }

    public void setFillingPercentage(double fillingPercentage) {
        this.fillingPercentage = fillingPercentage;
    }

    public int getWaterDischargeValve() {
        return waterDischargeValve;
    }

    public void setWaterDischargeValve(int waterDischargeValve) {
        this.waterDischargeValve = waterDischargeValve;
    }
}

