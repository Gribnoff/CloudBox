package com.geek.cloudbox.common.messages;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    public enum MsgType {
        LOGIN,
        AUTH_OK,
        AUTH_FAIL,
        FILE,
        FILE_REQUEST,
        UPLOAD_REQUEST,
        ACCEPT,
        DECLINE,
        DELETE_REQUEST,
        FILE_LIST,
        FILE_LIST_REQUEST,
    }

    protected MsgType type;

    public MsgType getType() {
        return type;
    }

    public boolean isTypeOf(MsgType type) {
        return this.type.equals(type);
    }
}