package com.midori.ui;

import com.midori.bot.Prefs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    static MainController Controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.setProperty("prism.allowhidpi", "false");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Font.loadFont(getClass().getResourceAsStream("/com/midori/ui/res/fonts/RobotoMono-Regular.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/com/midori/ui/res/fonts/RobotoMono-Bold.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/com/midori/ui/res/fonts/Roboto-Regular.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/com/midori/ui/res/fonts/Roboto-Bold.ttf"), 10);

        Parent root = loader.load();
        root.getStylesheets().add("/com/midori/ui/res/style.css");
        Controller = loader.getController();
        primaryStage.setTitle("midori (" + Prefs.VERSION+") - [AUTO-BOOST ACTIVATED]");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
