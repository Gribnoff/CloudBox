package com.geek.cloudbox.common.messages;

public class DeleteRequest extends AbstractMessage{
    private String filename;

    public String getFilename() {
        return filename;
    }

    public DeleteRequest(String filename) {
        this.filename = filename;
        this.type = MsgType.DELETE_REQUEST;
    }
}
