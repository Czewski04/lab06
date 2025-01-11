package org.wilczewski.riversection;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RiverSectionApp {
    public static void main(String[] args) throws IOException, InterruptedException {
        RiverSectionService river = new RiverSectionService(7, 500, "localhost");
        river.startServer();
        TimeUnit.SECONDS.sleep(5);
        river.sendRiverSectionData("localhost", 400);
        river.sendRiverSectionData("localhost", 998);
    }
}
