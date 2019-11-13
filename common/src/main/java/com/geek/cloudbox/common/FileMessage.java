package com.geek.cloudbox.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FileMessage extends AbstractMessage {
    private String pathString;
    private byte[] data;

    public String getPathString() {
        return pathString;
    }

    public String getRealtiveToRootPath() {
        Path path = Paths.get(pathString);
        path = path.subpath(2, path.getNameCount());
        return path.toString();
    }

    public byte[] getData() {
        return data;
    }

    public FileMessage(Path path) throws IOException {
        pathString = path.toString();
        data = Files.readAllBytes(path);
        type = MsgType.FILE_MESSAGE;
    }
}
