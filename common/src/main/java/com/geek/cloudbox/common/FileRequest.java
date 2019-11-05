package com.geek.cloudbox.common;

public class FileRequest extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public FileRequest(String filename) {
        this.filename = filename;
        this.type = MsgType.FILE_REQUEST;
    }
}
