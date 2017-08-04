package cc.roja.sql.types;

final class PostgresTypeMap extends TypeMapImpl {
  public String getAsGraphQLTypeString(int sqlType) {
    return sqlTypeToGraphQLTypeString(sqlType);
  }
}