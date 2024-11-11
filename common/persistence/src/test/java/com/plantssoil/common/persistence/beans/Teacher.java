package com.plantssoil.common.persistence.beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TEST_TEACHER")
public class Teacher {
	@Id
	private String teacherId;
	private String teacherName;

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	@Override
	public String toString() {
		return "teacherId: " + this.teacherId + ", teacherName: " + this.teacherName;
	}

}
