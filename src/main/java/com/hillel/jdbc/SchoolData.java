package com.hillel.jdbc;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;
import java.util.Base64;
import java.util.Properties;

import javax.sql.PooledConnection;

public class SchoolData {

  Connection connection;

  public SchoolData() throws SQLException, IOException {
    InputStream inputStream = getClass().getResourceAsStream("/db.properties");
    Properties properties = new Properties();
    properties.load(inputStream);

    /*
    connection = DriverManager
        .getConnection(properties.getProperty("url"),
            properties.getProperty("username"),
            properties.getProperty("password"));
            */

    MysqlConnectionPoolDataSource cp = new MysqlConnectionPoolDataSource();
    cp.setServerName("localhost");
    cp.setPortNumber(3306);
    cp.setUser("root");
    cp.setPassword("root");
    cp.setDatabaseName("school");

    PooledConnection pooledConnection = cp.getPooledConnection();

    connection = pooledConnection.getConnection();

    System.out.println("Connected");
  }

  public void printStudents() throws SQLException {
    String sql = "select * from students";

    Statement statement = connection.createStatement();
    ResultSet students = statement.executeQuery(sql);

    while (students.next()) {
      String firstname = students.getString("firstname");
      String lastname = students.getString("lastname");
      int age = students.getInt("age");
      System.out.println(firstname + " " + lastname + " " + age);
    }
  }

  public void addStudent() throws SQLException {
    String sql = "insert into students(firstname, lastname, age) values(?, ?, ?)";

    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, "Ivan");
    preparedStatement.setString(2, "Ivanov");
    preparedStatement.setInt(3, 16);

    preparedStatement.execute();
  }

  public void transactionExample() throws SQLException {
    String sql1 = "insert into students(firstname, lastname, age) values('John', 'Wick', 38)";
    String sql2 = "insert into students(firstname, lastname, age) values('Sam', 'Johnson', NULL)";

    connection.setAutoCommit(false);

    Statement statement = connection.createStatement();

    try {
      statement.executeUpdate(sql1);
      statement.executeUpdate(sql2);
      connection.commit();
    } catch (Exception e) {
      connection.rollback();
    }

    connection.setAutoCommit(true);
  }

  public void batchExample() throws SQLException {
    String sql = "insert into students(firstname, lastname, age) values(?, ?, ?)";

    PreparedStatement preparedStatement = connection.prepareStatement(sql);

    //connection.setAutoCommit(false);

    for (int i = 0; i < 10; i++) {
      preparedStatement.setString(1, "Ivan");
      preparedStatement.setString(2, "Ivanov");
      if (i != 5) {
        preparedStatement.setNull(3, Types.NUMERIC);
      }
      preparedStatement.addBatch();
    }

    preparedStatement.executeBatch();

    //connection.setAutoCommit(true);
  }

  public void addInfoAboutStudent() throws SQLException, FileNotFoundException {
    String sql = "update students set info = ? where id = 2";

    PreparedStatement preparedStatement = connection.prepareStatement(sql);

    File file = new File("e:/cv.txt");
    FileReader fileReader = new FileReader(file);

    preparedStatement.setCharacterStream(1, fileReader);

    preparedStatement.executeUpdate();

    preparedStatement.close();
  }

  public void readInfoAboutStudent() throws SQLException, IOException {
    String sql = "select info from students where id = 2";

    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);

    if (resultSet.next()) {
      Clob info = resultSet.getClob("info");
      Reader reader = info.getCharacterStream();
      BufferedReader bufferedReader = new BufferedReader(reader);

      //bufferedReader.lines().forEach(System.out::println);
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        System.out.println(line);
      }
    }

    resultSet.close();
    statement.close();
  }

  public void savePhoto() throws SQLException, FileNotFoundException {
    String sql = "update students set photo = ? where id = 2";

    PreparedStatement preparedStatement = connection.prepareStatement(sql);

    File file = new File("e:/monster.jpg");
    FileInputStream fileInputStream = new FileInputStream(file);

    preparedStatement.setBinaryStream(1, fileInputStream);

    preparedStatement.executeUpdate();

    preparedStatement.close();
  }

  public void retrievePhoto() throws SQLException, IOException {
    String sql = "select photo from students where id = 2";

    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);

    if (resultSet.next()) {
      Blob blob = resultSet.getBlob("photo");
      FileOutputStream fileOutputStream = new FileOutputStream("e:/monster_copy.jpg");
      fileOutputStream.write(blob.getBytes(1, (int)blob.length()));

      fileOutputStream.flush();
      fileOutputStream.close();
    }

    resultSet.close();
    statement.close();
  }

  public void retrievePhotoInBase64() throws SQLException, IOException {
    String sql = "select photo from students where id = 2";

    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);

    if (resultSet.next()) {
      Blob blob = resultSet.getBlob("photo");
      byte[] bytes = Base64.getEncoder().encode(blob.getBytes(1, (int) blob.length()));
      String encodedBytes = new String(bytes);
      System.out.println(encodedBytes);
    }

    resultSet.close();
    statement.close();
  }
}
