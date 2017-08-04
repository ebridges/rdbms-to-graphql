package cc.roja.sql.types;

public class TypeMapFactory {
  public static TypeMap getTypeMap(String db) {
    String database = db.toLowerCase();
    switch (database) {
      case "postgresql":
        return new PostgresTypeMap();
      case "mysql":
        return new MySQLTypeMap();
      default:
        throw new RuntimeException("Unable to find type map for database: " + db);
    }
  }
}