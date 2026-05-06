package org.csc311.capstone.db;

import org.csc311.capstone.models.DashboardStats;
import org.csc311.capstone.models.PaginatedResult;
import org.csc311.capstone.models.Student;
import org.csc311.capstone.models.StudentSearchCriteria;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class StudentRepository {

    public static PaginatedResult<Student> findPage(StudentSearchCriteria criteria) throws SQLException {
        List<Object> params = new ArrayList<>();
        String whereSql = buildWhereSql(criteria, params);
        String orderSql = buildOrderSql(criteria);

        String countSql = """
            SELECT COUNT(*)
            FROM students s
            LEFT JOIN departments d ON s.department_id = d.id
            LEFT JOIN majors m ON s.major_id = m.id
        """ + whereSql;

        int totalItems;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(countSql)) {

            applyParams(stmt, params);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                totalItems = rs.getInt(1);
            }
        }

        String pageSql = """
            SELECT s.id,
                   s.first_name,
                   s.last_name,
                   COALESCE(d.name, s.department) AS department,
                   COALESCE(m.name, s.major) AS major,
                   s.gpa
            FROM students s
            LEFT JOIN departments d ON s.department_id = d.id
            LEFT JOIN majors m ON s.major_id = m.id
        """ + whereSql + orderSql + " LIMIT ? OFFSET ?";

        List<Object> pageParams = new ArrayList<>(params);
        int page = Math.max(1, criteria.page());
        int pageSize = Math.max(1, criteria.pageSize());
        int offset = (page - 1) * pageSize;

        pageParams.add(pageSize);
        pageParams.add(offset);

        List<Student> students = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(pageSql)) {

            applyParams(stmt, pageParams);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapStudent(rs));
                }
            }
        }

        return new PaginatedResult<>(students, totalItems, page, pageSize);
    }

    public static List<Student> findForExport(StudentSearchCriteria criteria) throws SQLException {
        List<Object> params = new ArrayList<>();
        String whereSql = buildWhereSql(criteria, params);
        String orderSql = buildOrderSql(criteria);

        String sql = """
            SELECT s.id,
                   s.first_name,
                   s.last_name,
                   COALESCE(d.name, s.department) AS department,
                   COALESCE(m.name, s.major) AS major,
                   s.gpa
            FROM students s
            LEFT JOIN departments d ON s.department_id = d.id
            LEFT JOIN majors m ON s.major_id = m.id
        """ + whereSql + orderSql;

        List<Student> students = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            applyParams(stmt, params);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapStudent(rs));
                }
            }
        }

        return students;
    }

    public static List<Student> findAll() throws SQLException {
        return findForExport(new StudentSearchCriteria(
                "",
                "All Departments",
                "All Majors",
                null,
                null,
                "id",
                true,
                1,
                100000
        ));
    }

    public static void insert(Student student) throws SQLException {
        Integer departmentId = ReferenceDataRepository.findOrCreateDepartmentId(student.getDepartment());
        Integer majorId = ReferenceDataRepository.findOrCreateMajorId(student.getMajor());

        String sql = """
            INSERT INTO students (id, first_name, last_name, department, major, department_id, major_id, gpa)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getID());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getDepartment());
            stmt.setString(5, student.getMajor());

            if (departmentId == null) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setInt(6, departmentId);
            }

            if (majorId == null) {
                stmt.setNull(7, Types.INTEGER);
            } else {
                stmt.setInt(7, majorId);
            }

            stmt.setBigDecimal(8, new BigDecimal(student.getGpa()));

            stmt.executeUpdate();
        }
    }

    public static void update(Student student) throws SQLException {
        Integer departmentId = ReferenceDataRepository.findOrCreateDepartmentId(student.getDepartment());
        Integer majorId = ReferenceDataRepository.findOrCreateMajorId(student.getMajor());

        String sql = """
            UPDATE students
            SET first_name = ?,
                last_name = ?,
                department = ?,
                major = ?,
                department_id = ?,
                major_id = ?,
                gpa = ?
            WHERE id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getDepartment());
            stmt.setString(4, student.getMajor());

            if (departmentId == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, departmentId);
            }

            if (majorId == null) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setInt(6, majorId);
            }

            stmt.setBigDecimal(7, new BigDecimal(student.getGpa()));
            stmt.setString(8, student.getID());

            stmt.executeUpdate();
        }
    }

    public static void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    public static DashboardStats getDashboardStats() throws SQLException {
        int totalStudents = 0;
        double averageGpa = 0;
        double highestGpa = 0;
        double lowestGpa = 0;

        String summarySql = """
            SELECT COUNT(*) AS total,
                   COALESCE(AVG(gpa), 0) AS avg_gpa,
                   COALESCE(MAX(gpa), 0) AS max_gpa,
                   COALESCE(MIN(gpa), 0) AS min_gpa
            FROM students
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(summarySql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalStudents = rs.getInt("total");
                averageGpa = rs.getDouble("avg_gpa");
                highestGpa = rs.getDouble("max_gpa");
                lowestGpa = rs.getDouble("min_gpa");
            }
        }

        Map<String, Integer> byDepartment = new LinkedHashMap<>();

        String departmentSql = """
            SELECT COALESCE(d.name, s.department) AS department,
                   COUNT(*) AS total
            FROM students s
            LEFT JOIN departments d ON s.department_id = d.id
            GROUP BY COALESCE(d.name, s.department)
            ORDER BY department
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(departmentSql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                byDepartment.put(rs.getString("department"), rs.getInt("total"));
            }
        }

        return new DashboardStats(totalStudents, averageGpa, highestGpa, lowestGpa, byDepartment);
    }

    private static String buildWhereSql(StudentSearchCriteria criteria, List<Object> params) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1 ");

        if (criteria.search() != null && !criteria.search().trim().isEmpty()) {
            where.append("""
                AND (
                    LOWER(s.id) LIKE LOWER(?)
                    OR LOWER(s.first_name) LIKE LOWER(?)
                    OR LOWER(s.last_name) LIKE LOWER(?)
                    OR LOWER(COALESCE(d.name, s.department)) LIKE LOWER(?)
                    OR LOWER(COALESCE(m.name, s.major)) LIKE LOWER(?)
                )
            """);

            String search = "%" + criteria.search().trim() + "%";
            params.add(search);
            params.add(search);
            params.add(search);
            params.add(search);
            params.add(search);
        }

        if (criteria.department() != null
                && !criteria.department().startsWith("All")
                && !criteria.department().trim().isEmpty()) {
            where.append(" AND COALESCE(d.name, s.department) = ? ");
            params.add(criteria.department());
        }

        if (criteria.major() != null
                && !criteria.major().startsWith("All")
                && !criteria.major().trim().isEmpty()) {
            where.append(" AND COALESCE(m.name, s.major) = ? ");
            params.add(criteria.major());
        }

        if (criteria.minGpa() != null) {
            where.append(" AND s.gpa >= ? ");
            params.add(BigDecimal.valueOf(criteria.minGpa()));
        }

        if (criteria.maxGpa() != null) {
            where.append(" AND s.gpa <= ? ");
            params.add(BigDecimal.valueOf(criteria.maxGpa()));
        }

        return where.toString();
    }

    private static String buildOrderSql(StudentSearchCriteria criteria) {
        String sortColumn = switch (criteria.sortBy() == null ? "id" : criteria.sortBy()) {
            case "firstName" -> "s.first_name";
            case "lastName" -> "s.last_name";
            case "department" -> "COALESCE(d.name, s.department)";
            case "major" -> "COALESCE(m.name, s.major)";
            case "gpa" -> "s.gpa";
            default -> "s.id";
        };

        String direction = criteria.ascending() ? " ASC " : " DESC ";
        return " ORDER BY " + sortColumn + direction;
    }

    private static void applyParams(PreparedStatement stmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
    }

    private static Student mapStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("department"),
                rs.getString("major"),
                rs.getBigDecimal("gpa").toPlainString()
        );
    }
}