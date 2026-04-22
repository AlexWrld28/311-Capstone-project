-- Create Table for Student
CREATE TABLE IF NOT EXISTS student (
                                       id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    department VARCHAR(100),
    major VARCHAR(100),
    gpa VARCHAR(10)
    );

-- Insert 50 Example Students
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1001', 'Elizabeth', 'Lopez', 'Psychology', 'Cognitive Science', '2.53');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1002', 'Robert', 'Smith', 'History', 'American History', '3.42');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1003', 'Elizabeth', 'Williams', 'Mathematics', 'Actuarial Science', '3.81');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1004', 'Jennifer', 'Smith', 'Psychology', 'Clinical Psychology', '3.74');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1005', 'Charles', 'Garcia', 'Psychology', 'Clinical Psychology', '3.88');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1006', 'John', 'Williams', 'Mathematics', 'Applied Mathematics', '3.09');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1007', 'Richard', 'Williams', 'Computer Science', 'Software Engineering', '3.29');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1008', 'John', 'Thomas', 'Psychology', 'Cognitive Science', '3.72');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1009', 'Charles', 'Jackson', 'Mathematics', 'Statistics', '2.74');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1010', 'Michael', 'Taylor', 'Biology', 'Biochemistry', '3.08');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1011', 'William', 'Taylor', 'Biology', 'Genetics', '2.04');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1012', 'Charles', 'Garcia', 'English', 'Comparative Literature', '2.87');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1013', 'Patricia', 'Martin', 'English', 'Linguistics', '2.51');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1014', 'Thomas', 'Hernandez', 'English', 'Linguistics', '3.38');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1015', 'Linda', 'Jackson', 'Biology', 'Marine Biology', '3.26');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1016', 'Mary', 'Moore', 'Mathematics', 'Applied Mathematics', '2.22');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1017', 'David', 'Gonzalez', 'English', 'Creative Writing', '2.12');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1018', 'Richard', 'Jones', 'Computer Science', 'Software Engineering', '3.08');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1019', 'Sarah', 'Martinez', 'Psychology', 'Clinical Psychology', '2.76');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1020', 'Patricia', 'Thomas', 'Computer Science', 'Software Engineering', '2.68');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1021', 'Barbara', 'Davis', 'Psychology', 'Developmental Psychology', '3.57');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1022', 'Richard', 'Taylor', 'Mathematics', 'Statistics', '3.85');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1023', 'Charles', 'Rodriguez', 'Biology', 'Ecology', '3.75');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1024', 'Barbara', 'Thomas', 'English', 'Creative Writing', '2.96');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1025', 'Charles', 'Jackson', 'Mathematics', 'Statistics', '2.25');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1026', 'William', 'Garcia', 'English', 'Linguistics', '3.64');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1027', 'Sarah', 'Hernandez', 'Biology', 'Marine Biology', '3.47');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1028', 'William', 'Martinez', 'History', 'Art History', '3.72');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1029', 'Mary', 'Taylor', 'Computer Science', 'Game Development', '3.57');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1030', 'Jennifer', 'Brown', 'Psychology', 'Clinical Psychology', '3.59');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1031', 'Charles', 'Jackson', 'English', 'Linguistics', '3.37');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1032', 'Karen', 'Thomas', 'History', 'Art History', '3.51');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1033', 'Jessica', 'Miller', 'Biology', 'Genetics', '2.26');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1034', 'Sarah', 'Lopez', 'English', 'Creative Writing', '2.6');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1035', 'Barbara', 'Lopez', 'Psychology', 'Developmental Psychology', '2.34');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1036', 'Jessica', 'Taylor', 'Computer Science', 'Cyber Security', '3.08');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1037', 'Thomas', 'Williams', 'History', 'American History', '3.3');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1038', 'Joseph', 'Martin', 'Mathematics', 'Statistics', '2.74');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1039', 'Elizabeth', 'Lopez', 'Psychology', 'Cognitive Science', '3.29');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1040', 'Richard', 'Brown', 'Mathematics', 'Actuarial Science', '2.81');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1041', 'David', 'Jones', 'English', 'Comparative Literature', '2.37');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1042', 'Thomas', 'Martinez', 'English', 'Comparative Literature', '3.61');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1043', 'Susan', 'Davis', 'History', 'Ancient/Medieval Studies', '2.51');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1044', 'Richard', 'Taylor', 'History', 'Ancient/Medieval Studies', '3.72');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1045', 'Thomas', 'Lopez', 'Psychology', 'Developmental Psychology', '2.44');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1046', 'Susan', 'Thomas', 'English', 'Linguistics', '3.83');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1047', 'Charles', 'Martin', 'History', 'Art History', '2.22');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1048', 'Michael', 'Davis', 'Psychology', 'Developmental Psychology', '2.12');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1049', 'Sarah', 'Thomas', 'Mathematics', 'Actuarial Science', '2.48');
INSERT INTO student (id, first_name, last_name, department, major, gpa) VALUES ('STU1050', 'Jessica', 'Hernandez', 'Biology', 'Biochemistry', '2.66');
