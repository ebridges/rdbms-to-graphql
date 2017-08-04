package cc.roja.sql.types;

public interface TypeMap {
  String getAsGraphQLTypeString(int sqlType);
}
