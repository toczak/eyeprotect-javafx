package com.potoczak;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static javafx.application.Application.launch;

public class Main extends Application{
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/FXML/Main.fxml"));
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("EyeProtect");
        stage.setResizable(false);
        stage.getIcons().add(new Image("icon.png"));
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.show();


    }
}
