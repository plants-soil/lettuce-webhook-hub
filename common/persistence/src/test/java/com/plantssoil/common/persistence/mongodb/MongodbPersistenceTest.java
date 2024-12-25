package com.plantssoil.common.persistence.mongodb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.ConfigurableLoader;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.EntityUtils;
import com.plantssoil.common.persistence.IEntityQuery;
import com.plantssoil.common.persistence.IPersistence;
import com.plantssoil.common.persistence.IPersistenceFactory;
import com.plantssoil.common.persistence.beans.Address;
import com.plantssoil.common.persistence.beans.Course;
import com.plantssoil.common.persistence.beans.Student;
import com.plantssoil.common.persistence.beans.Teacher;
import com.plantssoil.common.test.TempDirectoryUtility;

public class MongodbPersistenceTest {
    private static TempDirectoryUtility util = new TempDirectoryUtility();

    public static void main(String[] args) throws Exception {
        MongodbPersistenceTest.setUpBeforeClass();
        MongodbPersistenceTest test = new MongodbPersistenceTest();
        test.testCreateQuery();
        MongodbPersistenceTest.tearDownAfterClass();
        System.out.println(EntityUtils.getInstance().createUniqueObjectId());
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Thread.sleep(1000);
        ConfigurableLoader.getInstance().removeSingleton(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE);

        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.PERSISTENCE_FACTORY_CONFIGURABLE, MongodbPersistenceFactory.class.getName());
        p.setProperty(LettuceConfiguration.PERSISTENCE_DATABASE_URL,
                "mongodb://lettuce:lettuce20241101@192.168.0.67:27017/?retryWrites=false&retryReads=false");

        try (FileOutputStream out = new FileOutputStream(util.getTempDir() + "/" + LettuceConfiguration.CONFIGURATION_FILE_NAME)) {
            p.store(out, "## No comments");
        }
        System.setProperty(LettuceConfiguration.CONF_DIRECTORY_PROPERTY_NAME, util.getTempDir());
        ConfigFactory.reload();

        IPersistenceFactory.getFactoryInstance();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        util.removeTempDirectory();
    }

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

    private Student newStudentEntity() {
        Student student = new Student();
        student.setStudentId(EntityUtils.getInstance().createUniqueObjectId());
        student.setStudentName("Student" + ThreadLocalRandom.current().nextInt());
        student.setGender(Student.Gender.Female);
        student.setAddress(new Address("Address No " + ThreadLocalRandom.current().nextInt(), "Street " + ThreadLocalRandom.current().nextInt(),
                "City " + ThreadLocalRandom.current().nextInt(), "Province " + ThreadLocalRandom.current().nextInt()));
        student.setCreationTime(new Date());
        for (int i = 0; i < 3; i++) {
            student.getCourses().add(newCourseEntity());
        }
        return student;
    }

    @Test
    public void testCreateObject() {
        List<String> successful = new ArrayList<String>();
        for (int i = 0; i < 25; i++) {
            Teacher teacher = newTeacherEntity();
            try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
                persists.create(teacher);
                successful.add(teacher.getTeacherId() + ", " + teacher.getTeacherName() + " created.");
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
        assertTrue(successful.size() == 25);
    }

    @Test
    public void testCreateListOfQ() {
        List<Student> students = new ArrayList<Student>();
        for (int i = 0; i < 25; i++) {
            students.add(newStudentEntity());
        }

        try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
            persists.create(students);
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateT() {
        List<Teacher> ts = null;
        try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
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
            try (IPersistence p = IPersistenceFactory.getFactoryInstance().create()) {
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
    @Test
    public void testUpdateListOfQ() {
        List<Student> ts = null;
        try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
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
        try (IPersistence p = IPersistenceFactory.getFactoryInstance().create()) {
            updated = (List<Student>) p.update(ts);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(updated.size() == ts.size());
    }

    @Test
    public void testRemoveObject() {
        List<Teacher> ts = null;
        try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
            IEntityQuery<Teacher> q = persists.createQuery(Teacher.class).firstResult(0).maxResults(5);
            ts = q.resultList().get();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        List<String> deleted = new ArrayList<>();
        for (Teacher t : ts) {
            try (IPersistence p = IPersistenceFactory.getFactoryInstance().create()) {
                p.remove(t);
                deleted.add(t.getTeacherId() + ", " + t.getTeacherName() + " removed.");
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
        assertTrue(deleted.size() == ts.size());
    }

    @Test
    public void testRemoveListOfQ() {
        List<Student> ts = null;
        try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
            IEntityQuery<Student> q = persists.createQuery(Student.class).firstResult(0).maxResults(5);
            ts = q.resultList().get();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        try (IPersistence p = IPersistenceFactory.getFactoryInstance().create()) {
            p.remove(ts);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        assertTrue(true);
    }

    @Test
    public void testCreateQuery() {
        try (IPersistence persists = IPersistenceFactory.getFactoryInstance().create()) {
            IEntityQuery<Student> query = persists.createQuery(Student.class);
            CompletableFuture<List<Student>> students = query.filter("studentName", IEntityQuery.FilterOperator.like, "Student-1.")
                    .filter("gender", IEntityQuery.FilterOperator.equals, Student.Gender.Female).firstResult(0).maxResults(5).resultList();
            for (Student s : students.get()) {
                System.out.println(s);
            }
            assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
