package com.plantssoil.common.persistence.rdbms;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.plantssoil.common.config.ConfigFactory;
import com.plantssoil.common.config.LettuceConfiguration;
import com.plantssoil.common.persistence.IInitializer;
import com.plantssoil.common.test.TempDirectoryUtility;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatasourceInitializerTest {
    private TempDirectoryUtility util = new TempDirectoryUtility();

    @Before
    public void setUp() throws Exception {
        util.createSubDirectory("conf");
        util.createSubDirectory("data");
        new File(util.getSubDirectory("data") + "/changelogs").mkdirs();

        // setup initial context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.eclipse.jetty.jndi.InitialContextFactory");
        InitialContext ic = new InitialContext();

        // Construct BasicDataSource
        int r = ThreadLocalRandom.current().nextInt(100000);
        Class.forName(org.h2.Driver.class.getName());
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName(org.h2.Driver.class.getName());
        bds.setUrl("jdbc:h2:mem:testJdbc" + r + ";DB_CLOSE_DELAY=-1");
        bds.setUsername("sa");
        bds.setPassword("sa");
        ic.rebind("datasource-lettuce" + r, bds);

        Properties p = new Properties();
        p.setProperty(LettuceConfiguration.RDBMS_INIT_DDL_CONFIGURABLE, DatasourceInitializer.class.getName());
        p.setProperty(LettuceConfiguration.RDBMS_DATASOURCE, "datasource-lettuce" + r);

        try (FileOutputStream out = new FileOutputStream(util.getSubDirectory("conf") + "/lettuce.properties")) {
            p.store(out, "## All configurations for lettuce");
        }
        System.setProperty("lettuce.config.dir", util.getSubDirectory("conf"));
        System.setProperty("lettuce.data.dir", util.getSubDirectory("data"));
        ConfigFactory.reload();
    }

    @After
    public void tearDown() throws Exception {
        util.removeTempDirectory();
    }

    private void version1() {
        StringBuilder sb = endDatabaseChangeLog(initTable(new StringBuilder()));
        createFile(util.getSubDirectory("data") + "/lettuce-master.xml", sb);
        createInitTableFile();
        IInitializer.createDefaultInitializer().initialize();
        try (Connection conn = ((DatasourceInitializer) IInitializer.createDefaultInitializer()).getConnection()) {
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM client_info");
            stmt.executeQuery();
            assertTrue(true);
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void version2() {
        version1();
        StringBuilder sb = endDatabaseChangeLog(addTable(initTable(new StringBuilder())));
        createFile(util.getSubDirectory("data") + "/lettuce-master.xml", sb);
        this.createInitTableFile();
        this.createAddTableFile();
        IInitializer.createDefaultInitializer().initialize();

        try (Connection conn = ((DatasourceInitializer) IInitializer.createDefaultInitializer()).getConnection()) {
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM client_address");
            stmt.executeQuery();
            assertTrue(true);
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void version3() {
        version2();
        StringBuilder sb = endDatabaseChangeLog(addColumn(addTable(initTable(new StringBuilder()))));
        createFile(util.getSubDirectory("data") + "/lettuce-master.xml", sb);
        this.createInitTableFile();
        this.createAddTableFile();
        this.createAddColumnFile();
        IInitializer.createDefaultInitializer().initialize();

        try (Connection conn = ((DatasourceInitializer) IInitializer.createDefaultInitializer()).getConnection()) {
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT street, line1, line2, line3 FROM client_address");
            stmt.executeQuery();
            assertTrue(true);
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testVersion1() {
        version1();
    }

    @Test
    public void testVersion2() {
        version2();
    }

    @Test
    public void testVersion3() {
        version3();
    }

    private void createFile(String fileName, StringBuilder content) {
        try (FileOutputStream fos = new FileOutputStream(fileName); BufferedOutputStream s = new BufferedOutputStream(fos)) {
            s.write(content.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private StringBuilder initTable(StringBuilder sb) {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
                + "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"\r\n"
                + "  xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\"\r\n" + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
                + "  xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd\r\n"
                + "                      http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.4.xsd\">\r\n"
                + "\r\n" + "  <!-- A few DBs don't accept JSON data type still -->\r\n" + "  <property name=\"lettuce_json\" value=\"json\" />\r\n"
                + "  <property name=\"lettuce_varchar\" value=\"150\" dbms=\"mysql,mariadb\" />\r\n"
                + "  <property name=\"lettuce_varchar\" value=\"255\" dbms=\"!mysql,!mariadb\" />\r\n"
                + "  <property name=\"clob.type\" value=\"longtext\" dbms=\"mysql\" />\r\n"
                + "  <property name=\"blob.type\" value=\"oid\" dbms=\"postgresql\" />\r\n"
                + "  <property name=\"blob.type\" value=\"longblob\" dbms=\"!postgresql\" />\r\n"
                + "  <property name=\"timestamp.type\" value=\"datetime\" dbms=\"mysql\" />\r\n"
                + "  <include file=\"changelogs/20241022-init-table.xml\" relativeToChangelogFile=\"true\" />\r\n");
        return sb;
    }

    private StringBuilder addTable(StringBuilder sb) {
        sb.append("  <include file=\"changelogs/20241024-add-table.xml\" relativeToChangelogFile=\"true\" />\r\n");
        return sb;
    }

    private StringBuilder addColumn(StringBuilder sb) {
        sb.append("  <include file=\"changelogs/20241024-add-column.xml\" relativeToChangelogFile=\"true\" />\r\n");
        return sb;
    }

    private StringBuilder endDatabaseChangeLog(StringBuilder sb) {
        sb.append("</databaseChangeLog>");
        return sb;
    }

    private void createInitTableFile() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
                + "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"\r\n"
                + "    xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\"\r\n" + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
                + "    xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd\r\n"
                + "                        http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.3.xsd\">\r\n"
                + "    <changeSet author=\"lettuce-danialdy\" id=\"20241022001-1\">\r\n" + "        <createTable tableName=\"client_version\">\r\n"
                + "            <column name=\"clientversionid\" type=\"VARCHAR(64)\">\r\n"
                + "                <constraints nullable=\"false\" primaryKey=\"true\" primaryKeyName=\"pk_client_version\"/>\r\n" + "            </column>\r\n"
                + "            <column name=\"createdby\" type=\"VARCHAR(255)\">\r\n" + "                <constraints nullable=\"false\" />\r\n"
                + "            </column>\r\n" + "            <column name=\"createdon\" type=\"TIMESTAMP\">\r\n"
                + "                <constraints nullable=\"false\" />\r\n" + "            </column>\r\n"
                + "            <column name=\"modifiedby\" type=\"VARCHAR(255)\">\r\n" + "                <constraints nullable=\"false\" />\r\n"
                + "            </column>\r\n" + "            <column name=\"modifiedon\" type=\"TIMESTAMP\">\r\n"
                + "                <constraints nullable=\"false\" />\r\n" + "            </column>\r\n"
                + "            <column name=\"publishedon\" type=\"TIMESTAMP\" />\r\n" + "            <column name=\"retiredon\" type=\"TIMESTAMP\" />\r\n"
                + "            <column name=\"status\" type=\"VARCHAR(255)\">\r\n" + "                <constraints nullable=\"false\" />\r\n"
                + "            </column>\r\n" + "            <column name=\"version\" type=\"VARCHAR(${lettuce_varchar})\">\r\n"
                + "                <constraints nullable=\"false\" />\r\n" + "            </column>\r\n"
                + "            <column name=\"clientid\" type=\"VARCHAR(${lettuce_varchar})\" />\r\n"
                + "            <column name=\"clientorgid\" type=\"VARCHAR(${lettuce_varchar})\" />\r\n"
                + "            <column name=\"apikey\" type=\"VARCHAR(255)\">\r\n" + "                <constraints nullable=\"false\" />\r\n"
                + "            </column>\r\n" + "        </createTable>\r\n" + "    </changeSet>\r\n"
                + "    <changeSet author=\"lettuce-danialdy\" id=\"20241022001-2\">\r\n" + "        <createTable tableName=\"client_info\">\r\n"
                + "            <column name=\"clientid\" type=\"VARCHAR(64)\">\r\n"
                + "                <constraints nullable=\"false\" primaryKey=\"true\" primaryKeyName=\"pk_client_info\" />\r\n" + "            </column>\r\n"
                + "            <column name=\"createdby\" type=\"VARCHAR(255)\">\r\n" + "                <constraints nullable=\"false\" />\r\n"
                + "            </column>\r\n" + "            <column name=\"createdon\" type=\"TIMESTAMP\">\r\n"
                + "                <constraints nullable=\"false\" />\r\n" + "            </column>\r\n"
                + "            <column name=\"description\" type=\"VARCHAR(512)\" />\r\n"
                + "            <column name=\"name\" type=\"VARCHAR(${lettuce_varchar})\">\r\n" + "                <constraints nullable=\"false\" />\r\n"
                + "            </column>\r\n" + "            <column name=\"organizationid\" type=\"VARCHAR(${lettuce_varchar})\">\r\n"
                + "                <constraints nullable=\"false\" />\r\n" + "            </column>\r\n" + "        </createTable>\r\n" + "    </changeSet>");
        sb = endDatabaseChangeLog(sb);
        createFile(util.getSubDirectory("data") + "/changelogs/20241022-init-table.xml", sb);
    }

    private void createAddTableFile() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
                + "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"\r\n"
                + "    xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\"\r\n" + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
                + "    xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd\r\n"
                + "                        http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.3.xsd\">\r\n"
                + "    <changeSet author=\"lettuce-danialdy\" id=\"20241024666-01\">\r\n" + "        <createTable tableName=\"client_address\">\r\n"
                + "            <column name=\"addressid\" type=\"VARCHAR(64)\">\r\n"
                + "                <constraints nullable=\"false\" primaryKey=\"true\" primaryKeyName=\"pk_client_address\" />\r\n"
                + "            </column>\r\n" + "            <column name=\"city\" type=\"VARCHAR(255)\">\r\n"
                + "                <constraints nullable=\"false\" />\r\n" + "            </column>\r\n"
                + "            <column name=\"province\" type=\"VARCHAR(255)\">\r\n" + "                <constraints nullable=\"false\" />\r\n"
                + "            </column>\r\n" + "        </createTable>\r\n" + "    </changeSet>\r\n" + "</databaseChangeLog>");
        createFile(util.getSubDirectory("data") + "/changelogs/20241024-add-table.xml", sb);
    }

    private void createAddColumnFile() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
                + "<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"\r\n"
                + "    xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\"\r\n" + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
                + "    xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd\r\n"
                + "                        http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.3.xsd\">\r\n"
                + "    <changeSet author=\"lettuce-danialdy\" id=\"add-addressdetail\">\r\n" + "        <addColumn tableName=\"client_address\">\r\n"
                + "            <column name=\"street\" type=\"VARCHAR(255)\" />\r\n" + "            <column name=\"line1\" type=\"VARCHAR(255)\" />\r\n"
                + "            <column name=\"line2\" type=\"VARCHAR(255)\" />\r\n" + "            <column name=\"line3\" type=\"VARCHAR(255)\" />\r\n"
                + "        </addColumn>\r\n" + "    </changeSet>\r\n" + "</databaseChangeLog>");
        createFile(util.getSubDirectory("data") + "/changelogs/20241024-add-column.xml", sb);
    }

}