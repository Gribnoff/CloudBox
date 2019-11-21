package com.geek.cloudbox.server;

import com.geek.cloudbox.common.messages.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static com.geek.cloudbox.common.messages.AbstractMessage.*;

public class MainHandler extends ChannelInboundHandlerAdapter {
    private String clientName;

    private final static String DEFAULT_CLOUD_FOLDER = "testFiles/cloudStorage/";
    private final String CLIENT_FOLDER_PREFIX = clientName + "/";
    private byte[] data = new byte[FileMessage.PART_SIZE];


    public MainHandler(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            AbstractMessage am;
            if (msg == null)
                return;

            handleMessage(ctx, (AbstractMessage) msg);

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void handleMessage(ChannelHandlerContext ctx, AbstractMessage am) throws IOException {
        if (am.isTypeOf(MsgType.FILE_REQUEST)) {
            FileRequest fr = (FileRequest) am;
            System.out.println("Trying to DL file: " + fr.getFilename());
            Path path = Paths.get(fr.getFilename());
            FileMessage fm = new FileMessage(path);
            sendFile(ctx, fm, path);
        } else if (am.isTypeOf(MsgType.UPLOAD_REQUEST)) {
            UploadRequest ur = (UploadRequest) am;
            System.out.println("Trying to UL file: " + ur.getPathString());
            ctx.writeAndFlush(new AcceptMessage(ur.getPathString()));
        } else if (am.isTypeOf(MsgType.FILE)) {
            FileMessage fm = (FileMessage) am;
            receiveFile(fm);
//                sendFileList(ctx, folder);
        } /*
        else if (am.isTypeOf(MsgType.FILE_LIST_REQUEST)) {
            FileListRequest flr = (FileListRequest) am;
            Deque<Path> current = new ArrayDeque<>();
            String[] path = flr.getPath().split("/");
            IntStream.range(2, path.length)
                    .mapToObj(i -> Paths.get(path[i]))
                    .forEach(current::add);
            ctx.writeAndFlush(new FileListMessage(current));
        } */ else if (am.isTypeOf(MsgType.DELETE_REQUEST)) {
            DeleteRequest dr = (DeleteRequest) am;
            deleteFile(dr);
        }
    }

    private void receiveFile(FileMessage fm) throws IOException {
        Path filePath = Paths.get(DEFAULT_CLOUD_FOLDER + CLIENT_FOLDER_PREFIX + fm.getRelativeToRootPath());
        Path folderForFile = filePath.subpath(0, filePath.getNameCount() - 1);
        if (!Files.exists(folderForFile))
            Files.createDirectories(folderForFile);

        if (Files.isDirectory(filePath))
            Files.createDirectories(filePath);
        else {
            if (fm.getPartNumber() == 1)
                Files.write(filePath, fm.getData(), StandardOpenOption.CREATE);
            else
                Files.write(filePath, fm.getData(), StandardOpenOption.APPEND);
        }

        if (fm.getPartNumber() == fm.getParts())
            System.out.println("Upload complete: " + fm.getPathString());
    }

    private void sendFile(ChannelHandlerContext ctx, FileMessage fm, Path path) throws IOException {
        if (Files.exists(path)) {
            if (Files.isDirectory(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        sendFile(ctx, fm, file);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                InputStream is = Files.newInputStream(path);
                int dataSize;
                while (is.available() > 0) {
                    dataSize = is.read(data);
                    if (dataSize == FileMessage.PART_SIZE) {
                        fm.setData(data);
                        ctx.writeAndFlush(fm);
                    } else {
                        byte[] lastData = new byte[dataSize];
                        System.arraycopy(data, 0, lastData, 0, dataSize);
                        fm.setData(lastData);
                        ctx.writeAndFlush(fm);
                    }
                }
            }
        }
    }

    private void deleteFile(DeleteRequest dr) throws IOException {
        Path path = Paths.get(dr.getFilename());
        System.out.println("Trying to Delete file: " + path);
        if (Files.exists(path)) {
            Files.delete(path);
            System.out.println("File Deleted!");
        }
    }

    private void sendFileList(ChannelHandlerContext ctx, Path currentFolder) {

//        ctx.writeAndFlush(new FileListMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
