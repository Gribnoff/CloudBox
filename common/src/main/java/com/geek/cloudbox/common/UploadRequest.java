package com.geek.cloudbox.common;

public class UploadRequest extends AbstractMessage {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public UploadRequest(String filename) {
        this.filename = filename;
        this.type = MsgType.UPLOAD_REQUEST;
    }
}
