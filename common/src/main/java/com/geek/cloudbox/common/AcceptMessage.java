package com.geek.cloudbox.common;

public class AcceptMessage extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public AcceptMessage(String filename) {
        this.filename = filename;
        this.type = MsgType.ACCEPT;
    }
}
