package cc.roja.sql.types;

final class PostgresTypeMap extends TypeMapImpl {
  PostgresTypeMap(int type) {
    super(type);
  }

  public String getAsGraphQLTypeString() {
    return sqlTypeToGraphQLTypeString(sqlType);
  }
}