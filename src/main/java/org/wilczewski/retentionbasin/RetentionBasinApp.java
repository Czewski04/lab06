package org.wilczewski.retentionbasin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RetentionBasinApp {
    public static void main(String[] args) throws IOException, InterruptedException {
        RetentionBasinService rBasin = new RetentionBasinService(1000, 400, "localhost");
        rBasin.startServer();
        TimeUnit.SECONDS.sleep(5);
        rBasin.sendRetentionBasinData("localhost", 500);
        rBasin.sendRetentionBasinData("localhost", 999);
    }
}
