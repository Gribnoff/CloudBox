package com.geek.cloudbox.common;

import java.nio.file.Path;

public class FileListRequest extends AbstractMessage {
    private Path path;

    public Path getPath() {
        return path;
    }

    public FileListRequest(Path path) {
        this.path = path;
        this.type = MsgType.FILE_LIST_REQUEST;
    }
}
