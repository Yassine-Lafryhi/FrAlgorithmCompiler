package AlgoC;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    public static Stage ps;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene s = new Scene(root);
        s.setFill(Color.TRANSPARENT);
        s.getStylesheets().add("Style.css");
        primaryStage.setScene(s);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
        ps = primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
