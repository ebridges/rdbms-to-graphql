package cc.roja.sql.types;

final class MySQLTypeMap extends TypeMapImpl {
  MySQLTypeMap(int type) {
    super(type);
  }

  public String getAsGraphQLTypeString() {
    return sqlTypeToGraphQLTypeString(sqlType);
  }
}