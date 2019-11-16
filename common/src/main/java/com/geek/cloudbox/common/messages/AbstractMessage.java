package com.geek.cloudbox.common.messages;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    public enum MsgType {
        FILE_MESSAGE,
        FILE_REQUEST,
        UPLOAD_REQUEST,
        ACCEPT,
        DECLINE,
        DELETE_REQUEST,
        FILE_LIST,
        FILE_LIST_REQUEST,
    }

    MsgType type;

    public MsgType getType() {
        return type;
    }

    public boolean isTypeOf(MsgType type) {
        return this.type.equals(type);
    }
}