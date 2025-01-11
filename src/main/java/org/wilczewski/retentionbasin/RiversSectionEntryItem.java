package org.wilczewski.retentionbasin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RiversSectionEntryItem {
    private final StringProperty host;
    private final IntegerProperty port;

    public RiversSectionEntryItem(String host, int port) {
        this.host = new SimpleStringProperty(host);
        this.port = new SimpleIntegerProperty(port);
    }

    public String getHost() {
        return host.get();
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public StringProperty hostProperty() {
        return host;
    }

    public int getPort() {
        return port.get();
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public IntegerProperty portProperty() {
        return port;
    }
}


