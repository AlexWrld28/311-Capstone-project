package org.csc311.capstone;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelloController {
    private static final String LIGHT_THEME = "light-theme";
    private static final String DARK_THEME = "dark-theme";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{6,}$";
    private static final String PASSWORD_REQUIREMENTS_MESSAGE =
            "Password must be at least 6 characters and include at least\none capital letter and one special character";

    @FXML
    private BorderPane root;

    private final AuthService authService = new AuthService();
    private final StudentService studentService = new StudentService();
    private final DashboardService dashboardService = new DashboardService();
    private final ProfileService profileService = new ProfileService();
    private final AuditService auditService = new AuditService();

    private Staff currentUser;

    private final ObservableList<Student> studentItems = FXCollections.observableArrayList();
    private final ObservableList<StudentGrade> gradeItems = FXCollections.observableArrayList();
    private final ObservableList<AuditLog> auditLogItems = FXCollections.observableArrayList();

    private List<String> departments = new ArrayList<>();
    private List<String> majors = new ArrayList<>();

    private TableView<Student> studentTable;
    private TableView<StudentGrade> gradeTable;
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
    private TextField gradeClassNameField;
    private TextField gradeClassCodeField;
    private TextField gradeValueField;
    private TextField gradeCreditsField;
    private TextField gradeTermField;
    private Button saveGradeButton;
    private Button deleteGradeButton;
    private Label gradeSummaryLabel;
    private Button dashboardNavButton;
    private Button studentsNavButton;
    private Button profileNavButton;
    private Button auditNavButton;
    private ToggleButton themeToggle;

    private Label statusLabel;
    private ImageView staffProfileImageView;
    private Label studentSummaryLabel;
    private Label auditSummaryLabel;
    private boolean shortcutsRegistered;

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
        Label eyebrow = new Label("School Operations");
        eyebrow.getStyleClass().add("page-eyebrow");

        Label title = new Label(registrationMode ? "Create Staff Account" : "Staff Login");
        title.getStyleClass().add("auth-title");

        Label subtitle = new Label(registrationMode
                ? "Provision a staff account for this workspace."
                : "Sign in to manage student records and reporting.");
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

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
        error.setWrapText(true);
        error.setMaxWidth(Double.MAX_VALUE);

        Button primary = new Button(registrationMode ? "Register" : "Login");
        primary.getStyleClass().add("primary-button");
        primary.setMaxWidth(Double.MAX_VALUE);

        Button secondary = new Button(registrationMode ? "Back to Login" : "Create Account");
        secondary.getStyleClass().add("secondary-button");
        secondary.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(12);
        form.getStyleClass().add("auth-card");
        form.getChildren().addAll(eyebrow, title, subtitle);

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

        Label brandMark = new Label("SMART STUDENT");
        brandMark.getStyleClass().add("shell-brand-mark");

        Label brandTitle = new Label("Management System");
        brandTitle.getStyleClass().add("auth-hero-title");

        Label brandCopy = new Label("A single desktop workspace for enrollment, reporting, and staff operations.");
        brandCopy.getStyleClass().add("auth-hero-copy");
        brandCopy.setWrapText(true);

        Button authThemeButton = new Button(root.getStyleClass().contains(DARK_THEME) ? "Light Mode" : "Dark Mode");
        authThemeButton.getStyleClass().add("secondary-button");
        authThemeButton.setOnAction(event -> {
            toggleTheme();
            authThemeButton.setText(root.getStyleClass().contains(DARK_THEME) ? "Light Mode" : "Dark Mode");
        });

        VBox brandPanel = new VBox(14,
                brandMark,
                brandTitle,
                brandCopy,
                new HBox(8,
                        buildTag("Students", "neutral-tag"),
                        buildTag("Reports", "neutral-tag"),
                        buildTag("Audit", "neutral-tag")
                ),
                authThemeButton
        );
        brandPanel.getStyleClass().add("auth-brand-panel");

        HBox authShell = new HBox(28, brandPanel, form);
        authShell.getStyleClass().add("auth-shell");
        authShell.setAlignment(Pos.CENTER);

        StackPane wrapper = new StackPane(authShell);
        wrapper.getStyleClass().add("auth-wrapper");

        return wrapper;
    }

    private void loginUser(String email, String password, Label error) {
        String normalizedEmail = clean(email).toLowerCase();

        if (!normalizedEmail.matches(EMAIL_PATTERN)) {
            error.setText("Enter a valid email address.");
            return;
        }

        if (password == null || !password.matches(PASSWORD_PATTERN)) {
            error.setText(PASSWORD_REQUIREMENTS_MESSAGE);
            return;
        }

        try {
            Staff user = authService.login(normalizedEmail, password);

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

        if (!password.getText().matches(PASSWORD_PATTERN)) {
            error.setText(PASSWORD_REQUIREMENTS_MESSAGE);
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
        root.setTop(buildShellHeader());

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        HBox statusBar = new HBox(statusLabel);
        statusBar.getStyleClass().add("status-bar");

        root.setBottom(statusBar);
        BorderPane.setMargin(statusBar, new Insets(0, 24, 24, 24));

        registerShortcuts();
        updateStatus("Signed in as " + currentUser.getFirstName() + " " + currentUser.getLastName() + ".");
    }

    private Node buildShellHeader() {
        Label brandMark = new Label("SMART STUDENT");
        brandMark.getStyleClass().add("shell-brand-mark");

        Label brandTitle = new Label("Management System");
        brandTitle.getStyleClass().add("shell-brand-title");

        Label brandSubtitle = new Label(currentUser.getJobType() + " Workspace");
        brandSubtitle.getStyleClass().add("shell-brand-subtitle");

        VBox brandBlock = new VBox(2, brandMark, brandTitle, brandSubtitle);

        dashboardNavButton = buildNavigationButton("Dashboard", this::showDashboardPage);
        studentsNavButton = buildNavigationButton("Students", this::showStudentsPage);
        profileNavButton = buildNavigationButton("Profile", this::showProfilePage);

        HBox navBar = new HBox(8, dashboardNavButton, studentsNavButton, profileNavButton);
        navBar.getStyleClass().add("shell-nav");

        if (isAdmin()) {
            auditNavButton = buildNavigationButton("Audit Logs", this::showAuditLogsPage);
            navBar.getChildren().add(auditNavButton);
        } else {
            auditNavButton = null;
        }

        themeToggle = new ToggleButton();
        themeToggle.getStyleClass().add("secondary-button");
        updateThemeButtonText();
        themeToggle.setOnAction(event -> toggleTheme());

        Button helpButton = new Button("Help");
        helpButton.getStyleClass().add("secondary-button");
        helpButton.setOnAction(event -> showHelp());

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("danger-button");
        logoutButton.setOnAction(event -> logoutUser());

        Label userChip = new Label(currentUser.getFirstName() + " " + currentUser.getLastName()
                + " | " + currentUser.getJobType());
        userChip.getStyleClass().add("user-chip");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox utilities = new HBox(10, themeToggle, helpButton, userChip, logoutButton);
        utilities.setAlignment(Pos.CENTER_RIGHT);

        HBox headerRow = new HBox(18, brandBlock, navBar, spacer, utilities);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        VBox shellHeader = new VBox(headerRow);
        shellHeader.getStyleClass().add("shell-header");

        return shellHeader;
    }

    private Button buildNavigationButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setOnAction(event -> action.run());
        return button;
    }

    private void setActiveNavigation(Button activeButton) {
        Button[] buttons = {dashboardNavButton, studentsNavButton, profileNavButton, auditNavButton};

        for (Button button : buttons) {
            if (button != null) {
                button.getStyleClass().remove("nav-button-active");
            }
        }

        if (activeButton != null && !activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    private Node buildPageHeader(String eyebrowText, String titleText, String subtitleText, Node... actions) {
        Label eyebrow = new Label(eyebrowText);
        eyebrow.getStyleClass().add("page-eyebrow");

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");

        Label subtitle = new Label(subtitleText);
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        VBox copy = new VBox(4, eyebrow, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, copy, spacer);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("page-header");

        if (actions.length > 0) {
            HBox actionRow = new HBox(10);
            actionRow.setAlignment(Pos.CENTER_RIGHT);
            actionRow.getChildren().addAll(actions);
            header.getChildren().add(actionRow);
        }

        return header;
    }

    private VBox buildFieldGroup(String labelText, Region control, double width) {
        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");

        control.setPrefWidth(width);

        VBox group = new VBox(6, label, control);
        group.getStyleClass().add("field-group");
        return group;
    }

    private Label buildTag(String text, String variantClass) {
        Label tag = new Label(text);
        tag.getStyleClass().addAll("tag", variantClass);
        return tag;
    }

    private VBox buildDetailItem(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.getStyleClass().add("detail-label");

        Label value = new Label(valueText);
        value.getStyleClass().add("detail-value");
        value.setWrapText(true);

        return new VBox(4, label, value);
    }

    private void registerShortcuts() {
        Scene scene = root.getScene();

        if (scene == null || shortcutsRegistered) {
            return;
        }

        scene.getAccelerators().put(KeyCombination.keyCombination("Shortcut+N"), () -> {
            if (currentUser != null) {
                showStudentsPage();
                clearForm();
            }
        });
        scene.getAccelerators().put(KeyCombination.keyCombination("Shortcut+S"), () -> {
            if (currentUser != null && studentTable != null && studentTable.getSelectionModel().getSelectedItem() != null) {
                saveStudent(true);
            }
        });
        scene.getAccelerators().put(KeyCombination.keyCombination("Shortcut+D"), () -> {
            if (currentUser != null && studentTable != null && studentTable.getSelectionModel().getSelectedItem() != null) {
                deleteSelectedStudent();
            }
        });
        scene.getAccelerators().put(KeyCombination.keyCombination("Shortcut+E"), () -> {
            if (currentUser != null) {
                exportCsv();
            }
        });
        scene.getAccelerators().put(KeyCombination.keyCombination("Shortcut+P"), () -> {
            if (currentUser != null) {
                exportPdf();
            }
        });
        scene.getAccelerators().put(KeyCombination.keyCombination("Shortcut+T"), this::toggleTheme);
        scene.getAccelerators().put(KeyCombination.keyCombination("F1"), this::showHelp);

        shortcutsRegistered = true;
    }

    private void logoutUser() {
        currentUser = null;
        studentItems.clear();
        auditLogItems.clear();
        showLoginView();
    }

    private void showDashboardPage() {
        try {
            setActiveNavigation(dashboardNavButton);
            DashboardStats stats = dashboardService.getStats();

            Button refreshButton = new Button("Refresh");
            refreshButton.getStyleClass().add("secondary-button");
            refreshButton.setOnAction(event -> showDashboardPage());

            FlowPane statCards = new FlowPane(14, 14,
                    statCard("Total Students", String.valueOf(stats.totalStudents())),
                    statCard("Average GPA", formatDouble(stats.averageGpa())),
                    statCard("Highest GPA", formatDouble(stats.highestGpa())),
                    statCard("Lowest GPA", formatDouble(stats.lowestGpa()))
            );
            statCards.getStyleClass().add("metric-grid");
            statCards.setPrefWrapLength(1200);

            TableView<Map.Entry<String, Integer>> departmentTable = new TableView<>();
            departmentTable.getStyleClass().add("student-table");
            departmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

            TableColumn<Map.Entry<String, Integer>, String> departmentColumn = new TableColumn<>("Department");
            departmentColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getKey()));

            TableColumn<Map.Entry<String, Integer>, String> totalColumn = new TableColumn<>("Students");
            totalColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getValue())));

            departmentTable.getColumns().addAll(departmentColumn, totalColumn);
            departmentTable.setItems(FXCollections.observableArrayList(stats.studentsByDepartment().entrySet()));

            VBox heroPanel = new VBox(10,
                    buildTag("Live Summary", "accent-tag"),
                    new Label("Enrollment Snapshot"),
                    new Label(stats.totalStudents() + " active records across " + stats.studentsByDepartment().size() + " departments.")
            );
            heroPanel.getStyleClass().addAll("surface-panel", "hero-panel");
            ((Label) heroPanel.getChildren().get(1)).getStyleClass().add("hero-title");
            ((Label) heroPanel.getChildren().get(2)).getStyleClass().add("section-copy");

            VBox departmentPanel = new VBox(12,
                    new Label("Students by Department"),
                    new Label("Department counts update from the current database state."),
                    departmentTable
            );
            departmentPanel.getStyleClass().addAll("surface-panel", "table-panel");
            ((Label) departmentPanel.getChildren().get(0)).getStyleClass().add("section-title");
            ((Label) departmentPanel.getChildren().get(1)).getStyleClass().add("section-copy");
            VBox.setVgrow(departmentTable, Priority.ALWAYS);

            VBox panel = new VBox(18,
                    buildPageHeader(
                            "Overview",
                            "Dashboard",
                            "Live student metrics and department distribution.",
                            refreshButton
                    ),
                    heroPanel,
                    statCards,
                    departmentPanel
            );
            panel.getStyleClass().add("workspace");
            VBox.setVgrow(departmentPanel, Priority.ALWAYS);

            root.setCenter(panel);
            updateStatus("Dashboard loaded.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Dashboard Error", "Could not load dashboard: " + e.getMessage());
        }
    }

    private Node statCard(String label, String value) {
        Label title = new Label(label);
        title.getStyleClass().add("metric-label");

        Label number = new Label(value);
        number.getStyleClass().add("metric-value");

        VBox card = new VBox(8, title, number);
        card.getStyleClass().addAll("surface-panel", "metric-card");
        card.setPrefWidth(220);
        card.setMinWidth(200);

        return card;
    }

    private void showStudentsPage() {
        setActiveNavigation(studentsNavButton);
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
            loadGradesForSelectedStudent();

            boolean hasSelection = selected != null;
            boolean canModify = isAdmin() && hasSelection;

            if (deleteButton != null) {
                deleteButton.setDisable(!canModify);
            }

            if (addButton != null) {
                addButton.setText(hasSelection && isAdmin() ? "Update Student" : "Add Student");
            }
        });

        studentSummaryLabel = new Label("Loading student records...");
        studentSummaryLabel.getStyleClass().add("summary-label");

        Button exportCsvButton = new Button("Export CSV");
        exportCsvButton.getStyleClass().add("secondary-button");
        exportCsvButton.setOnAction(event -> exportCsv());

        Button exportPdfButton = new Button("Export PDF");
        exportPdfButton.getStyleClass().add("secondary-button");
        exportPdfButton.setOnAction(event -> exportPdf());

        VBox tableHeader = new VBox(4, new Label("Student Directory"), studentSummaryLabel);
        ((Label) tableHeader.getChildren().get(0)).getStyleClass().add("section-title");

        VBox tablePanel = new VBox(16, tableHeader, buildStudentFilters(), studentTable, buildStudentPaginationControls());
        tablePanel.getStyleClass().addAll("surface-panel", "table-panel");
        VBox.setVgrow(studentTable, Priority.ALWAYS);

        Node formPanel = buildStudentForm();
        Node gradePanel = buildGradePanel();
        VBox aside = new VBox(16, formPanel, gradePanel);
        aside.setPrefWidth(390);
        aside.setMinWidth(360);

        SplitPane contentSplit = new SplitPane(tablePanel, aside);
        contentSplit.setDividerPositions(0.72);
        contentSplit.getStyleClass().add("content-split");

        VBox page = new VBox(18,
                buildPageHeader(
                        "Records",
                        "Students",
                        "Search, filter, and maintain student records.",
                        exportCsvButton,
                        exportPdfButton
                ),
                contentSplit
        );
        page.getStyleClass().add("workspace");
        VBox.setVgrow(contentSplit, Priority.ALWAYS);

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
        apply.getStyleClass().add("primary-button");
        apply.setOnAction(event -> {
            currentStudentPage = 1;
            loadStudentPage();
        });

        Button clear = new Button("Clear");
        clear.getStyleClass().add("secondary-button");
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

        HBox actions = new HBox(10, apply, clear);
        actions.setAlignment(Pos.BOTTOM_LEFT);

        FlowPane filters = new FlowPane();
        filters.setHgap(12);
        filters.setVgap(12);
        filters.getChildren().addAll(
                buildFieldGroup("Search", searchField, 320),
                buildFieldGroup("Department", filterDepartmentBox, 190),
                buildFieldGroup("Major", filterMajorBox, 190),
                buildFieldGroup("Min GPA", minGpaField, 110),
                buildFieldGroup("Max GPA", maxGpaField, 110),
                buildFieldGroup("Sort By", sortByBox, 150),
                buildFieldGroup("Direction", sortDirectionBox, 150),
                buildFieldGroup("Actions", actions, 180)
        );
        filters.getStyleClass().add("toolbar-wrap");

        return filters;
    }

    private Node buildStudentPaginationControls() {
        Button previous = new Button("Previous");
        previous.getStyleClass().add("secondary-button");
        previous.setOnAction(event -> {
            if (currentStudentPage > 1) {
                currentStudentPage--;
                loadStudentPage();
            }
        });

        Button next = new Button("Next");
        next.getStyleClass().add("secondary-button");
        next.setOnAction(event -> {
            if (currentStudentPage < currentStudentTotalPages) {
                currentStudentPage++;
                loadStudentPage();
            }
        });

        pageSizeBox = new ComboBox<>(FXCollections.observableArrayList(5, 10, 20, 50));
        pageSizeBox.getSelectionModel().select(Integer.valueOf(currentStudentPageSize));
        pageSizeBox.setPrefWidth(90);
        pageSizeBox.setOnAction(event -> {
            currentStudentPageSize = pageSizeBox.getValue();
            currentStudentPage = 1;
            loadStudentPage();
        });

        pageLabel = new Label();
        pageLabel.getStyleClass().add("summary-label");

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
        firstNameField.setPromptText("First name");
        lastNameField = new TextField();
        lastNameField.setPromptText("Last name");

        departmentBox = new ComboBox<>(FXCollections.observableArrayList(departments));
        departmentBox.setPromptText("Select department");
        majorBox = new ComboBox<>(FXCollections.observableArrayList(majors));
        majorBox.setPromptText("Select major");

        gpaField = new TextField();
        gpaField.setPromptText("0.00 - 4.00");

        idField.setPromptText("Numeric ID");
        idField.setMaxWidth(Double.MAX_VALUE);
        firstNameField.setMaxWidth(Double.MAX_VALUE);
        lastNameField.setMaxWidth(Double.MAX_VALUE);
        departmentBox.setMaxWidth(Double.MAX_VALUE);
        majorBox.setMaxWidth(Double.MAX_VALUE);
        gpaField.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        grid.addRow(0, new Label("Student ID"), idField);
        grid.addRow(1, new Label("First Name"), firstNameField);
        grid.addRow(2, new Label("Last Name"), lastNameField);
        grid.addRow(3, new Label("Department"), departmentBox);
        grid.addRow(4, new Label("Major"), majorBox);
        grid.addRow(5, new Label("GPA"), gpaField);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(96);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelColumn, fieldColumn);

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
        clear.getStyleClass().add("secondary-button");
        clear.setOnAction(event -> clearForm());

        HBox actions = new HBox(10, addButton, deleteButton, clear);
        actions.setAlignment(Pos.CENTER_LEFT);

        Label formTitle = new Label("Student Details");
        formTitle.getStyleClass().add("section-title");

        Label accessNote = new Label(isAdmin()
                ? "Select a row to update it, or clear the form to add a new record."
                : "Staff accounts can review records and export the current result set.");
        accessNote.getStyleClass().add("section-copy");
        accessNote.setWrapText(true);

        Label accessBadge = buildTag(isAdmin() ? "Administrator" : "Read Only", isAdmin() ? "accent-tag" : "neutral-tag");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox formHeader = new HBox(10, formTitle, headerSpacer, accessBadge);
        formHeader.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(14, formHeader, accessNote, grid, actions);
        form.getStyleClass().addAll("surface-panel", "form-panel", "aside-panel");
        form.setPrefWidth(360);
        form.setMinWidth(340);

        boolean editable = isAdmin();
        idField.setDisable(!editable);
        firstNameField.setDisable(!editable);
        lastNameField.setDisable(!editable);
        departmentBox.setDisable(!editable);
        majorBox.setDisable(!editable);
        gpaField.setDisable(!editable);

        return form;
    }

    private Node buildGradePanel() {
        gradeSummaryLabel = new Label("Select a student to view class grades.");
        gradeSummaryLabel.getStyleClass().add("summary-label");
        gradeSummaryLabel.setWrapText(true);

        gradeTable = new TableView<>(gradeItems);
        gradeTable.getStyleClass().add("student-table");
        gradeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        gradeTable.setPrefHeight(220);

        gradeTable.getColumns().addAll(
                gradeColumn("Class", "classCode"),
                gradeColumn("Grade", "gradeDisplay"),
                gradeColumn("Credits", "credits"),
                gradeColumn("Term", "term")
        );

        gradeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            populateGradeForm(selected);

            boolean canModify = isAdmin() && selected != null;

            if (deleteGradeButton != null) {
                deleteGradeButton.setDisable(!canModify);
            }

            if (saveGradeButton != null) {
                saveGradeButton.setText(selected == null ? "Add Grade" : "Update Grade");
            }
        });

        gradeClassCodeField = new TextField();
        gradeClassCodeField.setPromptText("CSC311");
        gradeClassNameField = new TextField();
        gradeClassNameField.setPromptText("Class name");
        gradeValueField = new TextField();
        gradeValueField.setPromptText("0 - 100");
        gradeCreditsField = new TextField();
        gradeCreditsField.setPromptText("Credits");
        gradeTermField = new TextField();
        gradeTermField.setPromptText("Fall 2026");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        grid.addRow(0, new Label("Code"), gradeClassCodeField);
        grid.addRow(1, new Label("Class"), gradeClassNameField);
        grid.addRow(2, new Label("Grade"), gradeValueField);
        grid.addRow(3, new Label("Credits"), gradeCreditsField);
        grid.addRow(4, new Label("Term"), gradeTermField);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(72);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labelColumn, fieldColumn);

        saveGradeButton = new Button("Add Grade");
        saveGradeButton.getStyleClass().add("primary-button");
        saveGradeButton.setDisable(!isAdmin());
        saveGradeButton.setOnAction(event -> saveGrade());

        deleteGradeButton = new Button("Delete");
        deleteGradeButton.getStyleClass().add("danger-button");
        deleteGradeButton.setDisable(true);
        deleteGradeButton.setOnAction(event -> deleteSelectedGrade());

        Button clearGrade = new Button("Clear");
        clearGrade.getStyleClass().add("secondary-button");
        clearGrade.setOnAction(event -> clearGradeForm());

        HBox actions = new HBox(10, saveGradeButton, deleteGradeButton, clearGrade);
        actions.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Class Grades");
        title.getStyleClass().add("section-title");

        VBox panel = new VBox(14, title, gradeSummaryLabel, gradeTable, grid, actions);
        panel.getStyleClass().addAll("surface-panel", "form-panel", "aside-panel");
        panel.setPrefWidth(390);
        panel.setMinWidth(360);

        boolean editable = isAdmin();
        gradeClassCodeField.setDisable(!editable);
        gradeClassNameField.setDisable(!editable);
        gradeValueField.setDisable(!editable);
        gradeCreditsField.setDisable(!editable);
        gradeTermField.setDisable(!editable);

        return panel;
    }

    private void loadStudentPage() {
        try {
            PaginatedResult<Student> result = studentService.findPage(buildStudentCriteria());

            studentItems.setAll(result.items());
            currentStudentTotalPages = result.totalPages();

            if (pageLabel != null) {
                pageLabel.setText("Page " + result.page() + " of " + result.totalPages() + " | " + result.totalItems() + " records");
            }

            if (studentSummaryLabel != null) {
                studentSummaryLabel.setText(result.totalItems() + " records matched the current filters.");
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

    private TableColumn<StudentGrade, Object> gradeColumn(String title, String property) {
        TableColumn<StudentGrade, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private void loadGradesForSelectedStudent() {
        Student selected = studentTable == null ? null : studentTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            gradeItems.clear();
            clearGradeForm();

            if (gradeSummaryLabel != null) {
                gradeSummaryLabel.setText("Select a student to view class grades.");
            }

            return;
        }

        try {
            gradeItems.setAll(studentService.findGradesForStudent(selected.getID()));
            StudentGradeStats stats = studentService.getGradeStatsForStudent(selected.getID());

            if (gradeSummaryLabel != null) {
                gradeSummaryLabel.setText(
                        stats.classCount() + " classes | "
                                + stats.totalCredits() + " credits | Avg "
                                + formatDouble(stats.averageGrade()) + " | High "
                                + formatDouble(stats.highestGrade()) + " | Low "
                                + formatDouble(stats.lowestGrade())
                );
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Grade Load Failed", "Could not load student grades: " + e.getMessage());
        }
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

    private void populateGradeForm(StudentGrade grade) {
        if (gradeClassCodeField == null) {
            return;
        }

        if (grade == null) {
            clearGradeFormFieldsOnly();
            return;
        }

        gradeClassCodeField.setText(grade.getClassCode());
        gradeClassNameField.setText(grade.getClassName());
        gradeValueField.setText(grade.getGradeDisplay());
        gradeCreditsField.setText(String.valueOf(grade.getCredits()));
        gradeTermField.setText(grade.getTerm());
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

    private void saveGrade() {
        if (!isAdmin()) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only administrators can modify student grades.");
            return;
        }

        Student selectedStudent = studentTable == null ? null : studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            showAlert(Alert.AlertType.WARNING, "No Student Selected", "Select a student before adding a class grade.");
            return;
        }

        String validation = validateGradeForm();

        if (validation != null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", validation);
            return;
        }

        StudentGrade grade = new StudentGrade();
        StudentGrade selectedGrade = gradeTable.getSelectionModel().getSelectedItem();

        if (selectedGrade != null) {
            grade.setId(selectedGrade.getId());
        }

        copyGradeFormIntoGrade(grade, selectedStudent.getID());

        try {
            if (selectedGrade == null) {
                studentService.addGrade(currentUser, grade);
                updateStatus("Added grade for " + selectedStudent.getID() + ".");
            } else {
                studentService.updateGrade(currentUser, grade);
                updateStatus("Updated grade for " + selectedStudent.getID() + ".");
            }

            clearGradeForm();
            loadGradesForSelectedStudent();
        } catch (SecurityException e) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", e.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save grade: " + e.getMessage());
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

    private void deleteSelectedGrade() {
        if (!isAdmin()) {
            showAlert(Alert.AlertType.WARNING, "Access Denied", "Only administrators can delete student grades.");
            return;
        }

        StudentGrade selectedGrade = gradeTable == null ? null : gradeTable.getSelectionModel().getSelectedItem();

        if (selectedGrade == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a class grade before deleting.");
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete the grade for " + selectedGrade.getClassCode() + "?",
                ButtonType.CANCEL,
                ButtonType.OK
        );

        styleDialog(confirm.getDialogPane());
        confirm.setHeaderText("Confirm Grade Delete");

        confirm.showAndWait().filter(ButtonType.OK::equals).ifPresent(button -> {
            try {
                studentService.deleteGrade(currentUser, selectedGrade);
                clearGradeForm();
                loadGradesForSelectedStudent();
                updateStatus("Deleted grade for " + selectedGrade.getStudentId() + ".");
            } catch (SecurityException e) {
                showAlert(Alert.AlertType.WARNING, "Access Denied", e.getMessage());
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Could not delete grade: " + e.getMessage());
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

    private void copyGradeFormIntoGrade(StudentGrade grade, String studentId) {
        grade.setStudentId(studentId);
        grade.setClassCode(clean(gradeClassCodeField.getText()).toUpperCase());
        grade.setClassName(capitalize(clean(gradeClassNameField.getText())));
        grade.setGrade(new BigDecimal(clean(gradeValueField.getText())));
        grade.setCredits(Integer.parseInt(clean(gradeCreditsField.getText())));
        grade.setTerm(clean(gradeTermField.getText()));
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

    private String validateGradeForm() {
        if (isBlank(gradeClassCodeField.getText()) || isBlank(gradeClassNameField.getText())
                || isBlank(gradeValueField.getText()) || isBlank(gradeCreditsField.getText())) {
            return "Class code, class name, grade, and credits are required.";
        }

        try {
            double grade = Double.parseDouble(clean(gradeValueField.getText()));

            if (grade < 0 || grade > 100) {
                return "Grade must be between 0 and 100.";
            }
        } catch (NumberFormatException exception) {
            return "Grade must be a number.";
        }

        try {
            int credits = Integer.parseInt(clean(gradeCreditsField.getText()));

            if (credits < 0 || credits > 12) {
                return "Credits must be between 0 and 12.";
            }
        } catch (NumberFormatException exception) {
            return "Credits must be a whole number.";
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

        gradeItems.clear();
        clearGradeForm();

        if (gradeSummaryLabel != null) {
            gradeSummaryLabel.setText("Select a student to view class grades.");
        }
    }

    private void clearGradeForm() {
        clearGradeFormFieldsOnly();

        if (gradeTable != null) {
            gradeTable.getSelectionModel().clearSelection();
        }

        if (saveGradeButton != null) {
            saveGradeButton.setText("Add Grade");
        }

        if (deleteGradeButton != null) {
            deleteGradeButton.setDisable(true);
        }
    }

    private void clearGradeFormFieldsOnly() {
        if (gradeClassCodeField == null) {
            return;
        }

        gradeClassCodeField.clear();
        gradeClassNameField.clear();
        gradeValueField.clear();
        gradeCreditsField.clear();
        gradeTermField.clear();
    }

    private void showProfilePage() {
        setActiveNavigation(profileNavButton);

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> {
            refreshCurrentUser();
            showProfilePage();
        });

        staffProfileImageView = new ImageView();
        staffProfileImageView.setFitWidth(176);
        staffProfileImageView.setFitHeight(176);
        staffProfileImageView.setPreserveRatio(true);

        loadProfileImage();

        StackPane avatarFrame = new StackPane(staffProfileImageView);
        avatarFrame.getStyleClass().add("profile-avatar-frame");

        Label name = new Label(currentUser.getFirstName() + " " + currentUser.getLastName());
        name.getStyleClass().add("page-title");

        Label email = new Label(currentUser.getEmail());
        email.getStyleClass().add("page-subtitle");

        Button upload = new Button("Upload Profile Picture");
        upload.getStyleClass().add("primary-button");
        upload.setOnAction(event -> uploadProfilePicture());

        HBox tags = new HBox(8,
                buildTag(currentUser.getJobType(), "accent-tag"),
                buildTag(currentUser.getDepartment(), "neutral-tag")
        );

        VBox profileCard = new VBox(16, avatarFrame, name, email, tags, upload);
        profileCard.getStyleClass().addAll("surface-panel", "profile-card");
        profileCard.setPrefWidth(360);

        VBox detailsCard = new VBox(14,
                new Label("Account Details"),
                buildDetailItem("Staff ID", currentUser.getID()),
                buildDetailItem("Email", currentUser.getEmail()),
                buildDetailItem("Role", currentUser.getJobType()),
                buildDetailItem("Department", currentUser.getDepartment()),
                buildDetailItem("Image Status", isBlank(currentUser.getImgURL()) ? "No profile image uploaded." : "Profile image available.")
        );
        detailsCard.getStyleClass().addAll("surface-panel", "form-panel");
        ((Label) detailsCard.getChildren().get(0)).getStyleClass().add("section-title");

        HBox body = new HBox(18, profileCard, detailsCard);
        body.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(detailsCard, Priority.ALWAYS);

        VBox page = new VBox(18,
                buildPageHeader(
                        "Account",
                        "Profile",
                        "Manage your staff identity and profile image.",
                        refreshButton
                ),
                body
        );
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

        setActiveNavigation(auditNavButton);

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setOnAction(event -> loadAuditLogs());

        auditLogTable = new TableView<>(auditLogItems);
        auditLogTable.getStyleClass().add("student-table");
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

        auditSummaryLabel = new Label("Loading audit activity...");
        auditSummaryLabel.getStyleClass().add("summary-label");

        Node pagination = buildAuditPaginationControls();

        VBox logPanel = new VBox(16,
                new VBox(4, new Label("Activity Feed"), auditSummaryLabel),
                auditLogTable,
                pagination
        );
        logPanel.getStyleClass().addAll("surface-panel", "table-panel");
        ((Label) ((VBox) logPanel.getChildren().get(0)).getChildren().get(0)).getStyleClass().add("section-title");
        VBox.setVgrow(auditLogTable, Priority.ALWAYS);

        VBox page = new VBox(18,
                buildPageHeader(
                        "Administration",
                        "Audit Logs",
                        "Recent staff actions, exports, and record changes.",
                        refreshButton
                ),
                logPanel
        );
        page.getStyleClass().add("workspace");
        VBox.setVgrow(logPanel, Priority.ALWAYS);

        root.setCenter(page);

        currentAuditPage = 1;
        loadAuditLogs();
    }

    private Node buildAuditPaginationControls() {
        Button previous = new Button("Previous");
        previous.getStyleClass().add("secondary-button");
        previous.setOnAction(event -> {
            if (currentAuditPage > 1) {
                currentAuditPage--;
                loadAuditLogs();
            }
        });

        Button next = new Button("Next");
        next.getStyleClass().add("secondary-button");
        next.setOnAction(event -> {
            if (currentAuditPage < currentAuditTotalPages) {
                currentAuditPage++;
                loadAuditLogs();
            }
        });

        ComboBox<Integer> auditPageSizeBox = new ComboBox<>(FXCollections.observableArrayList(5, 10, 20, 50));
        auditPageSizeBox.getSelectionModel().select(Integer.valueOf(currentAuditPageSize));
        auditPageSizeBox.setPrefWidth(90);
        auditPageSizeBox.setOnAction(event -> {
            currentAuditPageSize = auditPageSizeBox.getValue();
            currentAuditPage = 1;
            loadAuditLogs();
        });

        auditPageLabel = new Label();
        auditPageLabel.getStyleClass().add("summary-label");

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
                auditPageLabel.setText("Page " + result.page() + " of " + result.totalPages() + " | " + result.totalItems() + " logs");
            }

            if (auditSummaryLabel != null) {
                auditSummaryLabel.setText(result.totalItems() + " log entries available.");
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
                        + "Select a student to view and manage individual class grades with average, high, low, and credit totals.\n"
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
