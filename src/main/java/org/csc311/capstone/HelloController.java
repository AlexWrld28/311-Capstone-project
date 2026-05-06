package org.csc311.capstone;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.csc311.capstone.backend.DBHandler;
import org.csc311.capstone.models.LoginDTO;
import org.csc311.capstone.models.RegisterDTO;
import org.csc311.capstone.models.Staff;
import org.csc311.capstone.models.Student;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HelloController {
    private static final String LIGHT_THEME = "light-theme";
    private static final String DARK_THEME = "dark-theme";

    @FXML
    private BorderPane root;

    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final FilteredList<Student> filteredStudents = new FilteredList<>(students, student -> true);

    private TableView<Student> studentTable;
    private Button deleteButton;
    private MenuItem updateMenuItem;
    private MenuItem deleteMenuItem;
    private TextField searchField;
    private ToggleButton themeToggle;
    private Button addButton;
    private TextField idField;
    private TextField firstNameField;
    private TextField lastNameField;
    private ComboBox<String> departmentBox;
    private ComboBox<String> majorBox;
    private TextField gpaField;
    private ComboBox<String> filterDepartmentBox;
    private ComboBox<String> filterMajorBox;
    private Label statusLabel;
    private Staff currentUser;

    private final DBHandler dbHandler = new DBHandler(true);

    @FXML
    private void initialize() {
        root.getStyleClass().add(LIGHT_THEME);
        showLoginView();
    }

    private void loadStudentsFromDatabase() {
        students.setAll(dbHandler.getAllStudents());
    }

    private void showLoginView() {
        root.setTop(null);
        root.setCenter(buildAuthCard(false));
        root.setBottom(null);
    }

    private Node buildAuthCard(boolean registrationMode) {
        Label title = new Label(registrationMode ? "Create Staff Account" : "Staff Login");
        title.getStyleClass().add("auth-title");

        TextField firstName = new TextField();
        firstName.setPromptText("First name");

        TextField lastName = new TextField();
        lastName.setPromptText("Last name");

        TextField email = new TextField();
        email.setPromptText("Email");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("Administrator", "Staff"));
        role.getSelectionModel().selectFirst();
        role.setMaxWidth(Double.MAX_VALUE);

        Label error = new Label();
        error.getStyleClass().add("error-label");

        Button primary = new Button(registrationMode ? "Register" : "Login");
        primary.getStyleClass().add("primary-button");
        primary.setMaxWidth(Double.MAX_VALUE);

        Button secondary = new Button(registrationMode ? "Back to Login" : "Create Account");
        secondary.getStyleClass().add("secondary-button");
        secondary.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(12);
        form.getStyleClass().add("auth-card");
        form.getChildren().add(title);
        if (registrationMode) {
            form.getChildren().addAll(firstName, lastName, role);
        }
        form.getChildren().addAll(email, password, error, primary, secondary);

        primary.setOnAction(event -> {
            if (registrationMode) {
                registerUser(firstName, lastName, email, password, role, error);
            } else {
                loginUser(email.getText(), password.getText(), error);
            }
        });
        secondary.setOnAction(event -> root.setCenter(buildAuthCard(!registrationMode)));

        StackPane wrapper = new StackPane(form);
        wrapper.getStyleClass().add("auth-wrapper");
        return wrapper;
    }

    private void loginUser(String email, String password, Label error) {
        LoginDTO dto = new LoginDTO(clean(email).toLowerCase(), password);

        var userResult = dbHandler.login(dto);

        if (userResult.isEmpty()) {
            error.setText("Enter a valid staff email and password.");
            return;
        }

        currentUser = userResult.get();
        showDashboard();
    }

    private void registerUser(TextField firstName, TextField lastName, TextField email, PasswordField password,
                              ComboBox<String> role, Label error) {
        if (isBlank(firstName.getText()) || isBlank(lastName.getText()) || isBlank(email.getText()) || isBlank(password.getText())) {
            error.setText("All registration fields are required.");
            return;
        }

        String normalizedEmail = clean(email.getText()).toLowerCase();
        if (!normalizedEmail.contains("@")) {
            error.setText("Enter a valid email address.");
            return;
        }

        RegisterDTO dto = new RegisterDTO(
                normalizedEmail,
                password.getText(),
                clean(firstName.getText()),
                clean(lastName.getText()),

                "Student Services"
        );

        boolean registered = dbHandler.register(dto);

        if (!registered) {
            error.setText("An account already exists for this email.");
            return;
        }

        loginUser(normalizedEmail, password.getText(), error);
    }

    private void showDashboard() {
        loadStudentsFromDatabase();

        root.setTop(buildMenuBar());
        root.setCenter(buildStudentWorkspace());
        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        root.setBottom(statusLabel);
        BorderPane.setMargin(statusLabel, new Insets(0, 18, 14, 18));
        updateStatus("Signed in as " + currentUser.getFirstName() + " " + currentUser.getLastName() + ".");
    }

    private MenuBar buildMenuBar() {
        Menu records = new Menu("Records");
        MenuItem add = new MenuItem("Add Student");
        add.setAccelerator(KeyCombination.keyCombination("Shortcut+N"));
        add.setOnAction(event -> saveStudent(false));
        updateMenuItem = new MenuItem("Update Selected");
        updateMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        updateMenuItem.setOnAction(event -> saveStudent(true));
        updateMenuItem.setDisable(true);
        deleteMenuItem = new MenuItem("Delete Selected");
        deleteMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));
        deleteMenuItem.setOnAction(event -> deleteSelectedStudent());
        deleteMenuItem.setDisable(true);
        records.getItems().addAll(add, updateMenuItem, deleteMenuItem);

        Menu reports = new Menu("Reports");
        MenuItem csv = new MenuItem("Export Filtered CSV");
        csv.setAccelerator(KeyCombination.keyCombination("Shortcut+E"));
        csv.setOnAction(event -> exportCsv());
        reports.getItems().add(csv);

        Menu view = new Menu("View");
        MenuItem theme = new MenuItem("Switch Theme");
        theme.setAccelerator(KeyCombination.keyCombination("Shortcut+T"));
        theme.setOnAction(event -> toggleTheme());
        view.getItems().add(theme);

        Menu help = new Menu("Help");
        MenuItem usage = new MenuItem("Usage Notes");
        usage.setAccelerator(KeyCombination.keyCombination("F1"));
        usage.setOnAction(event -> showHelp());
        MenuItem logout = new MenuItem("Logout");
        logout.setOnAction(event -> {
            currentUser = null;
            showLoginView();
        });
        help.getItems().addAll(usage, new SeparatorMenuItem(), logout);

        return new MenuBar(records, reports, view, help);
    }

    private Node buildStudentWorkspace() {
        studentTable = new TableView<>(filteredStudents);
        studentTable.getStyleClass().add("student-table");
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        studentTable.getColumns().addAll(
                column("ID", "ID"),
                column("First Name", "firstName"),
                column("Last Name", "lastName"),
                column("Department", "department"),
                column("Major", "major"),
                column("GPA", "gpa")
        );
        studentTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            populateForm(selected);

            boolean hasSelection = selected != null;

            updateMenuItem.setDisable(!hasSelection);
            deleteMenuItem.setDisable(!hasSelection);

            deleteButton.setDisable(!hasSelection);

            if (addButton != null) {
                addButton.setText(hasSelection ? "Update Student" : "Add Student");
            }
        });

        VBox tablePanel = new VBox(12, buildToolbar(), studentTable);
        tablePanel.getStyleClass().add("table-panel");
        VBox.setVgrow(studentTable, Priority.ALWAYS);

        Node formPanel = buildStudentForm();
        HBox workspace = new HBox(18, tablePanel, formPanel);
        workspace.getStyleClass().add("workspace");
        HBox.setHgrow(tablePanel, Priority.ALWAYS);
        return workspace;
    }

    private TableColumn<Student, String> column(String title, String property) {
        TableColumn<Student, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private Node buildToolbar() {
        searchField = new TextField();
        searchField.setPromptText("Search by ID, name, or GPA");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(295);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterDepartmentBox = new ComboBox<>(FXCollections.observableArrayList("All Departments", "Computer Science", "Mathematics", "Business", "Health Sciences"));
        filterDepartmentBox.getSelectionModel().selectFirst();
        filterMajorBox = new ComboBox<>(FXCollections.observableArrayList("All Majors", "Software Engineering", "Data Science", "Accounting", "Nursing"));
        filterMajorBox.getSelectionModel().selectFirst();

        Button clearFilters = new Button("Clear Filters");
        clearFilters.setOnAction(event -> {
            searchField.clear();
            filterDepartmentBox.getSelectionModel().selectFirst();
            filterMajorBox.getSelectionModel().selectFirst();
        });

        filterDepartmentBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterMajorBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        Label count = new Label();
        count.textProperty().bind(Bindings.size(filteredStudents).asString("%d visible records"));
        count.getStyleClass().add("count-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, searchField, filterDepartmentBox, filterMajorBox, clearFilters, spacer, count);
        toolbar.getStyleClass().add("toolbar");
        toolbar.setAlignment(Pos.CENTER_LEFT);
        return toolbar;
    }

    private Node buildStudentForm() {
        idField = new TextField();
        firstNameField = new TextField();
        lastNameField = new TextField();
        departmentBox = new ComboBox<>(FXCollections.observableArrayList("Computer Science", "Mathematics", "Business", "Health Sciences"));
        majorBox = new ComboBox<>(FXCollections.observableArrayList("Software Engineering", "Data Science", "Accounting", "Nursing"));
        gpaField = new TextField();

        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        grid.addRow(0, new Label("Student ID"), idField);
        grid.addRow(1, new Label("First Name"), firstNameField);
        grid.addRow(2, new Label("Last Name"), lastNameField);
        grid.addRow(3, new Label("Department"), departmentBox);
        grid.addRow(4, new Label("Major"), majorBox);
        grid.addRow(5, new Label("GPA"), gpaField);

        addButton = new Button("Add Student");
        addButton.getStyleClass().add("primary-button");

        addButton.setOnAction(event -> {
            if (studentTable.getSelectionModel().getSelectedItem() != null) {
                saveStudent(true); //update
            } else {
                saveStudent(false); //add
            }
        });

        deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(event -> deleteSelectedStudent());
        deleteButton.setDisable(true);
        Button clear = new Button("Clear");
        clear.setOnAction(event -> clearForm());
        themeToggle = new ToggleButton();
        updateThemeButtonText();
        themeToggle.setOnAction(event -> toggleTheme());

        HBox actions = new HBox(10, addButton, deleteButton, clear);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(14, new Label("Student Details"), grid, actions, themeToggle);
        form.getStyleClass().add("form-panel");
        form.setPrefWidth(330);
        form.setMinWidth(330);
        return form;
    }

    private void populateForm(Student student) {
        if (student == null) {
            return;
        }
        idField.setText(student.getID());
        idField.setEditable(false);
        firstNameField.setText(student.getFirstName());
        lastNameField.setText(student.getLastName());
        departmentBox.setValue(student.getDepartment());
        majorBox.setValue(student.getMajor());
        gpaField.setText(student.getGpa());
    }

    private void saveStudent(boolean updateExisting) {
        String validation = validateStudentForm();
        if (validation != null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", validation);
            return;
        }

        Student existing = findStudentById(clean(idField.getText()));
        if (updateExisting) {
            Student selected = studentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Select a student before updating.");
                return;
            }
            if (existing != null && existing != selected) {
                showAlert(Alert.AlertType.ERROR, "Duplicate ID", "Another student already uses this ID.");
                return;
            }
            copyFormIntoStudent(selected);

            if (!dbHandler.updateStudent(selected)) {
                showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update student in the database.");
                return;
            }

            studentTable.refresh();
            updateStatus("Updated student " + selected.getID() + ".");
            return;
        }

        if (existing != null) {
            showAlert(Alert.AlertType.ERROR, "Duplicate ID", "A student with this ID already exists.");
            return;
        }
        Student student = new Student();
        copyFormIntoStudent(student);
        if (!dbHandler.addStudent(student)) {
            showAlert(Alert.AlertType.ERROR, "Add Failed", "Could not add student to the database.");
            return;
        }

        students.add(student);
        clearForm();
        updateStatus("Added student " + student.getID() + ".");
    }

    private void copyFormIntoStudent(Student student) {
        student.setID(clean(idField.getText()));
        student.setFirstName(capitalize(clean(firstNameField.getText())));
        student.setLastName(capitalize(clean(lastNameField.getText())));
        student.setDepartment(departmentBox.getValue());
        student.setMajor(majorBox.getValue());
        student.setGpa(clean(gpaField.getText()));
    }

    private String validateStudentForm() {
        if (isBlank(idField.getText()) || isBlank(firstNameField.getText()) || isBlank(lastNameField.getText())
                || departmentBox.getValue() == null || majorBox.getValue() == null || isBlank(gpaField.getText())) {
            return "Student ID, name, department, major, and GPA are required.";
        }
        try {
            double gpa = Double.parseDouble(clean(gpaField.getText()));
            if (gpa < 0 || gpa > 4) {
                return "GPA must be between 0.0 and 4.0.";
            }
        } catch (NumberFormatException exception) {
            return "GPA must be a number.";
        }
        return null;
    }

    private void deleteSelectedStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a student before deleting.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getFirstName() + " " + selected.getLastName() + "?", ButtonType.CANCEL, ButtonType.OK);
        styleDialog(confirm.getDialogPane());
        confirm.setHeaderText("Confirm Delete");
        confirm.showAndWait().filter(ButtonType.OK::equals).ifPresent(button -> {

            if (!dbHandler.deleteStudent(selected.getID())) {
                showAlert(Alert.AlertType.ERROR, "Delete Failed", "Could not delete student from the database.");
                return;
            }

            students.remove(selected);
            clearForm();
            updateStatus("Deleted student " + selected.getID() + ".");
        });
    }

    private void applyFilters() {
        String searchText = searchField == null ? "" : clean(searchField.getText().toLowerCase());
        String department = filterDepartmentBox.getValue();
        String major = filterMajorBox.getValue();

        filteredStudents.setPredicate(student -> {
            boolean searchMatches =
                    searchText.isEmpty()
                            || student.getID().toLowerCase().contains(searchText)
                            || student.getFirstName().toLowerCase().contains(searchText)
                            || student.getLastName().toLowerCase().contains(searchText)
                            || student.getGpa().toLowerCase().contains(searchText);

            boolean departmentMatches = department == null || department.startsWith("All") || department.equals(student.getDepartment());
            boolean majorMatches = major == null || major.startsWith("All") || major.equals(student.getMajor());
            return searchMatches && departmentMatches && majorMatches;
        });
    }

    private void exportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Student Report");
        chooser.setInitialFileName("student-report.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showSaveDialog(getWindow());
        if (file == null) {
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write("ID,First Name,Last Name,Department,Major,GPA");
            writer.newLine();
            for (Student student : filteredStudents) {
                writer.write(csv(student.getID()) + "," + csv(student.getFirstName()) + "," + csv(student.getLastName()) + ","
                        + csv(student.getDepartment()) + "," + csv(student.getMajor()) + "," + csv(student.getGpa()));
                writer.newLine();
            }
            updateStatus("Exported " + filteredStudents.size() + " student records to " + file.getName() + ".");
        } catch (IOException exception) {
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Unable to write the CSV report: " + exception.getMessage());
        }
    }

    private void clearForm() {
        idField.clear();
        idField.setEditable(true);
        firstNameField.clear();
        lastNameField.clear();
        departmentBox.getSelectionModel().clearSelection();
        majorBox.getSelectionModel().clearSelection();
        gpaField.clear();
        studentTable.getSelectionModel().clearSelection();
        addButton.setText("Add Student");
    }

    private Student findStudentById(String id) {
        return students.stream()
                .filter(student -> student.getID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    private void toggleTheme() {
        boolean isDark = root.getStyleClass().contains(DARK_THEME);

        if (isDark) {
            root.getStyleClass().remove(DARK_THEME);
            root.getStyleClass().add(LIGHT_THEME);
        } else {
            root.getStyleClass().remove(LIGHT_THEME);
            root.getStyleClass().add(DARK_THEME);

            updateThemeButtonText();
        }
    }

    private void updateThemeButtonText() {
        boolean isDark = root.getStyleClass().contains(DARK_THEME);

        if (themeToggle != null) {
        themeToggle.setText(isDark ? "Light Theme" : "Dark Theme");
        }
    }

    private String capitalize(String text) {
        if (isBlank(text)) return text;

        String[] parts = text.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    private void showHelp() {
        showAlert(Alert.AlertType.INFORMATION, "Usage Notes",
                "Use the Records menu or form buttons to add, update, and delete students.\n"
                        + "Filters affect the table and the CSV report export.\n"
                        + "Azure SQL and Blob Storage can replace the in-memory lists when credentials and schema are ready.");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(title);
        styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }

    private void styleDialog(DialogPane dialogPane) {
        dialogPane.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());
        dialogPane.getStyleClass().add(root.getStyleClass().contains(DARK_THEME) ? DARK_THEME : LIGHT_THEME);
    }

    private void updateStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    private Window getWindow() {
        return root.getScene().getWindow();
    }

    private String csv(String value) {
        String escaped = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
