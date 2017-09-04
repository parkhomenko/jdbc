package com.hillel.jdbc;

import java.io.IOException;
import java.sql.SQLException;

public class JdbcExample {

  public static void main(String[] args) throws SQLException, IOException {

    SchoolData schoolData = new SchoolData();
    //schoolData.printStudents();
    //schoolData.addStudent();

    //schoolData.transactionExample();
    //schoolData.batchExample();

    //schoolData.addInfoAboutStudent();
    //schoolData.readInfoAboutStudent();

    //schoolData.savePhoto();
    //schoolData.retrievePhoto();

    schoolData.retrievePhotoInBase64();
  }
}
