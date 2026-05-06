# Smart Student Management System

## Overview
This project is a JavaFX-based Smart Student Management System designed to manage and track student records in an academic environment. It supports viewing, adding, updating, deleting, filtering, and exporting student data.

The application uses an H2 in-memory database for testing, with data initialized from `data.sql`.

---

## Test Login Credentials

Use the following account to log into the system:

Email: admin@school.edu

Password: admin123

---

## Features

- Staff Login & Registration (with password hashing using BCrypt)
- View student records from database
- Add, update, and delete students
- Search and filter students by ID, name, GPA, department, and major
- Export filtered student data to CSV
- Light and Dark theme toggle
- Input validation (GPA range, required fields, numeric ID)

---

## Technology Stack

- Java 21
- JavaFX (UI)
- H2 Database (in-memory, test mode)
- JDBC (database connectivity)
- OpenCSV (CSV export support)
- BCrypt (secure password hashing)

---

## How It Works

- The UI is built using JavaFX
- The controller manages user interactions and updates the UI
- `DBHandler` handles all database operations (CRUD + authentication)
- Data is stored and retrieved from an H2 test database

---

