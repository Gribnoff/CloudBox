package com.geek.cloudbox.client;

import com.geek.cloudbox.common.messages.AbstractMessage;
import com.geek.cloudbox.common.messages.AuthOKMessage;
import com.geek.cloudbox.common.messages.LoginMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static com.geek.cloudbox.common.messages.AbstractMessage.*;

public class LoginController {
    @FXML
    VBox window;
    @FXML
    TextField loginField, passField;
    @FXML
    Pane rootNode;
    @FXML
    Label loginError;

    @FXML
    private void goToRegistration() {
    }

    @FXML
    private void goToMain() {
        if (!Platform.isFxApplicationThread()) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Parent mainScene = FXMLLoader.load(getClass().getResource("/Main.fxml"));
                    ((Stage) rootNode.getScene().getWindow()).setScene(new Scene(mainScene));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    }

    @FXML
    private void login() {
        if (!Network.checkConnection())
            Network.start();

        waitForAuth();
        Network.sendMsg(new LoginMessage(loginField.getText(), passField.getText()));
        passField.clear();
    }

    private void waitForAuth() {
        new Thread(() -> {
            try {
                AbstractMessage am;
                while (true) {
                    am = Network.readObject();

                    if (am.isTypeOf(MsgType.AUTH_FAIL))
                        loginError.setVisible(true);
                    else if (am.isTypeOf(MsgType.AUTH_OK)) {
                        loginError.setVisible(false);
                        goToMain();
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

    }

    @FXML
    void exit() {
        if (askForExit().get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private Optional<ButtonType> askForExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Выход");
        alert.setHeaderText(null);
        alert.setContentText("Действительно выйти из программы?");

        return alert.showAndWait();
    }
}
