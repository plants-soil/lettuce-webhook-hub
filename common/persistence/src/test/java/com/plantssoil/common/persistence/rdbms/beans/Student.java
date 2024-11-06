package com.plantssoil.common.persistence.rdbms.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "TEST_STUDENT")
public class Student {
    public enum Gender {
        Male, Female
    }

    @Id
    private String studentId;
    private String studentName;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Embedded
    private Address address;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "studentId")
    private List<Course> courses = new ArrayList<>();

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("studentId: ").append(this.getStudentId());
        sb.append(", studentName: ").append(this.getStudentName());
        sb.append(", gender: ").append(this.getGender());
        sb.append(", address: (").append(this.getAddress().getAddressNo()).append(", ").append(this.getAddress().getStreet()).append(", ")
                .append(this.getAddress().getCity()).append(", ").append(this.getAddress().getProvince()).append(")");
        sb.append(", creationTime: ").append(this.getCreationTime());
        sb.append(", courses: [");
        for (Course course : courses) {
            sb.append("(").append(course.toString()).append(")");
        }
        sb.append("]");
        return sb.toString();
    }

}
