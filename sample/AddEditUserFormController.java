package sample;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEditUserFormController
{

    protected class Subject
    {
        private int id;
        private String subjectName;
        private boolean isStudied;

        public Subject(int id, String subjectName, boolean isStudied) {
            this.id = id;
            this.subjectName = subjectName;
            this.isStudied = isStudied;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public boolean isStudied() {
            return isStudied;
        }

        public void setStudied(boolean studied) {
            isStudied = studied;
        }
    }
    private Md5 md5 = new Md5();
    private boolean okButtonClicked = false;
    private Stage addEditUserStage;
    private boolean add = false;
    private boolean edit = false;
    private boolean isTeacher = false;
    private boolean isStudent = false;
    private int userId;
    private String userName;
    private String currentName;
    private String currentPassportNumber;
    private String currentBirthDate;
    private String currentGroup;
    private File userPhoto = null;
    @FXML
    private Pane pane;
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
    @FXML
    private ChoiceBox groupNumberChoiceBox;
    @FXML
    private TableView<Subject> infoTableView;
    @FXML
    private TableColumn<Subject,String> idColumn;
    @FXML
    private TableColumn<Subject,String> infoColumn;
    @FXML
    private TableColumn<Subject,Boolean> additionalColumn;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button setPhotoButton;

    public boolean isOkButtonClicked() {
        return okButtonClicked;
    }

    public void setAddEditUserStage(Stage addEditUserStage) {
        this.addEditUserStage = addEditUserStage;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public void setTeacher(boolean teacher) {
        isTeacher = teacher;
    }

    public void setStudent(boolean student) {
        isStudent = student;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public void initializeForm()
    {
        ResultSet userInfo;
        if(add)
        {
            loginTextField.setEditable(true);
            loginTextField.setDisable(false);
            birthdayDatePicker.setValue(LocalDate.now());
            loginTextField.setFocusTraversable(true);
        }
        else if(edit)
            setPhotoButton.setText("Change photo");
        if(isTeacher)
        {
            groupNumberChoiceBox.setVisible(false);
            ArrayList<String> teacherSubjects = new ArrayList<>();
            int teacherId = 0;
            Db db = new Db();
            if(edit)
            {
                try {
                    userInfo = db.statement.executeQuery("SELECT t.id, t.id_user, t.name, t.date_of_birth, t.passport_num, u.login FROM teachers t LEFT JOIN users u ON t.id_user=u.id WHERE t.name='" + userName + "'");
                    if(userInfo.next())
                    {
                        teacherId = Integer.valueOf(userInfo.getString("id"));
                        userId = Integer.valueOf(userInfo.getString("id_user"));
                        currentName = userInfo.getString("name");
                        currentBirthDate = userInfo.getString("date_of_birth");
                        currentPassportNumber = userInfo.getString("passport_num");
                        loginTextField.setText(userInfo.getString("login"));
                        nameTextField.setText(currentName);
                        birthdayDatePicker.setValue(LocalDate.parse(currentBirthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        passportNumberTextField.setText(currentPassportNumber);
                        userPhotoImageView.setImage(new Image(new File("images/"+userId+".jpg").toURI().toString()));
                    }
                    userInfo = db.statement.executeQuery("SELECT s.subject AS subject_name FROM teacher_subject ts left JOIN subjects s ON ts.id_subject=s.id WHERE ts.id_teacher=" + teacherId);
                    while(userInfo.next())
                    {
                        String subjectName = userInfo.getString("subject_name");
                        teacherSubjects.add(subjectName);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            ObservableList<Subject> items = FXCollections.observableArrayList();
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            infoColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
            additionalColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Subject, Boolean>, ObservableValue<Boolean>>() {
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Subject, Boolean> param) {
                    Subject subject = param.getValue();
                    SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(subject.isStudied());
                    booleanProp.addListener(new ChangeListener<Boolean>() {
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            subject.setStudied(newValue);
                        }
                    });
                    return booleanProp;
                }
            });
            additionalColumn.setCellFactory(new Callback<TableColumn<Subject, Boolean>, TableCell<Subject, Boolean>>() {
                public TableCell<Subject, Boolean> call(TableColumn<Subject, Boolean> p) {
                    CheckBoxTableCell<Subject, Boolean> cell = new CheckBoxTableCell<Subject, Boolean>();
                    return cell;
                }
            });
            try {
                userInfo = db.statement.executeQuery("SELECT id, subject FROM subjects");
                while(userInfo.next())
                {
                    int subjectId = Integer.valueOf(userInfo.getString("id"));
                    String subjectName = userInfo.getString("subject");
                    boolean equals = false;
                    for(int i=0; i<teacherSubjects.size();i++)
                    {
                        if(teacherSubjects.get(i).equals(subjectName))
                        {
                            equals=true;
                            break;
                        }
                    }
                    Subject subject = new Subject(subjectId, subjectName, equals);
                    items.add(subject);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            infoTableView.setItems(items);
        }
        else if(isStudent)
        {
            pane.getChildren().remove(16);
            pane.setPrefHeight(375);
            okButton.setLayoutY(278.0);
            cancelButton.setLayoutY(278.0);
            addEditUserStage.setMaxHeight(375.0);
            groupNumberChoiceBox.setVisible(true);
            ArrayList<String> studentGroups = new ArrayList<>();
            Db db = new Db();
            if(add)
                studentGroups.add("Select group");
            try {
                userInfo = db.statement.executeQuery("SELECT group_num FROM groups");
                while(userInfo.next())
                {
                    String group = userInfo.getString("group_num");
                    studentGroups.add(group);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            groupNumberChoiceBox.setItems(FXCollections.observableArrayList(studentGroups));
            groupNumberChoiceBox.setValue(groupNumberChoiceBox.getItems().get(0));
            if(edit)
            {
                try {
                    userInfo = db.statement.executeQuery("SELECT s.id_user, s.name, s.date_of_birth, s.passport_num, g.group_num, u.login FROM students s LEFT JOIN users u ON s.id_user=u.id LEFT JOIN groups g ON s.id_group=g.id WHERE s.name='" + userName + "'");
                    if(userInfo.next())
                    {
                        userId = Integer.valueOf(userInfo.getString("id_user"));
                        loginTextField.setText(userInfo.getString("login"));
                        currentName = userInfo.getString("name");
                        currentBirthDate = userInfo.getString("date_of_birth");
                        currentPassportNumber = userInfo.getString("passport_num");
                        currentGroup = userInfo.getString("group_num");
                        nameTextField.setText(currentName);
                        birthdayDatePicker.setValue(LocalDate.parse(currentBirthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        passportNumberTextField.setText(currentPassportNumber);
                        userPhotoImageView.setImage(new Image(new File("images/"+userId+".jpg").toURI().toString()));
                        groupNumberChoiceBox.setValue(currentGroup);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void okButtonClick()
    {
        ResultSet userInfo;
        Db db = new Db();
        int userRang = 0;
        if(isTeacher)
            userRang = 2;
        else if(isStudent)
            userRang = 3;
        if(add)
        {
            boolean isLoginCorrect = true, isPasswordCorrect = true, isNameCorrect = true, isPassportNumberCorrect = true, isBirthDateCorrect = true, isGroupCorrect = true, isSubjectsCorrect = false;
            int subjectsCount = 0;
            boolean eraseLoginError = false;
            if(loginTextField.getText().equals("") || (loginTextField.getText().replaceAll(" ","")).equals(""))
                isLoginCorrect = false;
            else
            {
                try {
                    userInfo = db.statement.executeQuery("SELECT id FROM users WHERE login='" + loginTextField.getText() + "'");
                    if(userInfo.next())
                    {
                        isLoginCorrect = false;
                        eraseLoginError = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(!passwordTextField.getText().equals(passwordConfirmTextField.getText()))
            {
                isPasswordCorrect = false;
            }
            else
            {
                if(passwordTextField.getText().equals("") || (passwordTextField.getText().replaceAll(" ","")).equals(""))
                    isPasswordCorrect = false;
            }
            if(nameTextField.getText().equals("") || (nameTextField.getText().replaceAll(" ","")).equals(""))
                isNameCorrect = false;
            if(passportNumberTextField.getText().equals("") || (passportNumberTextField.getText().replaceAll(" ","")).equals(""))
                isPassportNumberCorrect = false;
            if(birthdayDatePicker.getValue().equals(LocalDate.now()))
                isBirthDateCorrect = false;
            if(isStudent && groupNumberChoiceBox.getValue().toString().equals("Select group"))
                isGroupCorrect = false;
            if(isTeacher)
            {
                try {
                    userInfo = db.statement.executeQuery("SELECT COUNT(*) FROM subjects");
                    if(userInfo.next())
                        subjectsCount = userInfo.getInt(1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                for(int i=0;i<subjectsCount;i++)
                {
                    if((infoTableView.getItems().get(i)).isStudied)
                    {
                        isSubjectsCorrect = true;
                        break;
                    }
                }
            }
            else if(isStudent)
                isSubjectsCorrect = true;
            if(isLoginCorrect && isPasswordCorrect && isNameCorrect && isPassportNumberCorrect && isBirthDateCorrect && isGroupCorrect && isSubjectsCorrect)
            {
                try {
                    db.statement.executeUpdate("INSERT INTO users (login, password, rang) VALUES ('" + loginTextField.getText() + "','" + md5.crypt(passwordTextField.getText()) + "', " + userRang + ")");
                    if(isTeacher)
                    {
                        db.statement.executeUpdate("INSERT INTO teachers (name, date_of_birth, passport_num, id_user) VALUES ('" + nameTextField.getText() + "','" + birthdayDatePicker.getValue().toString() + "', '" + passportNumberTextField.getText() + "', (SELECT id FROM users WHERE login='" + loginTextField.getText() + "'))");
                        for(int i=0;i<subjectsCount;i++)
                        {
                            if((infoTableView.getItems().get(i)).isStudied)
                            {
                                db.statement.executeUpdate("INSERT INTO teacher_subject (id_teacher, id_subject) VALUES ((SELECT id FROM teachers WHERE id_user=(SELECT id FROM users WHERE login='" + loginTextField.getText() + "'))," + (i+1) + ")");
                            }
                        }
                    }
                    else if(isStudent)
                    {
                        db.statement.executeUpdate("INSERT INTO students (name, date_of_birth, passport_num, id_group, id_user) VALUES ('" + nameTextField.getText() + "','" + birthdayDatePicker.getValue().toString() + "', '" + passportNumberTextField.getText() + "', (SELECT id FROM groups WHERE group_num='" + groupNumberChoiceBox.getValue().toString() + "'), (SELECT id FROM users WHERE login='" + loginTextField.getText() + "'))");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                okButtonClicked = true;
                addEditUserStage.close();
            }
            else
            {
                String errorMessage = "";
                if(!isLoginCorrect)
                    loginTextField.setStyle("-fx-border-color: red");
                if(!isPasswordCorrect)
                {
                    passwordTextField.setStyle("-fx-border-color: red");
                    passwordConfirmTextField.setStyle("-fx-border-color: red");
                }
                if(!isNameCorrect)
                    nameTextField.setStyle("-fx-border-color: red");
                if(!isPassportNumberCorrect)
                    passportNumberTextField.setStyle("-fx-border-color: red");
                if(eraseLoginError)
                    errorMessage+="Login \"" + loginTextField.getText() + "\" is already exists. ";
                if(!isBirthDateCorrect)
                    birthdayDatePicker.setStyle("-fx-border-color: red");
                if(!isGroupCorrect)
                    groupNumberChoiceBox.setStyle("-fx-border-color: red");
                if(!isSubjectsCorrect)
                    errorMessage+="Choose subjects for teacher.";
                if(eraseLoginError || !isSubjectsCorrect)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Registration error");
                    alert.setHeaderText(null);
                    alert.setContentText(errorMessage);
                    alert.showAndWait();
                }
            }
        }
        else if(edit)
        {
            boolean isPasswordChanged = false, isNameChanged = false, isPassportNumberChanged = false, isBirthDateChanged = false, isGroupChanged = false;
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
            if(isStudent && !groupNumberChoiceBox.getValue().toString().equals(currentGroup))
            {
                isGroupChanged = true;
            }
            if(isPasswordCorrect && isNameCorrect && isPassportNumberCorrect)
            {
                try {
                    if(isPasswordChanged)
                    {
                        db.statement.executeUpdate("UPDATE users SET password='"+md5.crypt(passwordTextField.getText())+"' WHERE id="+userId);
                    }
                    if(isNameChanged)
                    {
                        if(isTeacher)
                            db.statement.executeUpdate("UPDATE teachers SET name='"+nameTextField.getText()+"' WHERE id_user="+userId);
                        else if(isStudent)
                            db.statement.executeUpdate("UPDATE students SET name='"+nameTextField.getText()+"' WHERE id_user="+userId);
                    }
                    if(isPassportNumberChanged)
                    {
                        if(isTeacher)
                            db.statement.executeUpdate("UPDATE teachers SET passport_num='"+passportNumberTextField.getText()+"' WHERE id_user="+userId);
                        else if(isStudent)
                            db.statement.executeUpdate("UPDATE students SET passport_num='"+passportNumberTextField.getText()+"' WHERE id_user="+userId);
                    }
                    if(isBirthDateChanged)
                    {
                        if(isTeacher)
                            db.statement.executeUpdate("UPDATE teachers SET date_of_birth='"+birthdayDatePicker.getValue().toString()+"' WHERE id_user="+userId);
                        else if(isStudent)
                            db.statement.executeUpdate("UPDATE students SET date_of_birth='"+birthdayDatePicker.getValue().toString()+"' WHERE id_user="+userId);
                    }
                    if(isGroupChanged)
                    {
                        db.statement.executeUpdate("UPDATE students SET id_group=(SELECT id FROM groups WHERE group_num='" + groupNumberChoiceBox.getValue().toString() + "') WHERE id_user="+userId);
                    }
                    if(isTeacher)
                    {
                        boolean[] allSubjects = null;
                        int subjectsCount = 0;
                        userInfo = db.statement.executeQuery("SELECT COUNT(*) FROM subjects");
                        if(userInfo.next())
                        {
                            subjectsCount = userInfo.getInt(1);
                            allSubjects = new boolean[subjectsCount];
                        }
                        userInfo = db.statement.executeQuery("SELECT id_subject FROM teacher_subject WHERE id_teacher=(SELECT id FROM teachers WHERE id_user=" + userId + ")");
                        while(userInfo.next())
                        {
                            int subjectId = userInfo.getInt("id_subject");
                            allSubjects[subjectId-1]=true;
                        }
                        for(int i=0;i<subjectsCount;i++)
                        {
                            if(allSubjects[i]!=(infoTableView.getItems().get(i)).isStudied)
                            {
                                if(allSubjects[i])
                                {
                                    db.statement.executeUpdate("DELETE FROM teacher_subject WHERE id_teacher=(SELECT id FROM teachers WHERE id_user=" + userId + ") AND id_subject=" + (i+1));
                                }
                                else
                                {
                                    db.statement.executeUpdate("INSERT INTO teacher_subject (id_teacher, id_subject) VALUES ((SELECT id FROM teachers WHERE id_user=" + userId + ")," + (i+1) + ")");
                                }
                            }
                        }
                    }
                    if(userPhoto != null)
                    {
                        Files.copy(userPhoto.toPath(),new File(System.getProperty("user.dir")+"\\images\\"+userId+".jpg").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                okButtonClicked = true;
                addEditUserStage.close();
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
    }

    @FXML
    private void cancelButtonClick()
    {
        addEditUserStage.close();
    }

    @FXML
    private void setPhotoButtonClick()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG files (*.jpg)","*.jpg"));
        fileChooser.setTitle("Open Resource File");
        userPhoto = fileChooser.showOpenDialog(addEditUserStage);
        if(userPhoto != null)
            userPhotoImageView.setImage(new Image(userPhoto.toURI().toString()));
    }

    @FXML
    private void passwordTextFieldTextChanged(KeyEvent e)
    {
        passwordTextField.setStyle("");
        passwordConfirmTextField.setStyle("");
        if(!checkInputString(e.getCharacter(),"[\\w]"))
            e.consume();
    }

    @FXML
    private void passwordConfirmTextFieldTextChanged(KeyEvent e)
    {
        passwordConfirmTextField.setStyle("");
        passwordTextField.setStyle("");
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

    @FXML
    private void loginTextFieldTextChanged(KeyEvent e)
    {
        loginTextField.setStyle("");
        if(!checkInputString(e.getCharacter(),"[a-zA-Z0-9]"))
            e.consume();
    }

    @FXML
    private void groupsNumberChoiceBoxMouseClick()
    {
        groupNumberChoiceBox.setStyle("");
        if(groupNumberChoiceBox.getItems().get(0).toString().equals("Select group"))
            groupNumberChoiceBox.getItems().remove(0);
    }

    @FXML
    private void birthDatePickerTextChanged()
    {
        birthdayDatePicker.setStyle("");
    }

    private boolean checkInputString(String inputString, String regex)
    {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputString);
        return m.matches();
    }
}
