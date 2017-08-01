package cc.roja.sql.types;

public class TypeMapFactory {
  public static TypeMap getTypeMap(String db, int type) {
    String database = db.toLowerCase();
    switch (database) {
      case "postgresql":
        return new PostgresTypeMap(type);
      case "mysql":
        return new MySQLTypeMap(type);
      default:
        throw new RuntimeException("Unable to find type map for database: " + db);
    }
  }
}