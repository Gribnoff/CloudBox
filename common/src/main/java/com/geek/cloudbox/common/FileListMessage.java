package com.geek.cloudbox.common;

import java.nio.file.Path;
import java.util.Deque;

public class FileListMessage extends AbstractMessage {
    private Deque<Path> fileList;

    public Deque<Path> getFileList() {
        return fileList;
    }

    public FileListMessage(Deque<Path> fileList) {
        this.fileList = fileList;
        this.fileList.pollFirst();
        this.fileList.pollFirst();

        this.type = MsgType.FILE_LIST;
    }
}
