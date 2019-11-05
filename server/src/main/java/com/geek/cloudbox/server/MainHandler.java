package com.geek.cloudbox.server;

import com.geek.cloudbox.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.IntStream;

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
                System.out.println("Trying to UL file: " + ur.getFilename());
                ctx.writeAndFlush(new AcceptMessage(ur.getFilename()));
            } else if (am.isTypeOf(MsgType.FILE_MESSAGE)) {
                FileMessage fm = (FileMessage) am;
                Files.write(Paths.get("testFiles/cloudStorage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                System.out.println("Upload complete");
            } else if (am.isTypeOf(MsgType.FILE_LIST_REQUEST)) {
                FileListRequest flr = (FileListRequest) am;
                Deque<Path> current = new ArrayDeque<>();
                String[] path = flr.getPath().split("/");
                IntStream.range(2, path.length)
                        .mapToObj(i -> Paths.get(path[i]))
                        .forEach(current::add);
                ctx.writeAndFlush(new FileListMessage(current));
            } else if (am.isTypeOf(MsgType.DELETE_REQUEST)) {
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
