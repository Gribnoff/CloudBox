package com.geek.cloudbox.common.messages;

public class LoginMessage extends AbstractMessage {
    private String login;
    private String pass;

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public LoginMessage(String login, String pass) {
        this.login = login;
        this.pass = pass;
        this.type = MsgType.LOGIN;
    }
}
