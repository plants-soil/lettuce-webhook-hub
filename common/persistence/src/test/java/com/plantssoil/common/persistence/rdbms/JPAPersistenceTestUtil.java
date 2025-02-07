package com.plantssoil.common.persistence.rdbms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.common.persistence.beans.Address;
import com.plantssoil.common.persistence.beans.Course;
import com.plantssoil.common.persistence.beans.Student;
import com.plantssoil.common.persistence.beans.Teacher;

public class JPAPersistenceTestUtil {
    private Teacher newTeacherEntity() {
        Teacher teacher = new Teacher();
        teacher.setTeacherId(EntityUtils.getInstance().createUniqueObjectId());
        teacher.setTeacherName("Teacher " + ThreadLocalRandom.current().nextInt());
        return teacher;
    }

    private Course newCourseEntity() {
        Course course = new Course();
        course.setCourseId(EntityUtils.getInstance().createUniqueObjectId());
        course.setCourseName("Course" + ThreadLocalRandom.current().nextInt());
        return course;
    }

    private Student newStudentEntity(Student.Gender gender) {
        Student student = new Student();
        student.setStudentId(EntityUtils.getInstance().createUniqueObjectId());
        student.setStudentName("Student" + ThreadLocalRandom.current().nextInt());
        student.setGender(gender);
        student.setAddress(new Address("Address No " + ThreadLocalRandom.current().nextInt(), "Street " + ThreadLocalRandom.current().nextInt(),
                "City " + ThreadLocalRandom.current().nextInt(), "Province " + ThreadLocalRandom.current().nextInt()));
        student.setCreationTime(new Date());
        for (int i = 0; i < 3; i++) {
            student.getCourses().add(newCourseEntity());
        }
        return student;
    }

    protected void testCreate(IPersistenceFactory factory) {
        List<String> successful = new ArrayList<String>();
        for (int i = 0; i < 25; i++) {
            Teacher teacher = newTeacherEntity();
            try (IPersistence persists = factory.create()) {
                persists.create(teacher);
                successful.add(teacher.getTeacherId() + ", " + teacher.getTeacherName() + " created.");
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
        assertTrue(successful.size() == 25);
    }

    protected void testCreateList(IPersistenceFactory factory) {
        List<Student> students = new ArrayList<Student>();
        for (int i = 0; i < 25; i++) {
            students.add(newStudentEntity(Student.Gender.Male));
        }
        for (int i = 0; i < 25; i++) {
            students.add(newStudentEntity(Student.Gender.Female));
        }

        try (IPersistence persists = factory.create()) {
            persists.create(students);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    protected void testUpdate(IPersistenceFactory factory) {
        List<Teacher> ts = null;
        try (IPersistence persists = factory.create()) {
            IEntityQuery<Teacher> q = persists.createQuery(Teacher.class).firstResult(0).maxResults(5);
            ts = q.resultList().get();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        List<String> futures = new ArrayList<>();
        int i = 0;
        for (Teacher t : ts) {
            t.setTeacherName("Teacher" + i);
            try (IPersistence p = factory.create()) {
                Teacher updated = p.update(t);
                futures.add("Teacher name changed to: " + updated.getTeacherName());
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
            i++;
        }
        assertTrue(futures.size() == i);
    }

    @SuppressWarnings("unchecked")
    protected void testUpdateList(IPersistenceFactory factory) {
        List<Student> ts = null;
        try (IPersistence persists = factory.create()) {
            IEntityQuery<Student> q = persists.createQuery(Student.class).firstResult(0).maxResults(5);
            ts = q.resultList().get();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        int i = 0;
        for (Student t : ts) {
            t.setStudentName("Student" + i);
            i++;
        }
        List<Student> updated = null;
        try (IPersistence p = factory.create()) {
            updated = (List<Student>) p.update(ts);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(updated.size() == ts.size());
    }

    protected void testRemove(IPersistenceFactory factory) {
        List<Teacher> ts = null;
        try (IPersistence persists = factory.create()) {
            IEntityQuery<Teacher> q = persists.createQuery(Teacher.class).firstResult(0).maxResults(5);
            ts = q.resultList().get();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        List<String> deleted = new ArrayList<>();
        for (Teacher t : ts) {
            try (IPersistence p = factory.create()) {
                p.remove(t);
                deleted.add(t.getTeacherId() + ", " + t.getTeacherName() + " removed.");
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
        assertTrue(deleted.size() == ts.size());
    }

    protected void testRemoveList(IPersistenceFactory factory) {
        List<Student> ts = null;
        try (IPersistence persists = factory.create()) {
            IEntityQuery<Student> q = persists.createQuery(Student.class).firstResult(0).maxResults(5);
            ts = q.resultList().get();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        try (IPersistence p = factory.create()) {
            p.remove(ts);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(true);
    }

    protected void testEntityQuery(IPersistenceFactory factory) {
        try (IPersistence persists = factory.create()) {
            IEntityQuery<Student> query = persists.createQuery(Student.class);
            CompletableFuture<List<Student>> students = query.filter("studentName", IEntityQuery.FilterOperator.like, "Student-1%")
                    .filter("gender", IEntityQuery.FilterOperator.equals, Student.Gender.Female).firstResult(0).maxResults(5).resultList();
            for (Student s : students.get()) {
                System.out.println(s);
            }
            List<Student.Gender> g = new ArrayList<>();
            g.add(Student.Gender.Female);
            g.add(Student.Gender.Male);
            query = persists.createQuery(Student.class);
            students = query.filter("studentName", IEntityQuery.FilterOperator.like, "Student-1%").filter("gender", IEntityQuery.FilterOperator.in, g)
                    .firstResult(0).maxResults(5).resultList();
            for (Student s : students.get()) {
                System.out.println(s);
            }
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    protected void testDistinctQuery(IPersistenceFactory factory) {
        try (IPersistence persists = factory.create()) {
            IEntityQuery<Student> query = persists.createQuery(Student.class);
            query.filter("studentName", IEntityQuery.FilterOperator.like, "Student-1%")
                    .filter("gender", IEntityQuery.FilterOperator.equals, Student.Gender.Female).firstResult(0).maxResults(5);
            List<Student.Gender> gender = query.distinct(Student.Gender.class, "gender");
            assertTrue(gender.size() == 1);
            assertEquals(gender.get(0), Student.Gender.Female);
            System.out.println(gender.get(0));
            query = persists.createQuery(Student.class);
            List<Student.Gender> g = new ArrayList<>();
            g.add(Student.Gender.Female);
            g.add(Student.Gender.Male);
            query.filter("studentName", IEntityQuery.FilterOperator.like, "Student-1%").filter("gender", IEntityQuery.FilterOperator.in, g).firstResult(0)
                    .maxResults(5);
            gender = query.distinct(Student.Gender.class, "gender");
            assertTrue(gender.size() == 2);
            System.out.println(gender.get(0));
            System.out.println(gender.get(1));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
