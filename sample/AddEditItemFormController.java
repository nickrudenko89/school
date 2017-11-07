package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEditItemFormController
{
    private boolean okButtonClicked = false;
    private Stage addEditItemStage;
    private boolean add = false;
    private boolean edit = false;
    private boolean changeClassrooms = false;
    private boolean changeSubjects = false;
    private String currentTextFieldValue;
    @FXML
    private TextField itemNameTextField;
    @FXML
    private Label addEditLabel;

    public boolean isOkButtonClicked() {
        return okButtonClicked;
    }

    public void setItemNameTextFieldText(String itemNameTextFieldText) {
        this.itemNameTextField.setText(itemNameTextFieldText);
    }

    public void setAddEditLabelText(String addEditLabelText) {
        this.addEditLabel.setText(addEditLabelText);
    }

    public void setAddEditItemStage(Stage addEditItemStage) {
        this.addEditItemStage = addEditItemStage;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public void setChangeClassrooms(boolean changeClassrooms) {
        this.changeClassrooms = changeClassrooms;
    }

    public void setChangeSubjects(boolean changeSubjects) {
        this.changeSubjects = changeSubjects;
    }

    @FXML
    private void okButtonClick()
    {
        if(add)
        {
            if((itemNameTextField.getText().replaceAll(" ","")).equals(""))
            {
                itemNameTextField.setStyle("-fx-border-color: red");
            }
            else
            {
                Db db = new Db();
                if(changeSubjects)
                {
                    try {
                        db.statement.executeUpdate("INSERT INTO subjects (subject) VALUES ('" + itemNameTextField.getText() + "')");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else if(changeClassrooms)
                {
                    try {
                        db.statement.executeUpdate("INSERT INTO classrooms (classroom_num) VALUES ('" + itemNameTextField.getText() + "')");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                okButtonClicked = true;
                addEditItemStage.close();

            }
        }
        else if(edit)
        {
            if(itemNameTextField.equals(currentTextFieldValue) || (itemNameTextField.getText().replaceAll(" ","")).equals(""))
            {
                itemNameTextField.setStyle("-fx-border-color: red");
            }
            else
            {
                if(itemNameTextField.getText().equals(currentTextFieldValue))
                    addEditItemStage.close();
                else
                {
                    Db db = new Db();
                    if (changeSubjects)
                    {
                        try {
                            db.statement.executeUpdate("UPDATE subjects SET subject='" + itemNameTextField.getText() + "' WHERE subject='" + currentTextFieldValue + "'");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (changeClassrooms)
                    {
                        try {
                            db.statement.executeUpdate("UPDATE classrooms SET classroom_num='" + itemNameTextField.getText() + "' WHERE classroom_num='" + currentTextFieldValue + "'");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    okButtonClicked = true;
                    addEditItemStage.close();
                }
            }
        }
    }

    @FXML
    private void cancelButtonClick()
    {
        addEditItemStage.close();
    }

    @FXML
    private void itemNameTextFieldTextChanged(KeyEvent e)
    {
        itemNameTextField.setStyle("");
        if(changeClassrooms)
        {
            if (!checkInputString(e.getCharacter(), "[0-9]"))
                e.consume();
        }
        else if(changeSubjects)
        {
            if (!checkInputString(e.getCharacter(), "[^0-9]"))
                e.consume();
        }
    }

    public void saveCurrentInfo()
    {
        currentTextFieldValue = itemNameTextField.getText();
    }

    private boolean checkInputString(String inputString, String regex)
    {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputString);
        return m.matches();
    }
}
