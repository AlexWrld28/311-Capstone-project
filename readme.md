# Smart Student Management System

A JavaFX desktop application for managing student records, staff accounts, reporting, profile images, and administrative audit logs with PostgreSQL persistence and MinIO object storage.

## Overview

The Smart Student Management System is a CSC311 capstone project built as a full-stack desktop application. It uses a modern JavaFX front end, a PostgreSQL database backend, and MinIO object storage for staff profile images.

The original project proposal planned to use Microsoft Azure SQL Database and Azure Blob Storage. During development, PostgreSQL and MinIO were selected instead because they provided a more reliable development environment, easier JDBC integration, and stable object storage while still meeting the core cloud-backed persistence goals of the project.

The application supports staff login and registration, role-based access control, student management, reporting, dashboard statistics, profile pictures, and admin-only audit logs.

---

# Core Features

## Authentication

* Staff login
* Staff registration
* BCrypt password hashing
* Persistent staff accounts stored in PostgreSQL
* Default administrator seeding when the staff table is empty

## Role-Based Permissions

The system supports two main roles:

| Role          | Permissions                                                                           |
| ------------- | ------------------------------------------------------------------------------------- |
| Administrator | View, export, add, update, delete students, view audit logs, manage own profile image |
| Staff         | View students, search/filter students, export reports, manage own profile image       |

Administrators can modify records. Staff accounts are limited to viewing and exporting student information.

## Dashboard

The dashboard provides summary information about the student population:

* Total students
* Average GPA
* Highest GPA
* Lowest GPA
* Students grouped by department

## Student Management

Administrators can:

* Add students
* Update students
* Delete students
* Search student records
* Filter by department and major
* Filter by GPA range
* Sort records
* Navigate paginated results

Staff users can view, search, filter, sort, paginate, and export student records, but cannot modify them.

## Pagination

The student table supports pagination with configurable page sizes.

Supported page sizes:

* 5
* 10
* 20
* 50

This prevents the application from loading every student record into the table at once.

## Search and Filtering

The student page supports:

* Search by ID
* Search by first name
* Search by last name
* Search by department
* Search by major
* Department filtering
* Major filtering
* Minimum GPA filtering
* Maximum GPA filtering
* Sort by ID, first name, last name, department, major, or GPA

## Reporting

The application can export filtered student records to:

* CSV
* PDF

Exports respect the current search and filter criteria.

## Staff Profile Pictures

Staff users can upload profile pictures from the profile page.

Image storage flow:

1. User selects an image from the desktop application.
2. Image uploads to MinIO.
3. MinIO returns a public object URL.
4. The full image URL is stored in PostgreSQL under `staff.img_url`.
5. The image can be displayed in the profile page and audit log views.

Example stored image URL:

```txt
http://108.14.0.161:9000/staff-images/staff/A001/generated-image-id.png
```

## Audit Logs

Administrators can view audit logs from the admin-only audit logs page.

The system tracks actions such as:

* Student added
* Student updated
* Student deleted
* CSV exported
* PDF exported
* Staff profile picture uploaded

Audit logs include:

* Staff ID
* Staff name
* Staff email
* Staff profile image URL
* Action
* Student ID, when applicable
* Details
* Timestamp

---

# Technologies Used

| Technology  | Purpose                                               |
| ----------- | ----------------------------------------------------- |
| Java        | Main application language                             |
| JavaFX      | Desktop user interface                                |
| Maven       | Dependency management and build system                |
| PostgreSQL  | Relational database storage                           |
| JDBC        | Database connectivity                                 |
| MinIO       | S3-compatible object storage for staff profile images |
| BCrypt      | Secure password hashing                               |
| OpenCSV     | CSV export support                                    |
| OpenPDF     | PDF report generation                                 |
| dotenv-java | Local environment variable loading                    |

---

# Project Structure

```txt
src/
├── main/
│   ├── java/
│   │   └── org/csc311/capstone/
│   │       ├── db/
│   │       │   ├── Database.java
│   │       │   ├── SchemaRepository.java
│   │       │   ├── ReferenceDataRepository.java
│   │       │   ├── StudentRepository.java
│   │       │   ├── StaffRepository.java
│   │       │   └── AuditLogRepository.java
│   │       │
│   │       ├── models/
│   │       │   ├── Student.java
│   │       │   ├── Staff.java
│   │       │   ├── AuditLog.java
│   │       │   ├── DashboardStats.java
│   │       │   ├── PaginatedResult.java
│   │       │   └── StudentSearchCriteria.java
│   │       │
│   │       ├── services/
│   │       │   ├── AuthService.java
│   │       │   ├── StudentService.java
│   │       │   ├── DashboardService.java
│   │       │   ├── ProfileService.java
│   │       │   └── AuditService.java
│   │       │
│   │       ├── storage/
│   │       │   └── MinioStorageService.java
│   │       │
│   │       ├── util/
│   │       │   └── DataExportHandler.java
│   │       │
│   │       ├── HelloApplication.java
│   │       ├── HelloController.java
│   │       └── module-info.java
│   │
│   └── resources/
│       └── org/csc311/capstone/
│           ├── hello-view.fxml
│           └── styles.css
│
├── pom.xml
├── .env
└── README.md
```

---

# Architecture

The application follows a cleaner layered structure:

