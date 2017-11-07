package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //load main form
        FXMLLoader mainLoader = new FXMLLoader();
        mainLoader.setLocation(Main.class.getResource("mainForm.fxml"));
        primaryStage.setTitle("School");
        primaryStage.setScene(new Scene(mainLoader.load(), 800, 600));
        MainFormController mainFormController = mainLoader.getController();
        //load login form
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("loginForm.fxml"));
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");
        loginStage.setScene(new Scene(loader.load(), 300, 200));
        loginStage.initOwner(primaryStage);
        LoginFormController loginFormController = loader.getController();
        loginFormController.setLoginStage(loginStage);
        loginFormController.setPrimaryStage(primaryStage);
        loginFormController.setMainFormController(mainFormController);
        loginStage.showAndWait();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
