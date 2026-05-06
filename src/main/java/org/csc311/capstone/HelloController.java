package org.csc311.capstone;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.csc311.capstone.db.AuditLogRepository;
import org.csc311.capstone.db.ReferenceDataRepository;
import org.csc311.capstone.db.SchemaRepository;
import org.csc311.capstone.db.StaffRepository;
import org.csc311.capstone.models.*;
import org.csc311.capstone.services.*;
import org.csc311.capstone.util.DataExportHandler;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelloController {
    private static final String LIGHT_THEME = "light-theme";
    private static final String DARK_THEME = "dark-theme";

    @FXML
    private BorderPane root;

    private final AuthService authService = new AuthService();
    private final StudentService studentService = new StudentService();
    private final DashboardService dashboardService = new DashboardService();
    private final ProfileService profileService = new ProfileService();
    private final AuditService auditService = new AuditService();

    private Staff currentUser;

    private final ObservableList<Student> studentItems = FXCollections.observableArrayList();
    private final ObservableList<AuditLog> auditLogItems = FXCollections.observableArrayList();

    private List<String> departments = new ArrayList<>();
    private List<String> majors = new ArrayList<>();

    private TableView<Student> studentTable;
    private TableView<AuditLog> auditLogTable;

    private TextField searchField;
    private ComboBox<String> filterDepartmentBox;
    private ComboBox<String> filterMajorBox;
    private TextField minGpaField;
    private TextField maxGpaField;
    private ComboBox<String> sortByBox;
    private ComboBox<String> sortDirectionBox;
    private ComboBox<Integer> pageSizeBox;
    private Label pageLabel;

    private int currentStudentPage = 1;
    private int currentStudentPageSize = 10;
    private int currentStudentTotalPages = 1;

    private int currentAuditPage = 1;
    private int currentAuditPageSize = 10;
    private int currentAuditTotalPages = 1;
    private Label auditPageLabel;

    private TextField idField;
    private TextField firstNameField;
    private TextField lastNameField;
    private ComboBox<String> departmentBox;
    private ComboBox<String> majorBox;
    private TextField gpaField;
    private Button addButton;
    private Button deleteButton;
    private MenuItem updateMenuItem;
    private MenuItem deleteMenuItem;
    private ToggleButton themeToggle;

    private Label statusLabel;
    private ImageView staffProfileImageView;

    @FXML
    private void initialize() {
        root.getStyleClass().add(LIGHT_THEME);

        try {
            SchemaRepository.initialize();
            loadReferenceData();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not initialize database: " + e.getMessage());
        }

        showLoginView();
    }

    private void loadReferenceData() throws SQLException {
        departments = ReferenceDataRepository.findDepartments();
        majors = ReferenceDataRepository.findMajors();
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
        try {
            Staff user = authService.login(clean(email), password);

            if (user == null) {
                error.setText("Enter a valid staff email and password.");
                return;
            }

            currentUser = user;
            showAppShell();
            showDashboardPage();
        } catch (SQLException e) {
            error.setText("Database login failed: " + e.getMessage());
        }
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

        try {
            Staff staff = authService.register(
                    capitalize(clean(firstName.getText())),
                    capitalize(clean(lastName.getText())),
                    normalizedEmail,
                    password.getText(),
                    role.getValue()
            );

            if (staff == null) {
                error.setText("An account already exists for this email.");
                return;
            }

            currentUser = staff;
            showAppShell();
            showDashboardPage();
        } catch (SQLException e) {
            error.setText("Registration failed: " + e.getMessage());
        }
    }

    private void showAppShell() {
        root.setTop(buildMenuBar());

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        root.setBottom(statusLabel);
        BorderPane.setMargin(statusLabel, new Insets(0, 18, 14, 18));

        updateStatus("Signed in as " + currentUser.getFirstName() + " " + currentUser.getLastName() + ".");
    }

    private MenuBar buildMenuBar() {
        Menu pages = new Menu("Pages");

        MenuItem dashboard = new MenuItem("Dashboard");
        dashboard.setOnAction(event -> showDashboardPage());

        MenuItem students = new MenuItem("Students");
        students.setOnAction(event -> showStudentsPage());

        MenuItem profile = new MenuItem("Profile");
        profile.setOnAction(event -> showProfilePage());

        pages.getItems().addAll(dashboard, students, profile);

        if (isAdmin()) {
            MenuItem logs = new MenuItem("Audit Logs");
            logs.setOnAction(event -> showAuditLogsPage());
            pages.getItems().add(logs);
        }

        Menu records = new Menu("Records");

        MenuItem add = new MenuItem("Add Student");
        add.setAccelerator(KeyCombination.keyCombination("Shortcut+N"));
        add.setDisable(!isAdmin());
        add.setOnAction(event -> {
            showStudentsPage();
            clearForm();
        });

        updateMenuItem = new MenuItem("Update Selected");
        updateMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        updateMenuItem.setDisable(true);
        updateMenuItem.setOnAction(event -> saveStudent(true));

        deleteMenuItem = new MenuItem("Delete Selected");
        deleteMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));
        deleteMenuItem.setDisable(true);
        deleteMenuItem.setOnAction(event -> deleteSelectedStudent());

        records.getItems().addAll(add, updateMenuItem, deleteMenuItem);

        Menu reports = new Menu("Reports");

        MenuItem csv = new MenuItem("Export Filtered CSV");
        csv.setAccelerator(KeyCombination.keyCombination("Shortcut+E"));
        csv.setOnAction(event -> exportCsv());

        MenuItem pdf = new MenuItem("Export Filtered PDF");
        pdf.setAccelerator(KeyCombination.keyCombination("Shortcut+P"));
        pdf.setOnAction(event -> exportPdf());

        reports.getItems().addAll(csv, pdf);

        Menu account = new Menu("Account");

        MenuItem uploadPicture = new MenuItem("Upload Profile Picture");
        uploadPicture.setOnAction(event -> uploadProfilePicture());

        MenuItem refresh = new MenuItem("Refresh Profile");
        refresh.setOnAction(event -> refreshCurrentUser());

        MenuItem logout = new MenuItem("Logout");
        logout.setOnAction(event -> {
            currentUser = null;
            showLoginView();
        });

        account.getItems().addAll(uploadPicture, refresh, new SeparatorMenuItem(), logout);

        Menu view = new Menu("View");

        MenuItem theme = new MenuItem("Switch Theme");
        theme.setAccelerator(KeyCombination.keyCombination("Shortcut+T"));
        theme.setOnAction(event -> toggleTheme());

        view.getItems().add(theme);

        Menu help = new Menu("Help");

        MenuItem usage = new MenuItem("Usage Notes");
        usage.setAccelerator(KeyCombination.keyCombination("F1"));
        usage.setOnAction(event -> showHelp());

        help.getItems().add(usage);

        return new MenuBar(pages, records, reports, account, view, help);
    }

    private void showDashboardPage() {
        try {
            DashboardStats stats = dashboardService.getStats();

            Label title = new Label("Dashboard");
            title.getStyleClass().add("auth-title");

            HBox statCards = new HBox(14,
                    statCard("Total Students", String.valueOf(stats.totalStudents())),
                    statCard("Average GPA", formatDouble(stats.averageGpa())),
                    statCard("Highest GPA", formatDouble(stats.highestGpa())),
                    statCard("Lowest GPA", formatDouble(stats.lowestGpa()))
            );

            TableView<Map.Entry<String, Integer>> departmentTable = new TableView<>();
            departmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

            TableColumn<Map.Entry<String, Integer>, String> departmentColumn = new TableColumn<>("Department");
            departmentColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getKey()));

            TableColumn<Map.Entry<String, Integer>, String> totalColumn = new TableColumn<>("Students");
            totalColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getValue())));

            departmentTable.getColumns().addAll(departmentColumn, totalColumn);
            departmentTable.setItems(FXCollections.observableArrayList(stats.studentsByDepartment().entrySet()));

            Label sectionTitle = new Label("Students by Department");
            sectionTitle.getStyleClass().add("section-title");

            VBox panel = new VBox(18, title, statCards, sectionTitle, departmentTable);
            panel.getStyleClass().add("workspace");
            VBox.setVgrow(departmentTable, Priority.ALWAYS);

            root.setCenter(panel);
            updateStatus("Dashboard loaded.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Dashboard Error", "Could not load dashboard: " + e.getMessage());
        }
    }

    private Node statCard(String label, String value) {
        Label title = new Label(label);
        title.getStyleClass().add("count-label");

        Label number = new Label(value);
        number.getStyleClass().add("auth-title");

        VBox card = new VBox(8, title, number);
        card.getStyleClass().addAll("form-panel", "stat-card");
        card.setPrefWidth(180);

        return card;
    }

    private void showStudentsPage() {
        root.setCenter(buildStudentsPage());
        currentStudentPage = 1;
        loadStudentPage();
    }

    private Node buildStudentsPage() {
        studentTable = new TableView<>(studentItems);
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
            boolean canModify = isAdmin() && hasSelection;

            if (updateMenuItem != null) {
                updateMenuItem.setDisable(!canModify);
            }

            if (deleteMenuItem != null) {
                deleteMenuItem.setDisable(!canModify);
            }

            if (deleteButton != null) {
                deleteButton.setDisable(!canModify);
            }

            if (addButton != null) {
                addButton.setText(hasSelection && isAdmin() ? "Update Student" : "Add Student");
            }
        });

        VBox tablePanel = new VBox(12, buildStudentFilters(), studentTable, buildStudentPaginationControls());
        tablePanel.getStyleClass().add("table-panel");
        VBox.setVgrow(studentTable, Priority.ALWAYS);

        Node formPanel = buildStudentForm();

        HBox page = new HBox(18, tablePanel, formPanel);
        page.getStyleClass().add("workspace");
        HBox.setHgrow(tablePanel, Priority.ALWAYS);

        return page;
    }

    private Node buildStudentFilters() {
        searchField = new TextField();
        searchField.setPromptText("Search ID, name, department, major");
        searchField.setPrefWidth(250);

        filterDepartmentBox = new ComboBox<>();
        filterDepartmentBox.getItems().add("All Departments");
        filterDepartmentBox.getItems().addAll(departments);
        filterDepartmentBox.getSelectionModel().selectFirst();

        filterMajorBox = new ComboBox<>();
        filterMajorBox.getItems().add("All Majors");
        filterMajorBox.getItems().addAll(majors);
        filterMajorBox.getSelectionModel().selectFirst();

        minGpaField = new TextField();
        minGpaField.setPromptText("Min GPA");
        minGpaField.setPrefWidth(80);

        maxGpaField = new TextField();
        maxGpaField.setPromptText("Max GPA");
        maxGpaField.setPrefWidth(80);

        sortByBox = new ComboBox<>(FXCollections.observableArrayList(
                "id",
                "firstName",
                "lastName",
                "department",
                "major",
                "gpa"
        ));
        sortByBox.getSelectionModel().select("id");

        sortDirectionBox = new ComboBox<>(FXCollections.observableArrayList("Ascending", "Descending"));
        sortDirectionBox.getSelectionModel().selectFirst();

        Button apply = new Button("Apply");
        apply.setOnAction(event -> {
            currentStudentPage = 1;
            loadStudentPage();
        });

        Button clear = new Button("Clear");
        clear.setOnAction(event -> {
            searchField.clear();
            filterDepartmentBox.getSelectionModel().selectFirst();
            filterMajorBox.getSelectionModel().selectFirst();
            minGpaField.clear();
            maxGpaField.clear();
            sortByBox.getSelectionModel().select("id");
            sortDirectionBox.getSelectionModel().selectFirst();
            currentStudentPage = 1;
            loadStudentPage();
        });

        HBox filters = new HBox(10,
                searchField,
                filterDepartmentBox,
                filterMajorBox,
                minGpaField,
                maxGpaField,
                sortByBox,
                sortDirectionBox,
                apply,
                clear
        );

        filters.setAlignment(Pos.CENTER_LEFT);
        filters.getStyleClass().add("toolbar");

        return filters;
    }

    private Node buildStudentPaginationControls() {
        Button previous = new Button("Previous");
        previous.setOnAction(event -> {
            if (currentStudentPage > 1) {
                currentStudentPage--;
                loadStudentPage();
            }
        });

        Button next = new Button("Next");
        next.setOnAction(event -> {
            if (currentStudentPage < currentStudentTotalPages) {
                currentStudentPage++;
                loadStudentPage();
            }
        });

        pageSizeBox = new ComboBox<>(FXCollections.observableArrayList(5, 10, 20, 50));
        pageSizeBox.getSelectionModel().select(Integer.valueOf(currentStudentPageSize));
        pageSizeBox.setOnAction(event -> {
            currentStudentPageSize = pageSizeBox.getValue();
            currentStudentPage = 1;
            loadStudentPage();
        });

        pageLabel = new Label();
        pageLabel.getStyleClass().add("count-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox pagination = new HBox(10, previous, next, new Label("Page Size:"), pageSizeBox, spacer, pageLabel);
        pagination.setAlignment(Pos.CENTER_LEFT);
        pagination.getStyleClass().add("pagination-bar");

        return pagination;
    }

    private Node buildStudentForm() {
        idField = new TextField();
        idField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                idField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        firstNameField = new TextField();
        lastNameField = new TextField();

        departmentBox = new ComboBox<>(FXCollections.observableArrayList(departments));
        majorBox = new ComboBox<>(FXCollections.observableArrayList(majors));

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
        addButton.setDisable(!isAdmin());
        addButton.setOnAction(event -> {
            if (studentTable.getSelectionModel().getSelectedItem() != null) {
                saveStudent(true);
            } else {
                saveStudent(false);
            }
        });

        deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setDisable(true);
        deleteButton.setOnAction(event -> deleteSelectedStudent());

        Button clear = new Button("Clear");
        clear.setOnAction(event -> clearForm());

        themeToggle = new ToggleButton();
        themeToggle.getStyleClass().add("theme-toggle");
        updateThemeButtonText();
        themeToggle.setOnAction(event -> toggleTheme());

        HBox actions = new HBox(10, addButton, deleteButton, clear);
        actions.setAlignment(Pos.CENTER_LEFT);

        Label accessNote = new Label(isAdmin()
                ? "Administrator access: create, update, and delete enabled."
                : "Staff access: view and export only.");
        accessNote.getStyleClass().add("count-label");

        Label formTitle = new Label("Student Details");
        formTitle.getStyleClass().add("section-title");

        VBox form = new VBox(14, formTitle, accessNote, grid, actions, themeToggle);
        form.getStyleClass().add("form-panel");
        form.setPrefWidth(330);
        form.setMinWidth(330);

        boolean editable = isAdmin();
        idField.setDisable(!editable);
        firstNameField.setDisable(!editable);
        lastNameField.setDisable(!editable);
        departmentBox.setDisable(!editable);
        majorBox.setDisable(!editable);
        gpaField.setDisable(!editable);

        return form;
    }

    private void loadStudentPage() {
        try {
            PaginatedResult<Student> result = studentService.findPage(buildStudentCriteria());

            studentItems.setAll(result.items());
            currentStudentTotalPages = result.totalPages();

            if (pageLabel != null) {
                pageLabel.setText("Page " + result.page() + " of " + result.totalPages() + " • " + result.totalItems() + " records");
            }

            updateStatus("Loaded " + result.items().size() + " student records.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Load Failed", "Could not load students: " + e.getMessage());
        }
    }

    private StudentSearchCriteria buildStudentCriteria() {
        return new StudentSearchCriteria(
                searchField == null ? "" : clean(searchField.getText()),
                filterDepartmentBox == null ? "All Departments" : filterDepartmentBox.getValue(),
                filterMajorBox == null ? "All Majors" : filterMajorBox.getValue(),
                parseNullableDouble(minGpaField == null ? "" : minGpaField.getText()),
                parseNullableDouble(maxGpaField == null ? "" : maxGpaField.getText()),
                sortByBox == null ? "id" : sortByBox.getValue(),
                sortDirectionBox == null || !"Descending".equals(sortDirectionBox.getValue()),
                currentStudentPage,
                currentStudentPageSize
        );
    }

    private TableColumn<Student, String> column(String title, String property) {
        TableColumn<Student, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private void populateForm(Student student) {
        if (student == null || idField == null) {
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
        if (!isAdmin()) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only administrators can modify student records.");
            return;
        }

        String validation = validateStudentForm();

        if (validation != null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", validation);
            return;
        }

        Student student = new Student();
        copyFormIntoStudent(student);

        try {
            if (updateExisting) {
                Student selected = studentTable.getSelectionModel().getSelectedItem();

                if (selected == null) {
                    showAlert(Alert.AlertType.WARNING, "No Selection", "Select a student before updating.");
                    return;
                }

                student.setID(selected.getID());
                studentService.updateStudent(currentUser, student);
                updateStatus("Updated student " + student.getID() + ".");
            } else {
                studentService.addStudent(currentUser, student);
                updateStatus("Added student " + student.getID() + ".");
            }

            loadReferenceData();
            clearForm();
            loadStudentPage();
        } catch (SecurityException e) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save student: " + e.getMessage());
        }
    }

    private void deleteSelectedStudent() {
        if (!isAdmin()) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only administrators can delete student records.");
            return;
        }

        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a student before deleting.");
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete " + selected.getFirstName() + " " + selected.getLastName() + "?",
                ButtonType.CANCEL,
                ButtonType.OK
        );

        styleDialog(confirm.getDialogPane());
        confirm.setHeaderText("Confirm Delete");

        confirm.showAndWait().filter(ButtonType.OK::equals).ifPresent(button -> {
            try {
                studentService.deleteStudent(currentUser, selected);
                clearForm();
                loadStudentPage();
                updateStatus("Deleted student " + selected.getID() + ".");
            } catch (SecurityException e) {
                showAlert(Alert.AlertType.WARNING, "Access Denied", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Could not delete student: " + e.getMessage());
            }
        });
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

    private void clearForm() {
        if (idField == null) {
            return;
        }

        idField.clear();
        idField.setEditable(true);
        firstNameField.clear();
        lastNameField.clear();
        departmentBox.getSelectionModel().clearSelection();
        majorBox.getSelectionModel().clearSelection();
        gpaField.clear();

        if (studentTable != null) {
            studentTable.getSelectionModel().clearSelection();
        }

        if (addButton != null) {
            addButton.setText("Add Student");
        }
    }

    private void showProfilePage() {
        Label title = new Label("Profile");
        title.getStyleClass().add("auth-title");

        staffProfileImageView = new ImageView();
        staffProfileImageView.setFitWidth(160);
        staffProfileImageView.setFitHeight(160);
        staffProfileImageView.setPreserveRatio(true);

        loadProfileImage();

        Label name = new Label(currentUser.getFirstName() + " " + currentUser.getLastName());
        name.getStyleClass().add("auth-title");

        Label email = new Label(currentUser.getEmail());
        email.getStyleClass().add("profile-detail");
        Label role = new Label("Role: " + currentUser.getJobType());
        role.getStyleClass().add("profile-detail");
        Label department = new Label("Department: " + currentUser.getDepartment());
        department.getStyleClass().add("profile-detail");

        Button upload = new Button("Upload Profile Picture");
        upload.getStyleClass().add("primary-button");
        upload.setOnAction(event -> uploadProfilePicture());

        VBox profileCard = new VBox(14, staffProfileImageView, name, email, role, department, upload);
        profileCard.getStyleClass().add("form-panel");
        profileCard.setMaxWidth(420);

        StackPane wrapper = new StackPane(profileCard);
        wrapper.getStyleClass().add("workspace");

        VBox page = new VBox(18, title, wrapper);
        page.getStyleClass().add("workspace");

        root.setCenter(page);
        updateStatus("Profile loaded.");
    }

    private void uploadProfilePicture() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Staff Profile Picture");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
        );

        File file = chooser.showOpenDialog(getWindow());

        if (file == null) {
            return;
        }

        try {
            profileService.uploadProfileImage(currentUser, file);
            refreshCurrentUser();
            showProfilePage();
            updateStatus("Profile picture updated.");
        } catch (RuntimeException | SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Upload Failed", "Could not upload profile picture: " + e.getMessage());
        }
    }

    private void refreshCurrentUser() {
        if (currentUser == null) {
            return;
        }

        try {
            Staff refreshed = StaffRepository.findByEmail(currentUser.getEmail());

            if (refreshed != null) {
                currentUser = refreshed;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Refresh Failed", "Could not refresh profile: " + e.getMessage());
        }
    }

    private void loadProfileImage() {
        if (staffProfileImageView == null) {
            return;
        }

        if (!isBlank(currentUser.getImgURL())) {
            staffProfileImageView.setImage(new Image(currentUser.getImgURL(), true));
        } else {
            staffProfileImageView.setImage(null);
        }
    }

    private void showAuditLogsPage() {
        if (!isAdmin()) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only administrators can view audit logs.");
            return;
        }

        Label title = new Label("Audit Logs");
        title.getStyleClass().add("auth-title");

        auditLogTable = new TableView<>(auditLogItems);
        auditLogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<AuditLog, String> imageColumn = new TableColumn<>("Staff Image");
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("staffImgUrl"));
        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(42);
                imageView.setFitHeight(42);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);

                if (empty || url == null || url.isBlank()) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(url, true));
                    setGraphic(imageView);
                }
            }
        });

        TableColumn<AuditLog, String> staffColumn = new TableColumn<>("Staff");
        staffColumn.setCellValueFactory(new PropertyValueFactory<>("staffName"));

        TableColumn<AuditLog, String> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));

        TableColumn<AuditLog, String> studentColumn = new TableColumn<>("Student ID");
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<AuditLog, String> detailsColumn = new TableColumn<>("Details");
        detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));

        TableColumn<AuditLog, String> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCreatedAt() == null ? "" : data.getValue().getCreatedAt().toString()
        ));

        auditLogTable.getColumns().addAll(imageColumn, staffColumn, actionColumn, studentColumn, detailsColumn, timestampColumn);

        Node pagination = buildAuditPaginationControls();

        VBox page = new VBox(18, title, auditLogTable, pagination);
        page.getStyleClass().add("workspace");
        VBox.setVgrow(auditLogTable, Priority.ALWAYS);

        root.setCenter(page);

        currentAuditPage = 1;
        loadAuditLogs();
    }

    private Node buildAuditPaginationControls() {
        Button previous = new Button("Previous");
        previous.setOnAction(event -> {
            if (currentAuditPage > 1) {
                currentAuditPage--;
                loadAuditLogs();
            }
        });

        Button next = new Button("Next");
        next.setOnAction(event -> {
            if (currentAuditPage < currentAuditTotalPages) {
                currentAuditPage++;
                loadAuditLogs();
            }
        });

        ComboBox<Integer> auditPageSizeBox = new ComboBox<>(FXCollections.observableArrayList(5, 10, 20, 50));
        auditPageSizeBox.getSelectionModel().select(Integer.valueOf(currentAuditPageSize));
        auditPageSizeBox.setOnAction(event -> {
            currentAuditPageSize = auditPageSizeBox.getValue();
            currentAuditPage = 1;
            loadAuditLogs();
        });

        auditPageLabel = new Label();
        auditPageLabel.getStyleClass().add("count-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox pagination = new HBox(10, previous, next, new Label("Page Size:"), auditPageSizeBox, spacer, auditPageLabel);
        pagination.setAlignment(Pos.CENTER_LEFT);
        pagination.getStyleClass().add("pagination-bar");

        return pagination;
    }

    private void loadAuditLogs() {
        try {
            PaginatedResult<AuditLog> result = auditService.findPage(currentUser, currentAuditPage, currentAuditPageSize);

            auditLogItems.setAll(result.items());
            currentAuditTotalPages = result.totalPages();

            if (auditPageLabel != null) {
                auditPageLabel.setText("Page " + result.page() + " of " + result.totalPages() + " • " + result.totalItems() + " logs");
            }

            updateStatus("Loaded audit logs.");
        } catch (SecurityException e) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Audit Error", "Could not load logs: " + e.getMessage());
        }
    }

    private void exportCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Student CSV Report");
        chooser.setInitialFileName("student-report.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = chooser.showSaveDialog(getWindow());

        if (file == null) {
            return;
        }

        try {
            List<Student> exportRows = studentService.findForExport(buildStudentCriteria());
            DataExportHandler.exportToCSV(exportRows, file);

            AuditLogRepository.log(
                    currentUser,
                    "EXPORT_CSV",
                    null,
                    "Exported " + exportRows.size() + " student records to CSV."
            );

            updateStatus("Exported " + exportRows.size() + " student records to " + file.getName() + ".");
        } catch (RuntimeException | SQLException exception) {
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Unable to write the CSV report: " + exception.getMessage());
        }
    }

    private void exportPdf() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Student PDF Report");
        chooser.setInitialFileName("student-report.pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = chooser.showSaveDialog(getWindow());

        if (file == null) {
            return;
        }

        try {
            List<Student> exportRows = studentService.findForExport(buildStudentCriteria());
            DataExportHandler.exportToPDF(exportRows, file);

            AuditLogRepository.log(
                    currentUser,
                    "EXPORT_PDF",
                    null,
                    "Exported " + exportRows.size() + " student records to PDF."
            );

            updateStatus("Exported " + exportRows.size() + " student records to " + file.getName() + ".");
        } catch (RuntimeException | SQLException exception) {
            showAlert(Alert.AlertType.ERROR, "Export Failed", "Unable to write the PDF report: " + exception.getMessage());
        }
    }

    private boolean isAdmin() {
        return currentUser != null
                && currentUser.getJobType() != null
                && (currentUser.getJobType().equalsIgnoreCase("Administrator")
                || currentUser.getJobType().equalsIgnoreCase("Admin"));
    }

    private void toggleTheme() {
        boolean isDark = root.getStyleClass().contains(DARK_THEME);

        if (isDark) {
            root.getStyleClass().remove(DARK_THEME);
            root.getStyleClass().add(LIGHT_THEME);
        } else {
            root.getStyleClass().remove(LIGHT_THEME);
            root.getStyleClass().add(DARK_THEME);
        }

        updateThemeButtonText();
    }

    private void updateThemeButtonText() {
        boolean isDark = root.getStyleClass().contains(DARK_THEME);

        if (themeToggle != null) {
            themeToggle.setText(isDark ? "Light Theme" : "Dark Theme");
        }
    }

    private void showHelp() {
        showAlert(Alert.AlertType.INFORMATION, "Usage Notes",
                "Dashboard shows student totals, GPA stats, and department counts.\n"
                        + "Students page supports search, sorting, GPA filters, and pagination.\n"
                        + "Staff accounts can view and export data.\n"
                        + "Administrators can add, update, and delete student records.\n"
                        + "Profile pictures are stored in MinIO.\n"
                        + "Audit logs track important user actions.");
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

    private Double parseNullableDouble(String value) {
        if (isBlank(value)) {
            return null;
        }

        try {
            return Double.parseDouble(clean(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatDouble(double value) {
        return "%.2f".formatted(value);
    }

    private String capitalize(String text) {
        if (isBlank(text)) {
            return text;
        }

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

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
