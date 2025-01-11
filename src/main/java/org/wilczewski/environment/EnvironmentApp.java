package org.wilczewski.environment;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EnvironmentApp {
    public static void main(String[] args) throws IOException, InterruptedException {
        EnvironmentService environmentService = new EnvironmentService(998, "localhost");
        environmentService.startServer();
        TimeUnit.SECONDS.sleep(5);
        environmentService.setRainfall(500, "localhost", 100);
    }
}
