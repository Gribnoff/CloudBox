package com.geek.cloudbox.common.messages;

public class AcceptMessage extends AbstractMessage {
    private String pathString;

    public String getPathString() {
        return pathString;
    }

    public AcceptMessage(String pathString) {
        this.pathString = pathString;
        this.type = MsgType.ACCEPT;
    }
}
