package com.geek.cloudbox.common.messages;

public class AuthOKMessage extends AbstractMessage{
    public AuthOKMessage() {
        this.type = MsgType.AUTH_OK;
    }
}
