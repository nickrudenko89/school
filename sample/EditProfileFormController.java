package sample;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileFormController
{
    private Md5 md5 = new Md5();
    private boolean okButtonClicked = false;
    private Stage editProfileStage;
    private int userId;
    private int userRang;
    private String currentName;
    private String currentPassportNumber;
    private String currentBirthDate;
    private File newUserPhoto = null;
    @FXML
    private ImageView userPhotoImageView;
    @FXML
    private TextField loginTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private PasswordField passwordConfirmTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField passportNumberTextField;
    @FXML
    private DatePicker birthdayDatePicker;

    public boolean getOkButtonClicked() {
        return okButtonClicked;
    }

    public void setEditProfileStage(Stage editProfileStage) {
        this.editProfileStage = editProfileStage;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserRang(int userRang) {
        this.userRang = userRang;
    }

    public void setLoginTextField(String loginTextFieldText) {
        this.loginTextField.setText(loginTextFieldText);
    }

    public void setNameTextField(String nameTextFieldText) {
        this.nameTextField.setText(nameTextFieldText);
    }

    public void setPassportNumberTextField(String passportNumberTextFieldText) {
        this.passportNumberTextField.setText(passportNumberTextFieldText);
    }

    public void setBirthdayDatePicker(String birthDate) {
        this.birthdayDatePicker.setValue(LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public void setUserPhotoImageView(int userId) {
        userPhotoImageView.setImage(new Image(new File("images/"+userId+".jpg").toURI().toString()));
    }

    public void saveCurrentInfo()
    {
        currentName = nameTextField.getText();
        currentPassportNumber = passportNumberTextField.getText();
        currentBirthDate = birthdayDatePicker.getValue().toString();
    }

    @FXML
    private void okButtonClick()
    {
        boolean isPasswordChanged = false, isNameChanged = false, isPassportNumberChanged = false, isBirthDateChanged = false;
        boolean isPasswordCorrect = true, isNameCorrect = true, isPassportNumberCorrect = true;
        if(!passwordTextField.getText().equals(""))
        {
            isPasswordChanged = true;
            if(!passwordTextField.getText().equals(passwordConfirmTextField.getText()))
                isPasswordCorrect = false;
        }
        if(!nameTextField.getText().equals(currentName))
        {
            isNameChanged = true;
            if(nameTextField.getText().equals("") || (nameTextField.getText().replaceAll(" ","")).equals(""))
                isNameCorrect = false;
        }
        if(!passportNumberTextField.getText().equals(currentPassportNumber))
        {
            isPassportNumberChanged = true;
            if(passportNumberTextField.getText().equals("") || (passportNumberTextField.getText().replaceAll(" ","")).equals(""))
                isPassportNumberCorrect = false;
        }
        if(!birthdayDatePicker.getValue().toString().equals(currentBirthDate))
        {
            isBirthDateChanged = true;
        }
        if(isPasswordCorrect && isNameCorrect && isPassportNumberCorrect)
        {
            Db db = new Db();
            try {
                if(isPasswordChanged)
                {
                    db.statement.executeUpdate("UPDATE users SET password='"+md5.crypt(passwordTextField.getText())+"' WHERE id="+userId);
                }
                if(isNameChanged)
                {
                    if(userRang == 2)
                        db.statement.executeUpdate("UPDATE teachers SET name='"+nameTextField.getText()+"' WHERE id_user="+userId);
                    else if(userRang == 3)
                        db.statement.executeUpdate("UPDATE students SET name='"+nameTextField.getText()+"' WHERE id_user="+userId);
                }
                if(isPassportNumberChanged)
                {
                    if(userRang == 2)
                        db.statement.executeUpdate("UPDATE teachers SET passport_num='"+passportNumberTextField.getText()+"' WHERE id_user="+userId);
                    else if(userRang == 3)
                        db.statement.executeUpdate("UPDATE students SET passport_num='"+passportNumberTextField.getText()+"' WHERE id_user="+userId);
                }
                if(isBirthDateChanged)
                {
                    if(userRang == 2)
                        db.statement.executeUpdate("UPDATE teachers SET date_of_birth='"+birthdayDatePicker.getValue().toString()+"' WHERE id_user="+userId);
                    else if(userRang == 3)
                        db.statement.executeUpdate("UPDATE students SET date_of_birth='"+birthdayDatePicker.getValue().toString()+"' WHERE id_user="+userId);
                }
                if(newUserPhoto != null)
                {
                    Files.copy(newUserPhoto.toPath(),new File(System.getProperty("user.dir")+"\\images\\"+userId+".jpg").toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            okButtonClicked = true;
            editProfileStage.close();
        }
        else
        {
            if(!isPasswordCorrect)
            {
                passwordTextField.setStyle("-fx-border-color: red");
                passwordConfirmTextField.setStyle("-fx-border-color: red");
            }
            if(!isNameCorrect)
                nameTextField.setStyle("-fx-border-color: red");
            if(!isPassportNumberCorrect)
                passportNumberTextField.setStyle("-fx-border-color: red");
        }
    }

    @FXML
    private void cancelButtonClick()
    {
        editProfileStage.close();
    }

    @FXML
    private void changePhotoButtonClick()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG files (*.jpg)","*.jpg"));
        newUserPhoto = fileChooser.showOpenDialog(editProfileStage);
        if(newUserPhoto != null)
            userPhotoImageView.setImage(new Image(newUserPhoto.toURI().toString()));
    }

    @FXML
    private void passwordTextFieldTextChanged(KeyEvent e)
    {
        passwordTextField.setStyle("");
        if(!checkInputString(e.getCharacter(),"[\\w]"))
            e.consume();
    }

    @FXML
    private void passwordConfirmTextFieldTextChanged(KeyEvent e)
    {
        passwordConfirmTextField.setStyle("");
        if(!checkInputString(e.getCharacter(),"[\\w]"))
            e.consume();
    }

    @FXML
    private void nameTextFieldTextChanged(KeyEvent e)
    {
        nameTextField.setStyle("");
        if(!checkInputString(e.getCharacter(),"[a-zA-Z ]"))
            e.consume();
    }

    @FXML
    private void passportNumberTextFieldTextChanged(KeyEvent e)
    {
        passportNumberTextField.setStyle("");
        if(!checkInputString(e.getCharacter(),"[a-zA-Z0-9]"))
            e.consume();
    }

    private boolean checkInputString(String inputString, String regex)
    {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputString);
        return m.matches();
    }
}
