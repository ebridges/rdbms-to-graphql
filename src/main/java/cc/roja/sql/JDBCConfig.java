package cc.roja.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCConfig implements AutoCloseable {
  private static final Logger LOGGER = LoggerFactory.getLogger(JDBCConfig.class);

  private final DatabaseMetaData databaseMetadata;

  public JDBCConfig(String jdbcUrl, String driver, String username, String password)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
    Class.forName(driver).newInstance();

    Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
    this.databaseMetadata = connection.getMetaData();
  }

  public List<String> listTables(String schema, String ... tables) {
    List<String> includedTables = Arrays.asList(tables);
    List<String> tableList = new ArrayList<>();
    try {
      try (ResultSet results = this.databaseMetadata.getTables(null, schema, "%", new String[] {"TABLE", "VIEW"})) {
        while (results.next()) {
          String tableName = results.getString("TABLE_NAME");
          LOGGER.debug("tableName: "+tableName);
          if(tables.length > 0) {
            if (includedTables.contains(tableName)) {
              tableList.add(tableName);
            }
          } else {
            tableList.add(tableName);
          }
        }
      }
    } catch (SQLException e) {
      throw new IllegalStateException("unable to list tables.", e);
    }
    return tableList;
  }

  @Override
  public void close() {
    if(this.databaseMetadata != null) {
      Connection c;
      try {
        c = this.databaseMetadata.getConnection();
        c.close();
      } catch (SQLException e) {
        LOGGER.error("Unable to close database connection.", e);
      }
    }
  }
}
