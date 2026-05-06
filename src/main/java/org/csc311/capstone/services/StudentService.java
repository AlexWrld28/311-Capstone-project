package org.csc311.capstone.services;

import org.csc311.capstone.db.AuditLogRepository;
import org.csc311.capstone.db.StudentGradeRepository;
import org.csc311.capstone.db.StudentRepository;
import org.csc311.capstone.models.PaginatedResult;
import org.csc311.capstone.models.Staff;
import org.csc311.capstone.models.Student;
import org.csc311.capstone.models.StudentGrade;
import org.csc311.capstone.models.StudentGradeStats;
import org.csc311.capstone.models.StudentSearchCriteria;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    public PaginatedResult<Student> findPage(StudentSearchCriteria criteria) throws SQLException {
        return StudentRepository.findPage(criteria);
    }

    public List<Student> findForExport(StudentSearchCriteria criteria) throws SQLException {
        return StudentRepository.findForExport(criteria);
    }

    public List<StudentGrade> findGradesForStudent(String studentId) throws SQLException {
        return StudentGradeRepository.findByStudentId(studentId);
    }

    public StudentGradeStats getGradeStatsForStudent(String studentId) throws SQLException {
        return StudentGradeRepository.getStatsForStudent(studentId);
    }

    public void addStudent(Staff actor, Student student) throws SQLException {
        requireAdmin(actor);

        StudentRepository.insert(student);

        AuditLogRepository.log(
                actor,
                "ADD_STUDENT",
                student.getID(),
                "Added student " + student.getFirstName() + " " + student.getLastName() + "."
        );
    }

    public void updateStudent(Staff actor, Student student) throws SQLException {
        requireAdmin(actor);

        StudentRepository.update(student);

        AuditLogRepository.log(
                actor,
                "UPDATE_STUDENT",
                student.getID(),
                "Updated student " + student.getFirstName() + " " + student.getLastName() + "."
        );
    }

    public void deleteStudent(Staff actor, Student student) throws SQLException {
        requireAdmin(actor);

        StudentRepository.deleteById(student.getID());

        AuditLogRepository.log(
                actor,
                "DELETE_STUDENT",
                student.getID(),
                "Deleted student " + student.getFirstName() + " " + student.getLastName() + "."
        );
    }

    public void addGrade(Staff actor, StudentGrade grade) throws SQLException {
        requireAdmin(actor);

        StudentGradeRepository.insert(grade);

        AuditLogRepository.log(
                actor,
                "ADD_STUDENT_GRADE",
                grade.getStudentId(),
                "Added grade for " + grade.getClassCode() + " (" + grade.getClassName() + ")."
        );
    }

    public void updateGrade(Staff actor, StudentGrade grade) throws SQLException {
        requireAdmin(actor);

        StudentGradeRepository.update(grade);

        AuditLogRepository.log(
                actor,
                "UPDATE_STUDENT_GRADE",
                grade.getStudentId(),
                "Updated grade for " + grade.getClassCode() + " (" + grade.getClassName() + ")."
        );
    }

    public void deleteGrade(Staff actor, StudentGrade grade) throws SQLException {
        requireAdmin(actor);

        StudentGradeRepository.deleteById(grade.getId());

        AuditLogRepository.log(
                actor,
                "DELETE_STUDENT_GRADE",
                grade.getStudentId(),
                "Deleted grade for " + grade.getClassCode() + " (" + grade.getClassName() + ")."
        );
    }

    private void requireAdmin(Staff actor) {
        if (actor == null || !isAdmin(actor)) {
            throw new SecurityException("Only administrators can modify student records.");
        }
    }

    private boolean isAdmin(Staff staff) {
        return staff.getJobType() != null
                && (staff.getJobType().equalsIgnoreCase("Administrator")
                || staff.getJobType().equalsIgnoreCase("Admin"));
    }
}
