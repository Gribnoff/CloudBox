package com.geek.cloudbox.common.messages;

public class AuthFailMessage extends AbstractMessage{

    public AuthFailMessage() {
        this.type = MsgType.AUTH_FAIL;
    }
}
