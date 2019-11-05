package com.geek.cloudbox.common;

public class FileListRequest extends AbstractMessage {
    private String path;

    public String getPath() {
        return path;
    }

    public FileListRequest(String path) {
        this.path = path;
        this.type = MsgType.FILE_LIST_REQUEST;
    }
}
