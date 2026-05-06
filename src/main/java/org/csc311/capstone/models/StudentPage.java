package org.csc311.capstone.models;

import java.util.List;

public class StudentPage {
    private final List<Student> students;
    private final int page;
    private final int pageSize;
    private final long totalRecords;

    public StudentPage(List<Student> students, int page, int pageSize, long totalRecords) {
        this.students = students;
        this.page = page;
        this.pageSize = pageSize;
        this.totalRecords = totalRecords;
    }

    public List<Student> getStudents() {
        return students;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public int getTotalPages() {
        if (totalRecords == 0) {
            return 1;
        }

        return (int) Math.ceil((double) totalRecords / pageSize);
    }
}