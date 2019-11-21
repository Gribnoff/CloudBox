package com.geek.cloudbox.client;

import com.geek.cloudbox.client.test.LoginWindowController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.geek.cloudbox.client.test.ClientStart.*;


public class RegistrationController {
//    @FXML
//    VBox window;
//    @FXML
//    TextField loginField, nicknameField, passField, passCheckField;
//    @FXML
//    Pane registrationPane;
//    @FXML
//    Label loginTaken, nicknameTaken, passWrong, passNotMatch, spaceProblem, emptyField;
//
//    private Set<Label> labelSet;
//
//    private static RegistrationWindow registrationWindow;
//    private static LoginWindow loginWindow;
//
///**
//     * синглтон для окна авторизации
//     */
//
//    static RegistrationWindow getRegistrationWindowInstance() {
//        if (registrationWindow == null) {
//            try {
//                registrationWindow = new RegistrationWindow();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return registrationWindow;
//    }
//
///**
//     * переход на окно авторизации
//     */
//
//    @FXML
//    private void goToLoginPage() {
//        setRegistrationWindowVisible(false);
//        loginWindow.getController().setLoginWindowVisible(true);
//    }
//
///**
//     * взаимодействие пользователя с чатом и другими пользователями
//     */
//
//    private void register() {
//        new Thread(() -> {
//            try {
//                register:
//                while (true) {
//                    String str = in.readUTF();
//                    System.out.println(str);
//
//                    switch (str) {
//                        case "/registrationPassed":
//                            loginWindow = LoginWindowController.getLoginWindowInstance();
//                            setRegistrationWindowVisible(false);
//                            break register;
//                        case "/registrationSpaceProblem":
//                            showLabel(spaceProblem);
//                            break;
//                        case "/registrationLoginTaken":
//                            showLabel(loginTaken);
//                            break;
//                        case "/registrationNicknameTaken":
//                            showLabel(nicknameTaken);
//                            break;
//                        case "/registrationPassWrong":
//                            showLabel(passWrong);
//                            break;
//                        case "/registrationPassNotMatch":
//                            showLabel(passNotMatch);
//                            break;
//                    }
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                loginWindow = LoginWindowController.getLoginWindowInstance();
//                goToLoginPage();
//            }
//        }).start();
//
//    }
//
///**
//     * отправка сообщения на сервер
//     *
//     * @param text сообщение для отправки
//     */
//
//    private void sendMessage(String text) {
//        try {
//            if (!socket.isClosed()) {
//                out.writeUTF(text);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
///**
//     * окно подтвержения выхода из программы
//     *
//     * @return кнопку которую нажал пользователь(OK или Cancel)
//     */
//
//    private Optional<ButtonType> askForExit() {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Выход");
//        alert.setHeaderText(null);
//        alert.setContentText("Действительно выйти из программы?");
//
//        return alert.showAndWait();
//    }
//
///**
//     * выход из программы
//     */
//
//    public void exit() {
//        if (askForExit().get() == ButtonType.OK) {
//            System.exit(0);
//        }
//    }
//
///**
//     * скрывает или показывает окно регистрации
//     * @param visible true - показывает, false - скрывает
//     */
//
//    void setRegistrationWindowVisible(boolean visible) {
//        Platform.runLater(() -> {
//            Stage stage = (Stage) window.getScene().getWindow();
//            if (visible)
//                stage.show();
//            else
//                stage.hide();
//        });
//    }
//
///**
//     * попытка авторизации
//     */
//
//    @FXML
//    private void registerTry() {
//        if (loginField.getText().isEmpty() || nicknameField.getText().isEmpty() || passField.getText().isEmpty() || passCheckField.getText().isEmpty()) {
//            showLabel(emptyField);
//            return;
//        }
////        if (socket != null && !socket.isClosed())
//            register();
//
//        sendMessage("/registration " + loginField.getText() + " " + nicknameField.getText() + " " + passField.getText() + " " + passCheckField.getText());
//    }
//
//    void initLabelSet() {
//        labelSet = new HashSet<>();
//        {
//            labelSet.add(loginTaken);
//            labelSet.add(nicknameTaken);
//            labelSet.add(passWrong);
//            labelSet.add(passNotMatch);
//            labelSet.add(spaceProblem);
//            labelSet.add(emptyField);
//        }
//    }
//
///**
//     * показывает нужный Label при ошибке регистрации
//     */
//
//    private void showLabel(Label labelToShow) {
//        for (Label label : labelSet) {
//            if (labelToShow == label)
//                label.setVisible(true);
//            else
//                label.setVisible(false);
//        }
//    }
//
//    @FXML
//    private void setDefaultTheme() {
//        window.getStylesheets().clear();
//    }
//
//    @FXML
//    private void setGrassTheme() {
//        window.getStylesheets().clear();
//        window.getStylesheets().add("chat/client/css/grass.css");
//    }
//
//    @FXML
//    private void setSeaTheme() {
//        window.getStylesheets().clear();
//        window.getStylesheets().add("chat/client/css/sea.css");
//    }
//
//    @FXML
//    private void setDarculaTheme() {
//        window.getStylesheets().clear();
//        window.getStylesheets().add("chat/client/css/darcula.css");
//    }
}
