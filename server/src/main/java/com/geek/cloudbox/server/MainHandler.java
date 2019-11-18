package com.geek.cloudbox.server;

import com.geek.cloudbox.common.messages.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.geek.cloudbox.common.messages.AbstractMessage.*;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            AbstractMessage am = (AbstractMessage) msg;
            if (am.isTypeOf(MsgType.FILE_REQUEST)) {
                FileRequest fr = (FileRequest) msg;
                sendFile(ctx, fr);
            } else if (am.isTypeOf(MsgType.UPLOAD_REQUEST)) {
                UploadRequest ur = (UploadRequest) msg;
                System.out.println("Trying to UL file: " + ur.getPathString());
                ctx.writeAndFlush(new AcceptMessage(ur.getPathString()));
            } else if (am.isTypeOf(MsgType.FILE_MESSAGE)) {
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
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void receiveFile(FileMessage fm) throws IOException {
        Path filePath = Paths.get("testFiles/cloudStorage/" + fm.getRelativeToRootPath());
        Path folderForFile = filePath.subpath(0, filePath.getNameCount() - 1);
        if (!Files.exists(folderForFile))
            Files.createDirectories(folderForFile);

        if (fm.getPartNumber() == 1)
            Files.write(filePath, fm.getData(), StandardOpenOption.CREATE);
        else
            Files.write(filePath, fm.getData(), StandardOpenOption.APPEND);

        if (fm.getPartNumber() == fm.getParts())
            System.out.println("Upload complete: " + fm.getPathString());
    }

    private void sendFile(ChannelHandlerContext ctx, FileRequest fr) throws IOException {
        System.out.println("Trying to DL file: " + fr.getFilename());
        if (Files.exists(Paths.get(fr.getFilename()))) {
            FileMessage fm = new FileMessage(Paths.get(fr.getFilename()));
            Path path = Paths.get(fm.getPathString());

            InputStream is = Files.newInputStream(path);
            FileMessage out = new FileMessage(path);
            byte[] data = new byte[FileMessage.PART_SIZE];
            int dataSize;
            while (is.available() > 0) {
                dataSize = is.read(data);
                if (dataSize == FileMessage.PART_SIZE){
                    out.setData(data);
                    ctx.writeAndFlush(out);
                } else {
                    byte[] lastData = new byte[dataSize];
                    System.arraycopy(data, 0, lastData, 0, dataSize);
                    out.setData(lastData);
                    ctx.writeAndFlush(out);
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

    private void sendFile1(ChannelHandlerContext ctx, Path path) throws IOException {

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
