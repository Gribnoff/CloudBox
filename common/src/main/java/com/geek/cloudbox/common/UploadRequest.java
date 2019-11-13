package com.geek.cloudbox.common;

public class UploadRequest extends AbstractMessage {
    private String pathString;

    public String getPathString() {
        return pathString;
    }

    public UploadRequest(String pathString) {
        this.pathString = pathString;
        this.type = MsgType.UPLOAD_REQUEST;
    }
}
