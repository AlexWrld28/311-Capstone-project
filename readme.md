# Smart Student Management System

A JavaFX desktop application for managing student records and staff accounts with PostgreSQL persistence.

## Overview

This project is a desktop-based Student Management System built with JavaFX and Maven. It allows staff members to register, log in, and manage student records through a modern graphical interface.

The application supports:

* Staff authentication
* Student record management
* PostgreSQL database persistence
* Search and filtering
* CSV export
* Dark/light theme switching
* GPA validation
* Dynamic UI updates

Originally the project used in-memory collections for data storage. It has now been upgraded to use a live PostgreSQL database for persistent storage.

---

# Features

## Staff Authentication

* Staff account registration
* Secure password hashing using BCrypt
* Staff login system
* Persistent staff storage in PostgreSQL

## Student Management

* Add students
* Update students
* Delete students
* View student records in a JavaFX table
* Input validation
* Numeric-only student ID field
* GPA validation
* Automatic name capitalization

## Search and Filtering

* Search by:

    * Student ID
    * First name
    * Last name
    * GPA

* Filter by:

    * Department
    * Major

## Reporting

* Export filtered student records to CSV

## UI Features

* Modern JavaFX interface
* Responsive layout
* Dark/light theme toggle
* Dynamic add/update button state
* Status messages
* Confirmation dialogs

---

# Technologies Used

| Technology  | Purpose                                |
| ----------- | -------------------------------------- |
| Java 24     | Core application language              |
| JavaFX      | Desktop UI framework                   |
| Maven       | Dependency management and build system |
| PostgreSQL  | Database storage                       |
| JDBC        | Database connectivity                  |
| BCrypt      | Password hashing                       |
| OpenCSV     | CSV exporting                          |
| dotenv-java | Environment variable loading           |

---

# Project Structure

```txt
src/
├── main/
│   ├── java/
│   │   └── org/csc311/capstone/
│   │       ├── db/
│   │       │   ├── Database.java
│   │       │   ├── StudentRepository.java
│   │       │   └── StaffRepository.java
│   │       ├── models/
│   │       │   ├── Student.java
│   │       │   └── Staff.java
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
└── .env
```

---

# Database Setup

## PostgreSQL Requirements

* PostgreSQL server running
* PostgreSQL user with database access
* TCP access enabled

## Example PostgreSQL Configuration

```env
DB_HOST=127.0.0.1
DB_PORT=5432
DB_NAME=studentdb
DB_USER=admin
DB_PASSWORD=your_password_here
```

---

# Environment Variables

Create a `.env` file in the project root.

Example:

```env
DB_HOST=127.0.0.1
DB_PORT=5432
DB_NAME=studentdb
DB_USER=admin
DB_PASSWORD=your_password_here
```

Add `.env` to `.gitignore`.

```gitignore
.env
```

---

# Database Tables

## Students Table

```sql
CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(20) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    major VARCHAR(100) NOT NULL,
    gpa NUMERIC(3,2) NOT NULL
);
```

## Staff Table

```sql
CREATE TABLE IF NOT EXISTS staff (
    id VARCHAR(20) PRIMARY KEY,
    job_type VARCHAR(100) NOT NULL,
    img_url TEXT,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    password_hash TEXT NOT NULL
);
```

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

If the database is empty, a default administrator account is seeded automatically.

```txt
Email: admin@school.edu
Password: admin123
```

Change this password immediately in production.

---

# Maven Dependencies

Main dependencies used in the project:

```xml
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
```

---

# Security Notes

* Passwords are hashed using BCrypt.
* Do not commit `.env` files.
* Rotate exposed database credentials immediately.
* Restrict PostgreSQL access using firewalls and trusted IPs.

---

# Future Improvements

Potential future additions:

* Role-based access control
* Student profile images
* Attendance tracking
* Course management
* PDF report generation
* Audit logging
* Password reset system
* Dashboard analytics
* Pagination
* REST API backend
* Docker deployment

---

# Screenshots

Suggested screenshots to add:

* Login screen
* Student dashboard
* Add/edit student form
* Search/filter functionality
* Dark theme

---

# Authors

CSC311 Capstone Project Team

---

# License

This project is for educational and academic use.
