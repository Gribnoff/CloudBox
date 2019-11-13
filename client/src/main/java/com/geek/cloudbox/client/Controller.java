package com.geek.cloudbox.client;

import com.geek.cloudbox.common.*;
import com.geek.cloudbox.common.AbstractMessage.MsgType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Controller implements Initializable {

    @FXML
    ListView<Path> localStorage, cloudStorage;

    @FXML
    Label filesDragAndDrop, labelDragWindow;

    @FXML
    VBox mainVBox;

    @FXML
    StackPane mainStackPane;

    private Comparator<Path> fileListComparator = (p1, p2) -> Boolean.compare(Files.isDirectory(p2), Files.isDirectory(p1));

    private static Deque<Path> currentLocalDir = new ArrayDeque<>();
    private static Deque<Path> currentCloudDir = new ArrayDeque<>();

    static {
        clearLocalStorage();
        clearCloudStorage();
    }

    private static void clearCloudStorage() {
        currentCloudDir.clear();
        currentCloudDir.add(Paths.get("testFiles/cloudStorage"));
    }

    private static void clearLocalStorage() {
        currentLocalDir.clear();
        currentLocalDir.add(Paths.get("testFiles/localStorage"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLocalStorageListView();
        initializeCloudStorageListView();
        initializeDragAndDropLabel();
//        initializeWindowDragAndDropLabel();
//        initializeSceneStyle();
//        initializeSimpleListView();
//        btnShowSelectedElement.disableProperty().bind(btnDisabled);
        Network.start();
        initializeListeningThread();
    }

    private void initializeListeningThread() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am.isTypeOf(MsgType.FILE_MESSAGE)) {
                        FileMessage fm = (FileMessage) am;
                        Path filePath = Paths.get("testFiles/localStorage/" + fm.getRealtiveToRootPath());
                        Path folderForFile = filePath.subpath(0, filePath.getNameCount() - 1);
                        if (!Files.exists(folderForFile))
                            Files.createDirectories(folderForFile);
                        Files.write(filePath, fm.getData(), StandardOpenOption.CREATE);
                        System.out.println("Download complete: " + fm.getPathString());
                        refreshLocalFilesList();
                    } else if (am.isTypeOf(MsgType.ACCEPT)) {
                        System.out.println("Upload accepted by server");
                        AcceptMessage acm = (AcceptMessage) am;
                        Path path = Paths.get(acm.getPathString());
                        Network.sendMsg(new FileMessage(path));
                        System.out.println("Upload complete: " + acm.getPathString());
                    } else if (am.isTypeOf(MsgType.FILE_LIST)) {
                        FileListMessage flm = (FileListMessage) am;
                        clearCloudStorage();
                        while (flm.getFileList().size() > 0) {
                            currentCloudDir.add(flm.getFileList().pollFirst());
                        }
                        refreshCloudFilesList();
                    } else
                        System.out.println(am);
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void btnShowAlert(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you agree?", ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().getText().equals("OK")) {
            System.out.println("You clicked OK");
        } else if (result.get().getText().equals("Cancel")) {
            System.out.println("You clicked Cancel");
        }
    }

    private void initializeLocalStorageListView() {
        localStorage.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        refreshLocalFilesList();
        localStorage.setCellFactory(storageListView -> new StorageListCell());
    }

    private void initializeCloudStorageListView() {
        cloudStorage.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        refreshCloudFilesList();
        cloudStorage.setCellFactory(storageListView -> new StorageListCell());
    }

    @FXML
    private void refreshLocalFilesList() {
        updateUI(() -> {
            try {
                localStorage.getItems().clear();
//                if (currentLocalDir.size() > 1) {
//                    localStorage.getItems().add(currentLocalDir.peek());
//                }
                if (currentLocalDir.peekLast() != null) {
                    Files.list(Paths.get(currentLocalDir.peekLast().toString()))
                            .sorted(fileListComparator)
                            .forEach(localStorage.getItems()::add);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void refreshCloudFilesList() {
        updateUI(() -> {
            try {
                cloudStorage.getItems().clear();
                if (currentCloudDir.peekLast() != null) {
                    Files.list(Paths.get(currentCloudDir.peekLast().toString()))
                            .sorted(fileListComparator)
                            .forEach(cloudStorage.getItems()::add);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void updateUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
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
            LoginController lc = loader.getController();
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

            labelDragWindow.setOnMousePressed(mouseEvent -> {
                dragDeltaX = stage.getX() - mouseEvent.getScreenX();
                dragDeltaY = stage.getY() - mouseEvent.getScreenY();
            });
            labelDragWindow.setOnMouseDragged(mouseEvent -> {
                stage.setX(mouseEvent.getScreenX() + dragDeltaX);
                stage.setY(mouseEvent.getScreenY() + dragDeltaY);
            });
        });
    }

    private void initializeSceneStyle() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainStackPane.setPadding(new Insets(20, 20, 20, 20));
                mainStackPane.getChildren().get(0).setEffect(new DropShadow(15, Color.BLACK));
            }
        });
    }

    public void btnExit() {
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

    public void uploadFiles() {
        localStorage.getSelectionModel().getSelectedItems().forEach(path -> {
            Network.sendMsg(new UploadRequest(path.toString()));
            System.out.println("Trying to UL file: " + path);
        });
        refreshCloudFilesList();
    }

    public void downloadFiles() {
        cloudStorage.getSelectionModel().getSelectedItems().forEach(filename -> {
            Network.sendMsg(new FileRequest(filename.toString()));
            System.out.println("Trying to DL file: " + filename);
        });
        cloudStorage.getSelectionModel().clearSelection();
        refreshLocalFilesList();
    }

    public void renameFile() {
        Path src = cloudStorage.getSelectionModel().getSelectedItem();

        TextInputDialog dialog = new TextInputDialog(src.getFileName().toString());

        dialog.setTitle("Rename file");
        dialog.setHeaderText("Enter new file name");

        Optional<String> result;
        AtomicBoolean moved = new AtomicBoolean(false);
        do {
            result = dialog.showAndWait();

            result.ifPresent(name -> {
                if (!Files.exists(Paths.get(src.toString()).subpath(0, src.getNameCount() - 1).resolve(name))) {
                    try {
                        Files.move(src, src.resolveSibling(name));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    moved.set(true);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "File already exists!");
                    alert.showAndWait();
                }
            });
        } while (!moved.get() && result.isPresent());
    }

    public void deleteFiles() {
        cloudStorage.getSelectionModel().getSelectedItems().forEach(filename -> {
            Network.sendMsg(new DeleteRequest(filename.toString()));
            System.out.println("Trying to Delete file: " + filename);
        });
        refreshCloudFilesList();
    }

    //TODO
    public void logout() {

    }

    public void localStorageAction(@NotNull MouseEvent mouseEvent) {
        Path selected = localStorage.getSelectionModel().getSelectedItem();
        if (mouseEvent.getClickCount() == 2 && Files.isDirectory(selected)) {
            currentLocalDir.add(selected);
            refreshLocalFilesList();
        }
    }

    public void cloudStorageAction(@NotNull MouseEvent mouseEvent) {
        Path selected = cloudStorage.getSelectionModel().getSelectedItem();
        if (mouseEvent.getClickCount() == 2 && Files.isDirectory(selected)) {
            currentCloudDir.add(selected);
            refreshCloudFilesList();
        }
    }
}
