package com.midori.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

public class Main extends Application {
    static MainController Controller;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Font.loadFont(new FileInputStream(new File("/home/anon/IdeaProjects/midori/src/com/midori/ui/fonts/RobotoMono-Regular.ttf")), 10);
        Font.loadFont(new FileInputStream(new File("/home/anon/IdeaProjects/midori/src/com/midori/ui/fonts/RobotoMono-Bold.ttf")), 10);
        //todo: fonts

        Parent root = loader.load();
        Controller = loader.getController();
        primaryStage.setTitle("midori");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