| Layer        | Responsibility                                              |
| ------------ | ----------------------------------------------------------- |
| Controller   | Handles JavaFX UI events and page rendering                 |
| Services     | Handles business logic, permissions, and workflow decisions |
| Repositories | Handles SQL queries and database persistence                |
| Models       | Represents application data objects                         |
| Storage      | Handles MinIO image upload logic                            |
| Utilities    | Handles export helpers and shared tools                     |

This separates UI code from database access and business rules.

---

# Database Design

The database uses PostgreSQL.

## Tables

### `students`

Stores student records.

```sql
CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(20) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    major VARCHAR(100) NOT NULL,
    department_id INTEGER REFERENCES departments(id),
    major_id INTEGER REFERENCES majors(id),
    gpa NUMERIC(3,2) NOT NULL
);
```

### `staff`

Stores staff login accounts and profile image URLs.

```sql
CREATE TABLE IF NOT EXISTS staff (
    id VARCHAR(20) PRIMARY KEY,
    role_id INTEGER REFERENCES roles(id),
    job_type VARCHAR(100) NOT NULL,
    img_url TEXT,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    password_hash TEXT NOT NULL
);
```

### `roles`

Stores available user roles.

```sql
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);
```

### `departments`

Stores valid departments.

```sql
CREATE TABLE IF NOT EXISTS departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);
```

### `majors`

Stores valid majors.

```sql
CREATE TABLE IF NOT EXISTS majors (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);
```

### `audit_logs`

Stores administrative activity history.

```sql
CREATE TABLE IF NOT EXISTS audit_logs (
    id SERIAL PRIMARY KEY,
    staff_id VARCHAR(20),
    staff_name VARCHAR(255),
    staff_email VARCHAR(255),
    staff_img_url TEXT,
    action VARCHAR(100) NOT NULL,
    student_id VARCHAR(20),
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

# Environment Variables

Create a `.env` file in the project root, next to `pom.xml`.

```env
DB_HOST=your_database_host
DB_PORT=5432
DB_NAME=studentdb
DB_USER=admin
DB_PASSWORD="your_postgres_password"

MINIO_ENDPOINT=http://your_minio_host:9000
MINIO_PUBLIC_URL=http://your_minio_host:9000
MINIO_ACCESS_KEY=admin
MINIO_SECRET_KEY="your_minio_password"
MINIO_BUCKET=staff-images
```

Do not commit `.env` to GitHub.

Add this to `.gitignore`:

```gitignore
.env
target/
```

---

# MinIO Setup

The application stores staff profile pictures in a MinIO bucket.

Required bucket:

```txt
staff-images
```

Example MinIO CLI setup:

```bash
mc alias set capstone http://your_minio_host:9000 admin "your_minio_password"
mc mb --ignore-existing capstone/staff-images
mc anonymous set download capstone/staff-images
```

The public download policy is required so JavaFX can display staff profile pictures directly from their stored URL.

---

# Installation

## Clone Repository

```bash
git clone https://github.com/AlexWrld28/311-Capstone-project.git
```

## Navigate Into Project

```bash
cd 311-Capstone-project
```

## Configure Environment

Create `.env` in the project root and fill in PostgreSQL and MinIO values.

## Install Dependencies

```bash
mvn clean install
```

## Run Application

```bash
mvn javafx:run
```

---

# Default Login

If the staff table is empty, the app seeds a default administrator account.

```txt
Email: admin@school.edu
Password: admin123
```

Change this password for any real deployment.

---

# Maven Dependencies

Important dependencies:


```
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.10</version>
</dependency>

<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>3.2.0</version>
</dependency>

<dependency>
    <groupId>at.favre.lib</groupId>
    <artifactId>bcrypt</artifactId>
    <version>0.10.2</version>
</dependency>

<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.6.0</version>
</dependency>

<dependency>
    <groupId>com.github.librepdf</groupId>
    <artifactId>openpdf</artifactId>
    <version>2.0.3</version>
</dependency>

<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.12.0</version>
</dependency>
```


---

# Pages

## Login Page

Allows existing staff to sign in.

## Registration Page

Allows new staff accounts to register.

## Dashboard Page

Displays student statistics and department breakdowns.

## Students Page

Displays the searchable, sortable, paginated student table.

Administrators can add, update, and delete students from this page.

Staff users can view and export only.

## Profile Page

Displays staff profile details and allows staff users to upload a profile picture.

## Audit Logs Page

Admin-only page showing system activity logs.

---

# Report Export

CSV and PDF exports are available from the Reports menu.

Both exports respect current student filters and search criteria.

---

# Security Notes

* Passwords are hashed with BCrypt.
* Database credentials are stored in `.env`.
* MinIO credentials are stored in `.env`.
* `.env` must not be committed.
* Rotate any credentials that were exposed during development.
* Staff profile image objects are publicly readable for JavaFX display.
* Production systems should use signed URLs instead of public object policies.

---

# Known Notes

* Azure was part of the original proposal but was replaced with PostgreSQL and MinIO due to development stability issues.
* PostgreSQL still satisfies the project goal of persistent database-backed storage.
* MinIO replaces Azure Blob Storage with S3-compatible object storage.
* The UI is currently built programmatically in `HelloController` while `hello-view.fxml` provides the root container.

---
---

# Authors

GROUP 2:

* Gurkirat Singh
* Alex Zirilli
* Jake Dunn
* Benji Sanoff-Wiener
* Charles Gonzalez

---

# License

This project is for educational and academic use.
