-- Create Table for Staff
CREATE TABLE IF NOT EXISTS staff (
    id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    department VARCHAR(100),
    job_type VARCHAR(100),
    img_url VARCHAR(255),
    password_hash VARCHAR(255) NOT NULL
);

-- Create Table for Student
CREATE TABLE IF NOT EXISTS student (
                                       id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    department VARCHAR(100),
    major VARCHAR(100),
    gpa VARCHAR(10)
    );

MERGE INTO staff (id, first_name, last_name, email, department, job_type, img_url, password_hash)
    KEY(id)
    VALUES (
    'STFADMIN01',
    'Demo',
    'Admin',
    'admin@school.edu',
    'Administration',
    'Administrator',
    NULL,
    '$2a$12$Ao24gngCgv7EINhIWE.v6.NeedPE4wurXHCKKZZn4Aq1TPc/npGVm'
    );

-- Insert 50 Example Students
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1001', 'Elizabeth', 'Lopez', 'Psychology', 'Cognitive Science', '2.53');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1002', 'Robert', 'Smith', 'History', 'American History', '3.42');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1003', 'Elizabeth', 'Williams', 'Mathematics', 'Actuarial Science', '3.81');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1004', 'Jennifer', 'Smith', 'Psychology', 'Clinical Psychology', '3.74');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1005', 'Charles', 'Garcia', 'Psychology', 'Clinical Psychology', '3.88');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1006', 'John', 'Williams', 'Mathematics', 'Applied Mathematics', '3.09');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1007', 'Richard', 'Williams', 'Computer Science', 'Software Engineering', '3.29');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1008', 'John', 'Thomas', 'Psychology', 'Cognitive Science', '3.72');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1009', 'Charles', 'Jackson', 'Mathematics', 'Statistics', '2.74');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1010', 'Michael', 'Taylor', 'Biology', 'Biochemistry', '3.08');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1011', 'William', 'Taylor', 'Biology', 'Genetics', '2.04');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1012', 'Charles', 'Garcia', 'English', 'Comparative Literature', '2.87');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1013', 'Patricia', 'Martin', 'English', 'Linguistics', '2.51');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1014', 'Thomas', 'Hernandez', 'English', 'Linguistics', '3.38');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1015', 'Linda', 'Jackson', 'Biology', 'Marine Biology', '3.26');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1016', 'Mary', 'Moore', 'Mathematics', 'Applied Mathematics', '2.22');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1017', 'David', 'Gonzalez', 'English', 'Creative Writing', '2.12');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1018', 'Richard', 'Jones', 'Computer Science', 'Software Engineering', '3.08');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1019', 'Sarah', 'Martinez', 'Psychology', 'Clinical Psychology', '2.76');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1020', 'Patricia', 'Thomas', 'Computer Science', 'Software Engineering', '2.68');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1021', 'Barbara', 'Davis', 'Psychology', 'Developmental Psychology', '3.57');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1022', 'Richard', 'Taylor', 'Mathematics', 'Statistics', '3.85');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1023', 'Charles', 'Rodriguez', 'Biology', 'Ecology', '3.75');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1024', 'Barbara', 'Thomas', 'English', 'Creative Writing', '2.96');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1025', 'Charles', 'Jackson', 'Mathematics', 'Statistics', '2.25');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1026', 'William', 'Garcia', 'English', 'Linguistics', '3.64');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1027', 'Sarah', 'Hernandez', 'Biology', 'Marine Biology', '3.47');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1028', 'William', 'Martinez', 'History', 'Art History', '3.72');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1029', 'Mary', 'Taylor', 'Computer Science', 'Game Development', '3.57');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1030', 'Jennifer', 'Brown', 'Psychology', 'Clinical Psychology', '3.59');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1031', 'Charles', 'Jackson', 'English', 'Linguistics', '3.37');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1032', 'Karen', 'Thomas', 'History', 'Art History', '3.51');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1033', 'Jessica', 'Miller', 'Biology', 'Genetics', '2.26');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1034', 'Sarah', 'Lopez', 'English', 'Creative Writing', '2.6');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1035', 'Barbara', 'Lopez', 'Psychology', 'Developmental Psychology', '2.34');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1036', 'Jessica', 'Taylor', 'Computer Science', 'Cyber Security', '3.08');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1037', 'Thomas', 'Williams', 'History', 'American History', '3.3');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1038', 'Joseph', 'Martin', 'Mathematics', 'Statistics', '2.74');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1039', 'Elizabeth', 'Lopez', 'Psychology', 'Cognitive Science', '3.29');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1040', 'Richard', 'Brown', 'Mathematics', 'Actuarial Science', '2.81');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1041', 'David', 'Jones', 'English', 'Comparative Literature', '2.37');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1042', 'Thomas', 'Martinez', 'English', 'Comparative Literature', '3.61');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1043', 'Susan', 'Davis', 'History', 'Ancient/Medieval Studies', '2.51');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1044', 'Richard', 'Taylor', 'History', 'Ancient/Medieval Studies', '3.72');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1045', 'Thomas', 'Lopez', 'Psychology', 'Developmental Psychology', '2.44');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1046', 'Susan', 'Thomas', 'English', 'Linguistics', '3.83');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1047', 'Charles', 'Martin', 'History', 'Art History', '2.22');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1048', 'Michael', 'Davis', 'Psychology', 'Developmental Psychology', '2.12');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1049', 'Sarah', 'Thomas', 'Mathematics', 'Actuarial Science', '2.48');
MERGE INTO student (id, first_name, last_name, department, major, gpa) KEY(id) VALUES('STU1050', 'Jessica', 'Hernandez', 'Biology', 'Biochemistry', '2.66');
