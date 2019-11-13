package com.geek.cloudbox.server;

import com.geek.cloudbox.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.geek.cloudbox.common.AbstractMessage.*;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            AbstractMessage am = (AbstractMessage) msg;
            if (am.isTypeOf(MsgType.FILE_REQUEST)) {
                FileRequest fr = (FileRequest) msg;
                System.out.println("Trying to DL file: " + fr.getFilename());
                if (Files.exists(Paths.get(fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get(fr.getFilename()));
                    ctx.writeAndFlush(fm);
                }
            } else if (am.isTypeOf(MsgType.UPLOAD_REQUEST)) {
                UploadRequest ur = (UploadRequest) msg;
                System.out.println("Trying to UL file: " + ur.getPathString());
                ctx.writeAndFlush(new AcceptMessage(ur.getPathString()));
            } else if (am.isTypeOf(MsgType.FILE_MESSAGE)) {
                FileMessage fm = (FileMessage) am;
                Path filePath = Paths.get("testFiles/cloudStorage/" + fm.getRealtiveToRootPath());
                Path folderForFile = filePath.subpath(0, filePath.getNameCount() - 1);
                if (!Files.exists(folderForFile))
                    Files.createDirectories(folderForFile);
                Files.write(filePath, fm.getData(), StandardOpenOption.CREATE);
                System.out.println("Upload complete: " + fm.getPathString());
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
            } */
            else if (am.isTypeOf(MsgType.DELETE_REQUEST)) {
                DeleteRequest dr = (DeleteRequest) am;
                Path path = Paths.get(dr.getFilename());
                System.out.println("Trying to Delete file: " + path);
                if (Files.exists(path)) {
                    Files.delete(path);
                    System.out.println("File Deleted!");
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
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
