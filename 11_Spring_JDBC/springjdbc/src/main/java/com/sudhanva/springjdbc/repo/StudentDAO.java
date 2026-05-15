package com.sudhanva.springjdbc.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.sudhanva.springjdbc.model.Student;

@Repository
public class StudentDAO {

    private JdbcTemplate jdbc;

    public JdbcTemplate getJdbc() {
        return jdbc;
    }

    @Autowired
    public void setJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(Student s) {

        // Prepared Statement
        String sql = "insert into student (rollno,name,marks) values (?,?,?)";
        int rows = jdbc.update(sql, s.getRollNo(), s.getName(), s.getMarks());
        System.out.println(rows + " effected");
    }

    public List<Student> findAll() {

        String sql = "select * from student";

        // RowMapper for taking result set and giving all
        RowMapper<Student> rm = new RowMapper<>() {

            @Override
            public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
                Student student = new Student();

                student.setRollNo(rs.getInt("rollno"));
                student.setName(rs.getString("name"));
                student.setMarks(rs.getInt("marks"));

                return student;

            }

        };

        return jdbc.query(sql, (rs, rowNum) -> {
            Student student = new Student();

            student.setRollNo(rs.getInt("rollno"));
            student.setName(rs.getString("name"));
            student.setMarks(rs.getInt("marks"));

            return student;
        });
    }

}
