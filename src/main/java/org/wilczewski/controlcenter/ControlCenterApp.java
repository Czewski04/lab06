package org.wilczewski.controlcenter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ControlCenterApp {
    public static void main(String[] args) throws IOException, InterruptedException {
        ControlCenterService controlCenterService = new ControlCenterService(999, "localhost");
        controlCenterService.startServer();
        TimeUnit.SECONDS.sleep(5);
        controlCenterService.getRetentionBasinWaterDischarge(400, "localhost");
        controlCenterService.setRetentionBasinWaterDischarge(400, "localhost", 666);
        controlCenterService.getRetentionBasinWaterDischarge(400, "localhost");
    }
}
