package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class MainFormController
{
    private int userId;
    private int userRang;
    private String userBirthDate;
    private boolean isProfileTabDeleted = false;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button editProfileButton;
    @FXML
    private Button addTeacherButton;
    @FXML
    private Button editTeacherButton;
    @FXML
    private Button deleteTeacherButton;
    @FXML
    private Button addStudentButton;
    @FXML
    private Button editStudentButton;
    @FXML
    private Button deleteStudentButton;
    @FXML
    private Button addGroupButton;
    @FXML
    private Button editGroupButton;
    @FXML
    private Button deleteGroupButton;
    @FXML
    private Button addClassroomButton;
    @FXML
    private Button editClassroomButton;
    @FXML
    private Button deleteClassroomButton;
    @FXML
    private Button addSubjectButton;
    @FXML
    private Button editSubjectButton;
    @FXML
    private Button deleteSubjectButton;
    @FXML
    private ImageView userPhotoImageView;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label passportNumberLabel;
    @FXML
    private Label groupNumberLabel;
    @FXML
    private Label subjectsLabel;
    @FXML
    private TableView<ObservableList> teachersTableView;
    @FXML
    private TableColumn<ObservableList,String> teacherNameColumn;
    @FXML
    private TableColumn<ObservableList,String> teacherAgeColumn;
    @FXML
    private TableColumn<ObservableList,String> teacherSubjectsColumn;
    @FXML
    private TableColumn<ObservableList,String> teacherGroupsColumn;
    @FXML
    private TableView<ObservableList> studentsTableView;
    @FXML
    private TableColumn<ObservableList,String> studentNameColumn;
    @FXML
    private TableColumn<ObservableList,String> studentAgeColumn;
    @FXML
    private TableColumn<ObservableList,String> studentGroupColumn;
    @FXML
    private TableColumn<ObservableList,String> studentSubjectColumn;
    @FXML
    private TableView<ObservableList> groupsTableView;
    @FXML
    private TableColumn<ObservableList,String> groupNumberColumn;
    @FXML
    private TableColumn<ObservableList,String> groupAvgAgeColumn;
    @FXML
    private TableColumn<ObservableList,String> groupTeacherColumn;
    @FXML
    private TableColumn<ObservableList,String> groupSubjectColumn;
    @FXML
    private TableColumn<ObservableList,String> groupClassroomColumn;
    @FXML
    private TableView<ObservableList> classroomsTableView;
    @FXML
    private TableColumn<ObservableList,String> classroomNumberColumn;
    @FXML
    private TableColumn<ObservableList,String> classroomGroupsColumn;
    @FXML
    private TableView<ObservableList> subjectTableView;
    @FXML
    private TableColumn<ObservableList,String> subjectNumberColumn;
    @FXML
    private TableColumn<ObservableList,String> subjectNameColumn;
    @FXML
    private TextField searchStudentTextField;
    @FXML
    private ChoiceBox studentSearchByChoiceBox;
    @FXML
    private TextField searchTeacherTextField;
    @FXML
    private ChoiceBox teacherSearchByChoiceBox;
    @FXML
    private TextField searchGroupTextField;
    @FXML
    private ChoiceBox groupSearchByChoiceBox;
    @FXML
    private TextField searchClassroomTextField;
    @FXML
    private ChoiceBox classroomSearchByChoiceBox;
    @FXML
    private TableView studentsInGroupTableView;
    @FXML
    private TableColumn<ObservableList,String> studentsInGroupColumn;


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserRang(int userRang) {
        this.userRang = userRang;
    }

    @FXML
    private void profileTabEditButtonClick()
    {
        boolean okClicked = false;
        try {
            okClicked = showEditProfileForm();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (okClicked)
        {
            initializeTabs();
        }
    }

    @FXML
    private void teachersTabAddButtonClick()
    {
        boolean okClicked = false;
        try {
            okClicked = showAddEditUserForm("Add", "teacher", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (okClicked)
        {
            initializeTabs();
        }
    }

    @FXML
    private void teachersTabEditButtonClick()
    {
        if(teachersTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            boolean okClicked = false;
            try {
                okClicked = showAddEditUserForm("Edit", "teacher", teachersTableView.getItems().get(teachersTableView.getSelectionModel().getSelectedIndex()).get(0).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (okClicked)
            {
                initializeTabs();
            }
        }
    }

    @FXML
    private void teachersTabDeleteButtonClick()
    {
        if(teachersTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            Db db = new Db();
            ResultSet userInfo;
            int userId = 0;
            int teacherId = 0;
            try {
                userInfo = db.statement.executeQuery("SELECT id, id_user FROM teachers WHERE name='"+ teachersTableView.getItems().get(teachersTableView.getSelectionModel().getSelectedIndex()).get(0).toString() + "'");
                if(userInfo.next())
                {
                    userId = userInfo.getInt("id_user");
                    teacherId = userInfo.getInt("id");
                }
                if(userId > 0)
                {
                    String groups = "";
                    int groupCounter = 0;
                    userInfo = db.statement.executeQuery("SELECT group_num FROM groups WHERE id_teacher=" + teacherId);
                    while(userInfo.next())
                    {
                        if(groupCounter!=0)
                        {
                            groups += ", ";
                        }
                        groups+=userInfo.getString("group_num");
                        groupCounter++;
                    }
                    if(!groups.equals(""))
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Delete teacher error");
                        alert.setHeaderText(null);
                        alert.setContentText("Teacher \""+ teachersTableView.getItems().get(teachersTableView.getSelectionModel().getSelectedIndex()).get(0).toString() + "\" studies in group(s): "+groups+". Change teacher for this group(s) first.");
                        alert.showAndWait();
                    }
                    else
                    {
                        db.statement.executeUpdate("DELETE FROM teachers WHERE id_user=" + userId);
                        db.statement.executeUpdate("DELETE FROM users WHERE id=" + userId);
                        db.statement.executeUpdate("DELETE FROM teacher_subject WHERE id_teacher=" + teacherId);
                        initializeTabs();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void studentsTabAddButtonClick()
    {
        boolean okClicked = false;
        try {
            okClicked = showAddEditUserForm("Add", "student", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (okClicked)
        {
            initializeTabs();
        }
    }

    @FXML
    private void studentsTabEditButtonClick()
    {
        if(studentsTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            boolean okClicked = false;
            try {
                okClicked = showAddEditUserForm("Edit", "student", studentsTableView.getItems().get(studentsTableView.getSelectionModel().getSelectedIndex()).get(0).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (okClicked)
            {
                initializeTabs();
            }
        }
    }

    @FXML
    private void studentsTabDeleteButtonClick()
    {
        if(studentsTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            Db db = new Db();
            int userId = 0;
            try {
                ResultSet userInfo = db.statement.executeQuery("SELECT id_user FROM students WHERE name='"+ studentsTableView.getItems().get(studentsTableView.getSelectionModel().getSelectedIndex()).get(0).toString() + "'");
                if(userInfo.next())
                {
                    userId = userInfo.getInt("id_user");
                }
                if(userId > 0)
                {
                    db.statement.executeUpdate("DELETE FROM students WHERE id_user=" + userId);
                    db.statement.executeUpdate("DELETE FROM users WHERE id=" + userId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initializeTabs();
        }
    }

    @FXML
    private void groupsTabAddButtonClick()
    {
        boolean okClicked = false;
        try {
            okClicked = showAddEditGroupForm("Add", "","","","");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (okClicked)
        {
            initializeTabs();
        }
    }

    @FXML
    private void groupsTabEditButtonClick()
    {
        if(groupsTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            boolean okClicked = false;
            try {
                okClicked = showAddEditGroupForm("Edit", groupsTableView.getItems().get(groupsTableView.getSelectionModel().getSelectedIndex()).get(0).toString(), groupsTableView.getItems().get(groupsTableView.getSelectionModel().getSelectedIndex()).get(3).toString(), groupsTableView.getItems().get(groupsTableView.getSelectionModel().getSelectedIndex()).get(2).toString(), groupsTableView.getItems().get(groupsTableView.getSelectionModel().getSelectedIndex()).get(4).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (okClicked)
            {
                initializeTabs();
            }
        }
    }

    @FXML
    private void groupsTabDeleteButtonClick()
    {
        if(groupsTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            Db db = new Db();
            ResultSet userInfo;
            int groupId = 0;
            try {
                userInfo = db.statement.executeQuery("SELECT id FROM groups WHERE group_num='"+ groupsTableView.getItems().get(groupsTableView.getSelectionModel().getSelectedIndex()).get(0).toString() + "'");
                if(userInfo.next())
                    groupId = userInfo.getInt("id");
                if(groupId > 0)
                {
                    int studentCounter = 0;
                    userInfo = db.statement.executeQuery("SELECT id FROM students WHERE id_group=" + groupId);
                    while(userInfo.next())
                        studentCounter++;
                    if(studentCounter > 0)
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Delete group error");
                        alert.setHeaderText(null);
                        alert.setContentText("Group \""+ groupsTableView.getItems().get(groupsTableView.getSelectionModel().getSelectedIndex()).get(0).toString() + "\" contains "+studentCounter+" student(s). Move them to another group first.");
                        alert.showAndWait();
                    }
                    else
                    {
                        db.statement.executeUpdate("DELETE FROM groups WHERE id=" + groupId);
                        initializeTabs();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void classroomsTabAddButtonClick()
    {
        boolean okClicked = false;
        try {
            okClicked = showAddEditItemForm("classroom","", "Add");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (okClicked)
        {
            initializeTabs();
        }
    }

    @FXML
    private void classroomsTabEditButtonClick()
    {
        if(classroomsTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            boolean okClicked = false;
            try {
                okClicked = showAddEditItemForm("classroom", classroomsTableView.getItems().get(classroomsTableView.getSelectionModel().getSelectedIndex()).get(0).toString(), "Edit");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (okClicked)
            {
                initializeTabs();
            }
        }
    }

    @FXML
    private void classroomsTabDeleteButtonClick()
    {
        if(classroomsTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            Db db = new Db();
            try {
                db.statement.executeUpdate("DELETE FROM classrooms WHERE classroom_num='" + classroomsTableView.getItems().get(classroomsTableView.getSelectionModel().getSelectedIndex()).get(0).toString() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initializeTabs();
        }
    }

    @FXML
    private void subjectsTabAddButtonClick()
    {
        boolean okClicked = false;
        try {
            okClicked = showAddEditItemForm("subject","", "Add");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (okClicked)
        {
            initializeTabs();
        }
    }

    @FXML
    private void subjectsTabEditButtonClick()
    {
        if(subjectTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            boolean okClicked = false;
            try {
                okClicked = showAddEditItemForm("subject", subjectTableView.getItems().get(subjectTableView.getSelectionModel().getSelectedIndex()).get(1).toString(), "Edit");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (okClicked)
            {
                initializeTabs();
            }
        }
    }

    @FXML
    private void subjectsTabDeleteButtonClick()
    {
        if(subjectTableView.getSelectionModel().getSelectedIndex()>=0)
        {
            Db db = new Db();
            try {
                db.statement.executeUpdate("DELETE FROM subjects WHERE subject='" + subjectTableView.getItems().get(subjectTableView.getSelectionModel().getSelectedIndex()).get(1).toString() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            initializeTabs();
        }
    }

    @FXML
    private void searchTeacherTextFieldTextChanged()
    {
        Db db = new Db();
        ResultSet userInfo;
        String sqlQuery="";
        if(searchTeacherTextField.getText().toString().equals(""))
            sqlQuery = "SELECT id, name, date_of_birth FROM teachers";
        else
        {
            switch (teacherSearchByChoiceBox.getValue().toString())
            {
                case "Name":
                    sqlQuery = "SELECT id, name, date_of_birth FROM teachers WHERE name LIKE '%" + searchTeacherTextField.getText() + "%'";
                    break;
                case "Subject":
                    sqlQuery = "SELECT t.id, t.name, t.date_of_birth, ts.id_subject, s.subject FROM teachers t LEFT JOIN teacher_subject ts ON ts.id_teacher=t.id LEFT JOIN subjects s ON ts.id_subject=s.id WHERE s.subject LIKE '" + searchTeacherTextField.getText() + "%'";
                    break;
                case "Group":
                    sqlQuery = "SELECT t.id, t.name, t.date_of_birth, g.group_num FROM teachers t LEFT JOIN groups g ON g.id_teacher=t.id WHERE g.group_num LIKE '%" + searchTeacherTextField.getText() + "%'";
                    break;
            }
        }
        teachersTableView.getItems().clear();
        try {
            userInfo = db.statement.executeQuery(sqlQuery);
            initTeachersTableView(userInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchStudentTextFieldTextChanged()
    {
        Db db = new Db();
        ResultSet userInfo;
        String sqlQuery="";
        if(searchStudentTextField.getText().toString().equals(""))
            sqlQuery = "SELECT s.name, s.date_of_birth, g.group_num AS g_num, sb.subject AS subj FROM students s LEFT JOIN groups g ON s.id_group=g.id LEFT JOIN subjects sb ON g.id_subject=sb.id";
        else
        {
            switch (studentSearchByChoiceBox.getValue().toString())
            {
                case "Name":
                    sqlQuery = "SELECT s.name, s.date_of_birth, g.group_num AS g_num, sb.subject AS subj FROM students s LEFT JOIN groups g ON s.id_group=g.id LEFT JOIN subjects sb ON g.id_subject=sb.id WHERE name LIKE '%" + searchStudentTextField.getText() + "%'";
                    break;
                case "Subject":
                    sqlQuery = "SELECT s.name, s.date_of_birth, g.group_num AS g_num, sb.subject AS subj FROM students s LEFT JOIN groups g ON s.id_group=g.id LEFT JOIN subjects sb ON g.id_subject=sb.id WHERE sb.subject LIKE '" + searchStudentTextField.getText() + "%'";
                    break;
                case "Group":
                    sqlQuery = "SELECT s.name, s.date_of_birth, g.group_num AS g_num, sb.subject AS subj FROM students s LEFT JOIN groups g ON s.id_group=g.id LEFT JOIN subjects sb ON g.id_subject=sb.id WHERE g.group_num LIKE '%" + searchStudentTextField.getText() + "%'";
                    break;
            }
        }
        studentsTableView.getItems().clear();
        try {
            userInfo = db.statement.executeQuery(sqlQuery);
            initStudentsTableView(userInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchGroupTextFieldTextChanged()
    {
        Db db = new Db();
        ResultSet userInfo;
        String sqlQuery="";
        if(searchGroupTextField.getText().toString().equals(""))
            sqlQuery = "SELECT g.id, g.group_num, t.name AS t_name, sb.subject AS subj, c.classroom_num AS c_num FROM groups g LEFT JOIN teachers t ON g.id_teacher=t.id LEFT JOIN subjects sb ON g.id_subject=sb.id LEFT JOIN classrooms c ON g.id_classroom=c.id";
        else
        {
            switch (groupSearchByChoiceBox.getValue().toString())
            {
                case "Number":
                    sqlQuery = "SELECT g.id, g.group_num, t.name AS t_name, sb.subject AS subj, c.classroom_num AS c_num FROM groups g LEFT JOIN teachers t ON g.id_teacher=t.id LEFT JOIN subjects sb ON g.id_subject=sb.id LEFT JOIN classrooms c ON g.id_classroom=c.id WHERE g.group_num LIKE '%" + searchGroupTextField.getText() + "%'";
                    break;
                case "Teacher":
                    sqlQuery = "SELECT g.id, g.group_num, t.name AS t_name, sb.subject AS subj, c.classroom_num AS c_num FROM groups g LEFT JOIN teachers t ON g.id_teacher=t.id LEFT JOIN subjects sb ON g.id_subject=sb.id LEFT JOIN classrooms c ON g.id_classroom=c.id WHERE t.name LIKE '%" + searchGroupTextField.getText() + "%'";
                    break;
                case "Subject":
                    sqlQuery = "SELECT g.id, g.group_num, t.name AS t_name, sb.subject AS subj, c.classroom_num AS c_num FROM groups g LEFT JOIN teachers t ON g.id_teacher=t.id LEFT JOIN subjects sb ON g.id_subject=sb.id LEFT JOIN classrooms c ON g.id_classroom=c.id WHERE sb.subject LIKE '" + searchGroupTextField.getText() + "%'";
                    break;
                case "Classroom":
                    sqlQuery = "SELECT g.id, g.group_num, t.name AS t_name, sb.subject AS subj, c.classroom_num AS c_num FROM groups g LEFT JOIN teachers t ON g.id_teacher=t.id LEFT JOIN subjects sb ON g.id_subject=sb.id LEFT JOIN classrooms c ON g.id_classroom=c.id WHERE c.classroom_num LIKE '" + searchGroupTextField.getText() + "%'";
                    break;
            }
        }
        groupsTableView.getItems().clear();
        try {
            userInfo = db.statement.executeQuery(sqlQuery);
            initGroupsTableView(userInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchClassroomTextFieldTextChanged()
    {
        Db db = new Db();
        ResultSet userInfo;
        String sqlQuery="";
        if(searchClassroomTextField.getText().toString().equals(""))
            sqlQuery = "SELECT id, classroom_num FROM classrooms";
        else
        {
            switch (classroomSearchByChoiceBox.getValue().toString())
            {
                case "Number":
                    sqlQuery = "SELECT id, classroom_num FROM classrooms WHERE classroom_num LIKE '" + searchClassroomTextField.getText() + "%'";
                    break;
                case "Group":
                    sqlQuery = "SELECT c.id, c.classroom_num, g.group_num FROM classrooms c LEFT JOIN groups g ON c.id=g.id_classroom WHERE g.group_num LIKE '%" + searchClassroomTextField.getText() + "%' LIMIT 1";
                    break;
            }
        }
        classroomsTableView.getItems().clear();
        try {
            userInfo = db.statement.executeQuery(sqlQuery);
            initClassroomsTableView(userInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void groupsTableViewSelectedIndexChanged()
    {
        int selectedIndex = groupsTableView.getSelectionModel().getSelectedIndex();
        if(selectedIndex >= 0)
        {
            Db db = new Db();
            ResultSet userInfo;
            studentsInGroupTableView.getItems().clear();
            try {
                userInfo = db.statement.executeQuery("SELECT name FROM students WHERE id_group=(SELECT id FROM groups WHERE group_num='" + groupsTableView.getItems().get(selectedIndex).get(0) + "')");
                ObservableList<ObservableList> items = FXCollections.observableArrayList();
                while(userInfo.next())
                {
                    ObservableList<String> item = FXCollections.observableArrayList();
                    item.add(userInfo.getString("name"));
                    items.add(item);
                }
                studentsInGroupColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(0).toString());
                    }
                });
                studentsInGroupTableView.setItems(items);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean showEditProfileForm() throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("editProfileForm.fxml"));
        Stage editProfileStage = new Stage();
        editProfileStage.setTitle("Edit profile");
        editProfileStage.setScene(new Scene(loader.load(), 450, 340));
        editProfileStage.initOwner(editProfileButton.getScene().getWindow());
        EditProfileFormController editProfileFormController = loader.getController();
        editProfileFormController.setEditProfileStage(editProfileStage);
        editProfileFormController.setUserId(userId);
        editProfileFormController.setUserRang(userRang);
        Db db = new Db();
        try {
            ResultSet userInfo = db.statement.executeQuery("SELECT login FROM users WHERE id=" + userId);
            if(userInfo.next())
            {
                editProfileFormController.setLoginTextField(userInfo.getString("login"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        editProfileFormController.setNameTextField(userNameLabel.getText());
        editProfileFormController.setPassportNumberTextField(passportNumberLabel.getText().split("Passport #: ")[1]);
        editProfileFormController.setUserPhotoImageView(userId);
        editProfileFormController.setBirthdayDatePicker(userBirthDate);
        editProfileFormController.saveCurrentInfo();
        editProfileStage.showAndWait();
        return editProfileFormController.getOkButtonClicked();
    }

    private boolean showAddEditItemForm(String itemType, String itemName, String act) throws Exception
    {
        String labelText = "";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("addEditItemForm.fxml"));
        Stage addEditItemStage = new Stage();
        addEditItemStage.setTitle(act + " " + itemType);
        addEditItemStage.setScene(new Scene(loader.load(), 250, 170));
        addEditItemStage.initOwner(tabPane.getScene().getWindow());
        AddEditItemFormController addEditItemFormController = loader.getController();
        addEditItemFormController.setAddEditItemStage(addEditItemStage);
        addEditItemFormController.setItemNameTextFieldText(itemName);
        addEditItemFormController.saveCurrentInfo();
        switch (act)
        {
            case "Add":
                addEditItemFormController.setAdd(true);
                labelText+="Enter name for new ";
                break;
            case "Edit":
                addEditItemFormController.setEdit(true);
                labelText+="Change name for ";
                break;
        }
        switch (itemType)
        {
            case "classroom":
                addEditItemFormController.setChangeClassrooms(true);
                labelText+="classroom:";
                break;
            case "subject":
                addEditItemFormController.setChangeSubjects(true);
                labelText+="subject:";
                break;
        }
        addEditItemFormController.setAddEditLabelText(labelText);
        addEditItemStage.showAndWait();
        return addEditItemFormController.isOkButtonClicked();
    }

    private boolean showAddEditUserForm(String act, String userType, String userName) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("addEditUserForm.fxml"));
        Stage addEditUserStage = new Stage();
        addEditUserStage.setTitle(act + " " + userType);
        addEditUserStage.setScene(new Scene(loader.load(), 500, 500));
        addEditUserStage.initOwner(tabPane.getScene().getWindow());
        AddEditUserFormController addEditUserFormController = loader.getController();
        addEditUserFormController.setAddEditUserStage(addEditUserStage);
        switch (userType)
        {
            case "teacher":
                addEditUserFormController.setTeacher(true);
                break;
            case "student":
                addEditUserFormController.setStudent(true);
                break;
        }
        switch (act)
        {
            case "Add":
                addEditUserFormController.setAdd(true);
                break;
            case "Edit":
                addEditUserFormController.setEdit(true);
                addEditUserFormController.setUserName(userName);
                break;
        }
        addEditUserFormController.initializeForm();
        addEditUserStage.showAndWait();
        return addEditUserFormController.isOkButtonClicked();
    }

    private boolean showAddEditGroupForm(String act, String groupName, String subject, String teacher, String classroom) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("addEditGroupForm.fxml"));
        Stage addEditGroupStage = new Stage();
        addEditGroupStage.setTitle(act + " group " + groupName);
        addEditGroupStage.setScene(new Scene(loader.load(), 400, 230));
        addEditGroupStage.initOwner(tabPane.getScene().getWindow());
        AddEditGroupFormController addEditGroupFormController = loader.getController();
        addEditGroupFormController.setAddEditGroupStage(addEditGroupStage);
        switch (act)
        {
            case "Add":
                addEditGroupFormController.setAdd(true);
                break;
            case "Edit":
                addEditGroupFormController.setEdit(true);
                addEditGroupFormController.setGroupName(groupName);
                break;
        }
        addEditGroupFormController.setCurrentInfo(groupName,subject,teacher,classroom);
        addEditGroupFormController.initializeForm();
        addEditGroupStage.showAndWait();
        return addEditGroupFormController.isOkButtonClicked();
    }

    public void initializeTabs()
    {
        Db db = new Db();
        ResultSet userInfo;
        if(userRang==1)
        {
            if(!isProfileTabDeleted)
            {
                tabPane.getTabs().remove(0);
                isProfileTabDeleted = true;
            }
        }
        else if(userRang>1)
        {
            if(userRang==3)
                subjectsLabel.setVisible(false);
            addTeacherButton.setVisible(false);
            editTeacherButton.setVisible(false);
            deleteTeacherButton.setVisible(false);
            addStudentButton.setVisible(false);
            editStudentButton.setVisible(false);
            deleteStudentButton.setVisible(false);
            addGroupButton.setVisible(false);
            editGroupButton.setVisible(false);
            deleteGroupButton.setVisible(false);
            addClassroomButton.setVisible(false);
            editClassroomButton.setVisible(false);
            deleteClassroomButton.setVisible(false);
            addSubjectButton.setVisible(false);
            editSubjectButton.setVisible(false);
            deleteSubjectButton.setVisible(false);
            //profile tab
            File file = new File("images/"+userId+".jpg");
            Image image = new Image(file.toURI().toString());
            userPhotoImageView.setImage(image);
            if(userRang==2)
            {
                try {
                    userInfo = db.statement.executeQuery("SELECT id, name, date_of_birth, passport_num FROM teachers WHERE id_user=" + userId);
                    if(userInfo.next())
                    {
                        int teacherId = userInfo.getInt("id");
                        userNameLabel.setText(userInfo.getString("name"));
                        userBirthDate = userInfo.getString("date_of_birth");
                        ageLabel.setText(getAge(userBirthDate.split("-")).toString()+" years");
                        passportNumberLabel.setText("Passport #: "+userInfo.getString("passport_num"));
                        groupNumberLabel.setText("Groups: "+getTeachersGroups(teacherId));
                        subjectsLabel.setText("Subjects: "+getTeachersSubjects(teacherId));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if(userRang==3)
            {
                try {
                    userInfo = db.statement.executeQuery("SELECT s.name, s.date_of_birth, s.passport_num, g.group_num AS g_num FROM students s LEFT JOIN groups g ON s.id_group=g.id WHERE s.id_user=" + userId);
                    if(userInfo.next())
                    {
                        userNameLabel.setText(userInfo.getString("name"));
                        userBirthDate = userInfo.getString("date_of_birth");
                        ageLabel.setText(getAge(userBirthDate.split("-")).toString()+" years");
                        passportNumberLabel.setText("Passport #: "+userInfo.getString("passport_num"));
                        groupNumberLabel.setText("Group "+userInfo.getString("g_num"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        //teachers tab
        try {
            userInfo = db.statement.executeQuery("SELECT id, name, date_of_birth FROM teachers");
            initTeachersTableView(userInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //students tab
        try {
            userInfo = db.statement.executeQuery("SELECT s.name, s.date_of_birth, g.group_num AS g_num, sb.subject AS subj FROM students s LEFT JOIN groups g ON s.id_group=g.id LEFT JOIN subjects sb ON g.id_subject=sb.id");
            initStudentsTableView(userInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //groups tab
        try {
            userInfo = db.statement.executeQuery("SELECT g.id, g.group_num, t.name AS t_name, sb.subject AS subj, c.classroom_num AS c_num FROM groups g LEFT JOIN teachers t ON g.id_teacher=t.id LEFT JOIN subjects sb ON g.id_subject=sb.id LEFT JOIN classrooms c ON g.id_classroom=c.id");
            initGroupsTableView(userInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //classrooms tab
        try {
            userInfo = db.statement.executeQuery("SELECT id, classroom_num FROM classrooms");
            initClassroomsTableView(userInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //subjects tab
        try {
            userInfo = db.statement.executeQuery("SELECT subject FROM subjects");
            initSubjectsTableView(userInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Integer getAge(String dateOfBirth[])
    {
        LocalDate currentDate = LocalDate.now();
        Integer age = currentDate.getYear()-Integer.valueOf(dateOfBirth[0]);
        if(currentDate.getMonthValue()<Integer.valueOf(dateOfBirth[1]))
            age--;
        else if(currentDate.getMonthValue()==Integer.valueOf(dateOfBirth[1]))
        {
            if(currentDate.getDayOfMonth()<Integer.valueOf(dateOfBirth[2]))
                age--;
        }
        return age;
    }

    private String getTeachersGroups(int teacherId)
    {
        Db db = new Db();
        ResultSet userInfo;
        String groups="";
        int groupCounter=0;
        try {
            userInfo = db.statement.executeQuery("SELECT group_num FROM groups WHERE id_teacher=" + teacherId);
            while(userInfo.next())
            {
                if(groupCounter!=0)
                    groups+=", ";
                groups+=userInfo.getString("group_num");
                groupCounter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    private String getTeachersSubjects(int teacherId)
    {
        Db db = new Db();
        ResultSet userInfo;
        String subjects="";
        int subjectCounter=0;
        try {
            userInfo = db.statement.executeQuery("SELECT ts.id_subject, s.subject AS subject_name FROM teacher_subject ts left JOIN subjects s ON ts.id_subject=s.id WHERE ts.id_teacher=" + teacherId);
            while(userInfo.next())
            {
                if(subjectCounter!=0)
                    subjects+=", ";
                subjects+=userInfo.getString("subject_name");
                subjectCounter++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    private String getGroupAverageAge(int groupId)
    {
        Db db = new Db();
        ResultSet userInfo;
        int ageSum = 0;
        int studentsCounter = 0;
        double averageAge = 0.0;
        try {
            userInfo = db.statement.executeQuery("SELECT date_of_birth FROM students WHERE id_group=" + groupId);
            while(userInfo.next())
            {
                ageSum+=getAge(userInfo.getString("date_of_birth").split("-"));
                studentsCounter++;
            }
            if(studentsCounter!=0)
                averageAge=ageSum/studentsCounter;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        return String.format("%.2f",averageAge);
    }

    private String getGroupsInClassroom(int classroomId)
    {
        Db db = new Db();
        ResultSet userInfo;
        String groupsInClass="";
        try {
            userInfo = db.statement.executeQuery("SELECT group_num FROM groups WHERE id_classroom=" + classroomId);
            while(userInfo.next())
                groupsInClass+=userInfo.getString("group_num")+"\n";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupsInClass;
    }

    private void initTeachersTableView(ResultSet userInfo) throws SQLException
    {
        ObservableList<ObservableList> items = FXCollections.observableArrayList();
        while(userInfo.next())
        {
            ObservableList<String> item = FXCollections.observableArrayList();
            int teacherId = userInfo.getInt("id");
            item.add(userInfo.getString("name"));
            item.add(getAge(userInfo.getString("date_of_birth").split("-")).toString());
            item.add(getTeachersSubjects(teacherId));
            item.add(getTeachersGroups(teacherId));
            items.add(item);
        }
        teacherNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(0).toString());
            }
        });
        teacherAgeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(1).toString());
            }
        });
        teacherSubjectsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(2).toString());
            }
        });
        teacherGroupsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(3).toString());
            }
        });
        teachersTableView.setItems(items);
    }

    private void initStudentsTableView(ResultSet userInfo) throws SQLException
    {
        ObservableList<ObservableList> items = FXCollections.observableArrayList();
        while(userInfo.next())
        {
            ObservableList<String> item = FXCollections.observableArrayList();
            item.add(userInfo.getString("name"));
            item.add(getAge(userInfo.getString("date_of_birth").split("-")).toString());
            item.add(userInfo.getString("g_num"));
            item.add(userInfo.getString("subj"));
            items.add(item);
        }
        studentNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(0).toString());
            }
        });
        studentAgeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(1).toString());
            }
        });
        studentGroupColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(2).toString());
            }
        });
        studentSubjectColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(3).toString());
            }
        });
        studentsTableView.setItems(items);
    }

    private void initGroupsTableView(ResultSet userInfo) throws SQLException
    {
        ObservableList<ObservableList> items = FXCollections.observableArrayList();
        while(userInfo.next())
        {
            ObservableList<String> item = FXCollections.observableArrayList();
            int groupId = userInfo.getInt("id");
            String groupNum = userInfo.getString("group_num");
            String teacherName = userInfo.getString("t_name");
            String subject = userInfo.getString("subj");
            String classroomNumber = userInfo.getString("c_num");
            item.add(groupNum);
            item.add(getGroupAverageAge(groupId));
            item.add(teacherName);
            item.add(subject);
            item.add(classroomNumber);
            items.add(item);
        }
        groupNumberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(0).toString());
            }
        });
        groupAvgAgeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(1).toString());
            }
        });
        groupTeacherColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(2).toString());
            }
        });
        groupSubjectColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(3).toString());
            }
        });
        groupClassroomColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(4).toString());
            }
        });
        groupsTableView.setItems(items);
    }

    private void initClassroomsTableView(ResultSet userInfo) throws SQLException
    {
        ObservableList<ObservableList> items = FXCollections.observableArrayList();
        while(userInfo.next())
        {
            ObservableList<String> item = FXCollections.observableArrayList();
            int classroomId = userInfo.getInt("id");
            item.add(userInfo.getString("classroom_num"));
            item.add(getGroupsInClassroom(classroomId));
            items.add(item);
        }
        classroomNumberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(0).toString());
            }
        });
        classroomGroupsColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(1).toString());
            }
        });
        classroomsTableView.setItems(items);
    }

    private void initSubjectsTableView(ResultSet userInfo) throws SQLException
    {
        ObservableList<ObservableList> items = FXCollections.observableArrayList();
        int subjectCounter = 1;
        while(userInfo.next())
        {
            ObservableList<String> item = FXCollections.observableArrayList();
            item.add(String.valueOf(subjectCounter));
            item.add(userInfo.getString("subject"));
            items.add(item);
            subjectCounter++;
        }
        subjectNumberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(0).toString());
            }
        });
        subjectNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(1).toString());
            }
        });
        subjectTableView.setItems(items);
    }
}
