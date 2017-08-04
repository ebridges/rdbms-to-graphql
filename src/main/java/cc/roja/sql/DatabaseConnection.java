package cc.roja.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import cc.roja.sql.types.TypeMap;
import cc.roja.sql.types.TypeMapFactory;

public class DatabaseConnection {
  public static Connection connect(String jdbcUrl, String driver, String username, String password) throws SQLException {
    try {
      Class.forName(driver).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }

    return DriverManager.getConnection(jdbcUrl, username, password);
  }

  public static TypeMap getTypeMap(Connection connection) throws SQLException {
    String databaseProduct = connection.getMetaData().getDatabaseProductName();
    return TypeMapFactory.getTypeMap(databaseProduct);
  }
}
