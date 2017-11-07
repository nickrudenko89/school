package sample;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEditGroupFormController
{
    private boolean isOkButtonClicked = false;
    private Stage addEditGroupStage;
    private String groupName;
    private String currentGroupName;
    private String currentSubject;
    private String currentTeacher;
    private String currentClassroom;
    private boolean add;
    private boolean edit;
    @FXML
    private TextField groupNumberTextField;
    @FXML
    private ComboBox subjectComboBox;
    @FXML
    private ComboBox teacherComboBox;
    @FXML
    private ComboBox classroomComboBox;

    public boolean isOkButtonClicked() {
        return isOkButtonClicked;
    }

    public void setAddEditGroupStage(Stage addEditGroupStage) {
        this.addEditGroupStage = addEditGroupStage;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public void setCurrentInfo(String groupName,String subject,String teacher,String classroom)
    {
        currentGroupName = groupName;
        currentSubject = subject;
        currentTeacher = teacher;
        currentClassroom = classroom;
        groupNumberTextField.setText(groupName);
        subjectComboBox.setValue(subject);
        teacherComboBox.setValue(teacher);
        classroomComboBox.setValue(classroom);
    }

    @FXML
    private void subjectComboBoxSelectedIndexChanged()
    {
        subjectComboBox.setStyle("");
        ResultSet userInfo;
        ArrayList<String> items = new ArrayList<>();
        Db db = new Db();
        try {
            userInfo = db.statement.executeQuery("SELECT ts.id_teacher, t.name FROM teacher_subject ts LEFT JOIN teachers t ON ts.id_teacher=t.id WHERE ts.id_subject=" + (subjectComboBox.getSelectionModel().getSelectedIndex()+1));
            boolean isTeachers = false;
            while(userInfo.next())
            {
                isTeachers = true;
                String item = userInfo.getString("name");
                items.add(item);
            }
            teacherComboBox.getItems().clear();
            if(isTeachers)
            {
                teacherComboBox.setItems(FXCollections.observableArrayList(items));
                teacherComboBox.getSelectionModel().select(0);
            }
            else
            {
                items.add("");
                teacherComboBox.setItems(FXCollections.observableArrayList(items));
                teacherComboBox.getSelectionModel().select(0);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Set subject error");
                alert.setHeaderText(null);
                alert.setContentText("There are no teachers educating " + subjectComboBox.getSelectionModel().getSelectedItem().toString());
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void teacherComboBoxSelectedIndexChanged()
    {
        teacherComboBox.setStyle("");
    }

    @FXML
    private void classroomComboBoxSelectedIndexChanged()
    {
        classroomComboBox.setStyle("");
    }

    @FXML
    private void cancelButtonClick()
    {
        addEditGroupStage.close();
    }

    @FXML
    private void okButtonClick()
    {
        Db db = new Db();
        ResultSet userInfo;
        if(add)
        {
            boolean isGroupNameCorrect = true, isSubjectCorrect = true, isTeacherCorrect = true, isClassroomCorrect = true;
            boolean eraseGroupExistsError = false;
            if(groupNumberTextField.getText().equals("") || (groupNumberTextField.getText().replaceAll(" ","")).equals(""))
                isGroupNameCorrect = false;
            else
            {
                try {
                    userInfo = db.statement.executeQuery("SELECT id FROM groups WHERE group_num='" + groupNumberTextField.getText() + "'");
                    if(userInfo.next())
                    {
                        isGroupNameCorrect = false;
                        eraseGroupExistsError = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(subjectComboBox.getValue().toString().equals(""))
                isSubjectCorrect = false;
            if(teacherComboBox.getValue().toString().equals(""))
                isTeacherCorrect = false;
            if(classroomComboBox.getValue().toString().equals(""))
                isClassroomCorrect = false;
            if(isGroupNameCorrect && isSubjectCorrect && isTeacherCorrect && isClassroomCorrect)
            {
                try {
                    db.statement.executeUpdate("INSERT INTO groups (group_num, id_teacher, id_subject, id_classroom) VALUES ('" + groupNumberTextField.getText() + "', (SELECT id FROM teachers WHERE name='" + (teacherComboBox.getSelectionModel().getSelectedItem().toString()) + "'), " + (subjectComboBox.getSelectionModel().getSelectedIndex()+1) + ", " + (classroomComboBox.getSelectionModel().getSelectedIndex()+1) + ")");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                isOkButtonClicked = true;
                addEditGroupStage.close();
            }
            else
            {
                if(!isGroupNameCorrect)
                {
                    groupNumberTextField.setStyle("-fx-border-color: red");
                    if(eraseGroupExistsError)
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Adding group error");
                        alert.setHeaderText(null);
                        alert.setContentText("Group name \"" + groupNumberTextField.getText() + "\" is already exists");
                        alert.showAndWait();
                    }
                }
                if(!isSubjectCorrect)
                    subjectComboBox.setStyle("-fx-border-color: red");
                if(!isTeacherCorrect)
                    teacherComboBox.setStyle("-fx-border-color: red");
                if(!isClassroomCorrect)
                    classroomComboBox.setStyle("-fx-border-color: red");
            }
        }
        else if(edit)
        {
            int groupId = 0;
            boolean isGroupNameCorrect = true, isTeacherCorrect = true;
            boolean isGroupNameChanged = false, isSubjectChanged = false, isTeacherChanged = false, isClassroomChanged = false;
            boolean eraseGroupExistsError = false;
            try {
                userInfo = db.statement.executeQuery("SELECT id FROM groups WHERE group_num='" + groupName + "'");
                if(userInfo.next())
                    groupId = userInfo.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(!groupNumberTextField.getText().equals(currentGroupName))
            {
                isGroupNameChanged = true;
                if(groupNumberTextField.getText().equals("") || (groupNumberTextField.getText().replaceAll(" ","")).equals(""))
                    isGroupNameCorrect = false;
                else
                {
                    try {
                        userInfo = db.statement.executeQuery("SELECT id FROM groups WHERE group_num='" + groupNumberTextField.getText() + "'");
                        if(userInfo.next())
                        {
                            eraseGroupExistsError = true;
                            isGroupNameCorrect = false;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(!subjectComboBox.getValue().toString().equals(currentSubject))
                isSubjectChanged = true;
            if(!teacherComboBox.getValue().toString().equals(currentTeacher))
            {
                isTeacherChanged = true;
                if (teacherComboBox.getValue().toString().equals(""))
                    isTeacherCorrect = false;
            }
            if(!classroomComboBox.getValue().toString().equals(currentClassroom))
                isClassroomChanged = true;
            if(isGroupNameCorrect && isTeacherCorrect)
            {
                try {
                    if(isGroupNameChanged)
                        db.statement.executeUpdate("UPDATE groups SET group_num='"+groupNumberTextField.getText()+"' WHERE id="+groupId);
                    if(isSubjectChanged)
                        db.statement.executeUpdate("UPDATE groups SET id_subject="+(subjectComboBox.getSelectionModel().getSelectedIndex()+1)+" WHERE id="+groupId);
                    if(isTeacherChanged)
                        db.statement.executeUpdate("UPDATE groups SET id_teacher=(SELECT id FROM teachers WHERE name='"+teacherComboBox.getSelectionModel().getSelectedItem().toString()+"') WHERE id="+groupId);
                    if(isClassroomChanged)
                        db.statement.executeUpdate("UPDATE groups SET id_classroom="+(classroomComboBox.getSelectionModel().getSelectedIndex()+1)+" WHERE id="+groupId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                isOkButtonClicked = true;
                addEditGroupStage.close();
            }
            else
            {
                if(!isGroupNameCorrect)
                {
                    groupNumberTextField.setStyle("-fx-border-color: red");
                    if(eraseGroupExistsError)
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setTitle("Adding group error");
                        alert.setContentText("Group name \"" + groupNumberTextField.getText() + "\" is already exists");
                        alert.showAndWait();
                    }
                }
                if(!isTeacherCorrect)
                    teacherComboBox.setStyle("-fx-border-color: red");
            }
        }
    }

    @FXML
    private void groupNumberTextFieldTextChanged(KeyEvent e)
    {
        groupNumberTextField.setStyle("");
        if(!checkInputString(e.getCharacter(),"[a-zA-Z0-9]"))
            e.consume();
    }

    public void initializeForm()
    {
        Db db = new Db();
        ArrayList<String> items;
        ResultSet userInfo;
        if(add)
        {
            try {
                items = new ArrayList<>();
                userInfo = db.statement.executeQuery("SELECT subject FROM subjects");
                while(userInfo.next())
                {
                    String item = userInfo.getString("subject");
                    items.add(item);
                }
                subjectComboBox.setItems(FXCollections.observableArrayList(items));
                items = new ArrayList<>();
                userInfo = db.statement.executeQuery("SELECT classroom_num FROM classrooms");
                while(userInfo.next())
                {
                    String item = userInfo.getString("classroom_num");
                    items.add(item);
                }
                classroomComboBox.setItems(FXCollections.observableArrayList(items));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(edit)
        {
            try {
                items = new ArrayList<>();
                userInfo = db.statement.executeQuery("SELECT subject FROM subjects");
                while(userInfo.next())
                {
                    String item = userInfo.getString("subject");
                    items.add(item);
                }
                subjectComboBox.setItems(FXCollections.observableArrayList(items));
                items = new ArrayList<>();
                userInfo = db.statement.executeQuery("SELECT ts.id_teacher, t.name FROM teacher_subject ts LEFT JOIN teachers t ON ts.id_teacher=t.id WHERE ts.id_subject=(SELECT id FROM subjects WHERE subject='" + currentSubject + "')");
                while(userInfo.next())
                {
                    String item = userInfo.getString("name");
                    items.add(item);
                }
                teacherComboBox.setItems(FXCollections.observableArrayList(items));
                items = new ArrayList<>();
                userInfo = db.statement.executeQuery("SELECT classroom_num FROM classrooms");
                while(userInfo.next())
                {
                    String item = userInfo.getString("classroom_num");
                    items.add(item);
                }
                classroomComboBox.setItems(FXCollections.observableArrayList(items));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkInputString(String inputString, String regex)
    {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputString);
        return m.matches();
    }
}
