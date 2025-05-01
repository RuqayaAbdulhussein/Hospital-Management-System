import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
//this starts the desktop app which redirects to the user login
    @Override
    public void start(Stage primaryStage) {
        //this.primaryStage = primaryStage;
        userLogin login = new userLogin(primaryStage);
        login.initializeComponents();
    }

    public static void main(String[] args) {
        launch(args);
    }
}