package cc.roja.sql.types;

final class MySQLTypeMap extends TypeMapImpl {
  public String getAsGraphQLTypeString(int sqlType) {
    return sqlTypeToGraphQLTypeString(sqlType);
  }
}
