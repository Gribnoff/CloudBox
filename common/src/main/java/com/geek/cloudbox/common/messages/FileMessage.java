package com.geek.cloudbox.common.messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMessage extends AbstractMessage {
    public static final int PART_SIZE = 8096;
    private String pathString;
    private byte[] data;
    private int parts;
    private int partNumber;

    public String getPathString() {
        return pathString;
    }

    public String getRelativeToRootPath() {
        Path path = Paths.get(pathString);
        path = path.subpath(2, path.getNameCount());
        return path.toString();
    }

    public byte[] getData() {
        return data;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public int getParts() {
        return parts;
    }

    public void setData(byte[] data) {
        this.data = data;
        partNumber++;
    }

    public FileMessage(Path path) throws IOException {
        pathString = path.toString();
        data = new byte[PART_SIZE];
        partNumber = 0;
        parts = (int)(Files.size(path) / PART_SIZE) + 1;
        type = MsgType.FILE_MESSAGE;
    }
}
