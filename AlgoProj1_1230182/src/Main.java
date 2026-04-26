
import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage stage) {
    	
    	MyArrayList<Task> tasks = new MyArrayList<>();
    	
    	MainScene mainView = new MainScene(stage, tasks);
    	
	    
    	stage.setScene(mainView.getScene());
    	stage.setTitle("Daily Task Optimizer");
    	stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
