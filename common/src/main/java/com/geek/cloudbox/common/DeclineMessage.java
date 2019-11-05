package com.geek.cloudbox.common;

public class DeclineMessage extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public DeclineMessage(String filename) {
        this.filename = filename;
        this.type = MsgType.DECLINE;
    }
}
