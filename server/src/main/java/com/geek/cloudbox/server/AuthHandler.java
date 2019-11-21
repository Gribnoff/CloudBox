package com.geek.cloudbox.server;

import com.geek.cloudbox.common.messages.AbstractMessage;
import com.geek.cloudbox.common.messages.AuthFailMessage;
import com.geek.cloudbox.common.messages.AuthOKMessage;
import com.geek.cloudbox.common.messages.LoginMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import static com.geek.cloudbox.common.messages.AbstractMessage.*;

public class AuthHandler extends ChannelInboundHandlerAdapter {
    private boolean authorized;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AbstractMessage am = (AbstractMessage) msg;
        if (!authorized) {
            if (am.isTypeOf(MsgType.LOGIN)) {
                LoginMessage lm = (LoginMessage) msg;

                String username = DBService.getNameByLoginAndPass(lm.getLogin(), lm.getPass());
                if (username == null)
                    ctx.writeAndFlush(new AuthFailMessage());
                else {
                    ctx.writeAndFlush(new AuthOKMessage()).await();
                    authorized = true;
                    ctx.pipeline().addLast(new MainHandler(username));
                }
            } else {
                ReferenceCountUtil.release(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
