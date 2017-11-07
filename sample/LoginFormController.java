package sample;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController
{
    private Md5 md5 = new Md5();
    private Stage loginStage;
    private Stage primaryStage;
    private MainFormController mainFormController;
    @FXML
    private TextField loginTextField;
    @FXML
    private PasswordField passwordTextField;

    public void setLoginStage(Stage loginStage)
    {
        this.loginStage = loginStage;
    }

    public void setPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }

    public void setMainFormController(MainFormController mainFormController) {
        this.mainFormController = mainFormController;
    }

    @FXML
    private void loginButtonClick()
    {
        int userId = -1,userRang=-1;
        Db db = new Db();
        try {
            ResultSet res = db.statement.executeQuery("SELECT id,rang FROM school.users WHERE login='"+loginTextField.getText()+"' AND password='"+md5.crypt(passwordTextField.getText())+"'");
            if(res.next())
            {
                userId = Integer.valueOf(res.getString("id"));
                userRang = Integer.valueOf(res.getString("rang"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(userId>0)
        {
            mainFormController.setUserId(userId);
            mainFormController.setUserRang(userRang);
            loginStage.close();
            mainFormController.initializeTabs();
            primaryStage.show();
        }
        else
        {
            loginTextField.setStyle("-fx-border-color: red");
            passwordTextField.setStyle("-fx-border-color: red");
        }
    }

    @FXML
    private void loginTextFieldTextChanged(KeyEvent e)
    {
        if(!e.getCharacter().equals("\r"))
        {
            loginTextField.setStyle("");
            passwordTextField.setStyle("");
        }
    }

    @FXML
    private void passwordTextFieldTextChanged(KeyEvent e)
    {
        if(!e.getCharacter().equals("\r"))
        {
            loginTextField.setStyle("");
            passwordTextField.setStyle("");
        }
    }

    @FXML
    private void loginFormKeyPressed(KeyEvent e)
    {
        if(e.getCode().equals(KeyCode.ENTER))
            loginButtonClick();
    }
}
