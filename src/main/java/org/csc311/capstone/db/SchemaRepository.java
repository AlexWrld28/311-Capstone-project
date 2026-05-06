package org.csc311.capstone.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaRepository {

    public static void initialize() throws SQLException {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS roles (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) UNIQUE NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS departments (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) UNIQUE NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS majors (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) UNIQUE NOT NULL
                )
            """);

            stmt.execute("""
                INSERT INTO roles (name) VALUES
                ('Administrator'),
                ('Staff')
                ON CONFLICT (name) DO NOTHING
            """);

            stmt.execute("""
                INSERT INTO departments (name) VALUES
                ('Computer Science'),
                ('Mathematics'),
                ('Business'),
                ('Health Sciences'),
                ('Administration'),
                ('Student Services')
                ON CONFLICT (name) DO NOTHING
            """);

            stmt.execute("""
                INSERT INTO majors (name) VALUES
                ('Software Engineering'),
                ('Data Science'),
                ('Accounting'),
                ('Nursing')
                ON CONFLICT (name) DO NOTHING
            """);

            stmt.execute("""
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
                )
            """);

            stmt.execute("""
                ALTER TABLE staff
                ADD COLUMN IF NOT EXISTS role_id INTEGER REFERENCES roles(id)
            """);

            stmt.execute("""
                ALTER TABLE staff
                ADD COLUMN IF NOT EXISTS img_url TEXT
            """);

            stmt.execute("""
                UPDATE staff s
                SET role_id = r.id
                FROM roles r
                WHERE s.role_id IS NULL
                AND LOWER(s.job_type) = LOWER(r.name)
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id VARCHAR(20) PRIMARY KEY,
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL,
                    department VARCHAR(100) NOT NULL,
                    major VARCHAR(100) NOT NULL,
                    department_id INTEGER REFERENCES departments(id),
                    major_id INTEGER REFERENCES majors(id),
                    gpa NUMERIC(3,2) NOT NULL
                )
            """);

            stmt.execute("""
                ALTER TABLE students
                ADD COLUMN IF NOT EXISTS department_id INTEGER REFERENCES departments(id)
            """);

            stmt.execute("""
                ALTER TABLE students
                ADD COLUMN IF NOT EXISTS major_id INTEGER REFERENCES majors(id)
            """);

            stmt.execute("""
                UPDATE students s
                SET department_id = d.id
                FROM departments d
                WHERE s.department_id IS NULL
                AND LOWER(s.department) = LOWER(d.name)
            """);

            stmt.execute("""
                UPDATE students s
                SET major_id = m.id
                FROM majors m
                WHERE s.major_id IS NULL
                AND LOWER(s.major) = LOWER(m.name)
            """);

            stmt.execute("""
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
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS student_class_grades (
                    id SERIAL PRIMARY KEY,
                    student_id VARCHAR(20) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                    class_name VARCHAR(150) NOT NULL,
                    class_code VARCHAR(50) NOT NULL,
                    grade NUMERIC(5,2) NOT NULL,
                    credits INTEGER NOT NULL DEFAULT 3,
                    term VARCHAR(50),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE INDEX IF NOT EXISTS idx_student_class_grades_student_id
                ON student_class_grades(student_id)
            """);
        }

        StaffRepository.seedDefaultAdminIfEmpty();
    }
}
