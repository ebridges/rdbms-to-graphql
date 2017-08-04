package cc.roja.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
  public static Connection connect(String jdbcUrl, String driver, String username, String password) throws SQLException {
    try {
      Class.forName(driver).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    return DriverManager.getConnection(jdbcUrl, username, password);
  }
}
