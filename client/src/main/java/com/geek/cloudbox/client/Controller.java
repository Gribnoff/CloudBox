package com.geek.cloudbox.client;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    /*
    TextInputDialog dialog = new TextInputDialog(file.getName());
    dialog.setTitle("Загрузка файла");
    dialog.setHeaderText("Загрузка файла");
    dialog.setContentText("Укажите новое имя файла:");
    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()) {

    FileChooser
     */

    @FXML
    ListView<Path> localStorage, cloudStorage;

    @FXML
    ListView<String> simpleListView;

    @FXML
    Label filesDragAndDrop, labelDragWindow;

    @FXML
    VBox mainVBox;

    @FXML
    StackPane mainStackPane;

    @FXML
    Button btnShowSelectedElement;

    SimpleBooleanProperty btnDisabled = new SimpleBooleanProperty(false);
    Comparator<Path> fileListComparator = new Comparator<Path>() {
        @Override
        public int compare(Path p1, Path p2) {
            return Boolean.compare(Files.isDirectory(p2), Files.isDirectory(p1));
        }
    };

    // Выполняется при старте контроллера
    // Для работы этого метода необходимо чтобы контроллер реализовал интерфейс Initializable
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLocalStorageListView();
        initializeCloudStorageListView();
//        initializeDragAndDropLabel();
//        initializeWindowDragAndDropLabel();
        initializeSceneStyle();
//        initializeSimpleListView();
//        btnShowSelectedElement.disableProperty().bind(btnDisabled);
    }

    // Показывает Alert с возможностью нажатия одной из двух кнопок
    public void btnShowAlert(ActionEvent actionEvent) {
        // Создаем Alert, указываем текст и кнопки, которые на нем должны быть
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you agree?", ButtonType.OK, ButtonType.CANCEL);
        // showAndWait() показывает Alert и блокирует остальное приложение пока мы не закроем Alert
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().getText().equals("OK")) {
            System.out.println("You clicked OK");
        } else if (result.get().getText().equals("Cancel")) {
            System.out.println("You clicked Cancel");
        }
    }

    public void initializeLocalStorageListView() {
        localStorage.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        try {
            Files.list(Paths.get("testFiles/localStorage")).sorted(fileListComparator).forEach(localStorage.getItems()::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        localStorage.setCellFactory(storageListView -> new StorageListCell());
    }

    public void initializeCloudStorageListView() {
        cloudStorage.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        try {
            Files.list(Paths.get("testFiles/cloudStorage")).sorted(fileListComparator).forEach(cloudStorage.getItems()::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cloudStorage.setCellFactory(storageListView -> new StorageListCell());
    }

    public void initializeDragAndDropLabel() {
        filesDragAndDrop.setOnDragOver(event -> {
            if (event.getGestureSource() != filesDragAndDrop && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        filesDragAndDrop.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                filesDragAndDrop.setText("");
                for (File o : db.getFiles()) {
                    filesDragAndDrop.setText(filesDragAndDrop.getText() + o.getAbsolutePath() + " ");
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void btnShowModal(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            LoginController lc = (LoginController) loader.getController();
            lc.id = 100;
            lc.backController = this;

            stage.setTitle("JavaFX Autorization");
            stage.setScene(new Scene(root, 400, 200));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    double dragDeltaX, dragDeltaY;

    public void initializeWindowDragAndDropLabel() {
        Platform.runLater(() -> {
            Stage stage = (Stage) mainVBox.getScene().getWindow();

            labelDragWindow.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    // record a delta distance for the drag and drop operation.
                    dragDeltaX = stage.getX() - mouseEvent.getScreenX();
                    dragDeltaY = stage.getY() - mouseEvent.getScreenY();
                }
            });
            labelDragWindow.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    stage.setX(mouseEvent.getScreenX() + dragDeltaX);
                    stage.setY(mouseEvent.getScreenY() + dragDeltaY);
                }
            });
        });
    }

    public void initializeSceneStyle() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainStackPane.setPadding(new Insets(20, 20, 20, 20));
                mainStackPane.getChildren().get(0).setEffect(new DropShadow(15, Color.BLACK));
            }
        });
    }

    public void initializeSimpleListView() {
        simpleListView.getItems().addAll("Java", "Core", "List", "View");

    }

    public void btnExit(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void btnShow2SceneStage(ActionEvent actionEvent) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/Scene1.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printSelectedItemInListView(ActionEvent actionEvent) {
        System.out.println(simpleListView.getSelectionModel().getSelectedItem());
    }

    public void changeBindedBoolean(ActionEvent actionEvent) {
        btnDisabled.set(!btnDisabled.get());
    }


    //TODO
    public void uploadFiles(ActionEvent actionEvent) {

    }

    public void downloadFiles(ActionEvent actionEvent) {

    }

    public void renameFile(ActionEvent actionEvent) {

    }

    public void deleteFiles(ActionEvent actionEvent) {

    }

    public void logout(ActionEvent actionEvent) {

    }
}