package cc.roja.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.roja.sql.model.Attribute;
import cc.roja.sql.model.Cardinality;
import cc.roja.sql.model.Entity;
import cc.roja.sql.model.Relation;
import cc.roja.sql.types.TypeMap;
import cc.roja.sql.types.TypeMapFactory;

@SuppressWarnings("WeakerAccess")
public class DatabaseAnalyzer implements AutoCloseable {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseAnalyzer.class);

  private final DatabaseMetaData databaseMetadata;

  private final String databaseType;
  private final String catalog;
  private final String schema;

  public DatabaseAnalyzer(String jdbcUrl, String driver, String username, String password, String schema)
      throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
    Class.forName(driver).newInstance();

    Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
    this.databaseMetadata = connection.getMetaData();
    this.schema = schema;
    this.catalog = connection.getCatalog();
    this.databaseType = this.databaseMetadata.getDatabaseProductName();
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

  public List<Entity> initializeEntities(String ... tables) throws SQLException {
    List<String> includedTables = Arrays.asList(tables);
    List<Entity> entities = new ArrayList<>();
    try (ResultSet results = this.databaseMetadata.getTables(this.catalog, this.schema, "%", new String[] {"TABLE", "VIEW"})) {
      while (results.next()) {
        String tableName = results.getString("TABLE_NAME");
        Entity entity = initializeEntity(tableName);
        if(entity != null) {
          LOGGER.debug(entity.toString());
          entities.add(entity);
        }
      }
    }

    initializeRelations(entities);

    return entities.stream()
        .filter(entity -> includedTables.contains(entity.getName()))
        .collect(Collectors.toList());
  }

  public Entity initializeEntity(String table) throws SQLException {
    Entity entity = new Entity(table);
    List<String> primaryKeys = new ArrayList<>();

    try(ResultSet results = this.databaseMetadata.getPrimaryKeys(catalog, schema, table)) {
      LOGGER.debug("Loading primary keys for table: "+table);
      while(results.next()) {
        String name = results.getString("COLUMN_NAME");
        primaryKeys.add(name);
      }
    }

    try(ResultSet results = this.databaseMetadata.getColumns(catalog, schema, table, "%")) {
      LOGGER.debug("Loading columns for table: "+table);
      while(results.next()) {
        Attribute attribute = initializeAttribute(results, primaryKeys);
        entity.addAttribute(attribute);
      }
    }

    return entity;
  }

  private Attribute initializeAttribute(ResultSet results, List<String> primaryKeys) throws SQLException {
    String name = results.getString("COLUMN_NAME");
    int position = results.getInt("ORDINAL_POSITION");
    int dataType = results.getInt("DATA_TYPE");
    TypeMap typeMap = TypeMapFactory.getTypeMap(databaseType, dataType);
    boolean isNullable = results.getInt("NULLABLE") == 1;
    boolean isPrimaryKey = primaryKeys.contains(name);

    return new Attribute(name, position, typeMap, isPrimaryKey, isNullable);
  }


  private void initializeRelations(List<Entity> entities) throws SQLException {
    Map<Entity, Map<Attribute, Relation>> relationMap = initializeRelationMap(entities);
    Map<String, Entity> entityMap = initializeEntityMap(entities);
    for(Entity e : relationMap.keySet()) {
      try(ResultSet results = this.databaseMetadata.getImportedKeys(catalog, schema, e.getName())) {
        LOGGER.debug("Loading imported keys for table: "+e.getName());
        while(results.next()) {
          String pktable = results.getString("PKTABLE_NAME");
          String pkColumn = results.getString("PKCOLUMN_NAME");
          String fkTable = results.getString("FKTABLE_NAME");
          String fkColumn = results.getString("FKCOLUMN_NAME");

          Entity pkEntity = entityMap.get(pktable);
          Attribute pkAttribute = getMatchingAttribute(pkEntity.getAttributes(), pkColumn);

          Entity fkEntity = entityMap.get(fkTable);
          Attribute fkAttribute = getMatchingAttribute(fkEntity.getAttributes(), fkColumn);

          LOGGER.debug(String.format("Relation: %s.%s (1) <- %s.%s (M)", pktable, pkColumn, fkTable, fkColumn));
          // set "one" side of relation
          for(Attribute a : pkEntity.getAttributes()) {
            if(a.getName().equals(pkColumn)) {
              Relation r = new Relation(fkEntity, fkAttribute, Cardinality.MANY);
              a.setForeignKey(r);
            }
          }

          // set "many" side of relation
          for(Attribute a : e.getAttributes()) {
            if(a.getName().equals(fkColumn)) {
              Relation r = new Relation(pkEntity, pkAttribute, Cardinality.ONE);
              a.setForeignKey(r);
            }
          }
        }
      }
    }
  }

  private Attribute getMatchingAttribute(Set<Attribute> attributes, String column) {
    for(Attribute a : attributes) {
      if(a.getName().equals(column)) {
        return a;
      }
    }
    throw new IllegalStateException("searching for unknown column: "+column);
  }

  private Map<String, Entity> initializeEntityMap(List<Entity> entities) {
    Map<String, Entity> entityMap = new HashMap<>();
    for(Entity e : entities) {
      entityMap.put(e.getName(), e);
    }
    return entityMap;
  }

  private Map<Entity, Map<Attribute, Relation>> initializeRelationMap(List<Entity> entities) {
    Map<Entity, Map<Attribute, Relation>> relationMap = new HashMap<>();
    for(Entity e : entities) {
      if(!relationMap.containsKey(e)) {
        relationMap.put(e, new HashMap<>());
      }
      for(Attribute a : e.getAttributes()) {
        relationMap.get(e).put(a, a.getForeignKey());
      }
    }
    return relationMap;
  }

  @SuppressWarnings("unused")
  private static void dumpResultSet(ResultSet results) throws SQLException {
    while(results.next()) {
      ResultSetMetaData rsmd = results.getMetaData();
      int cnt = rsmd.getColumnCount();
      for(int i=1; i<cnt; i++) {
        LOGGER.debug(String.format("%s (%s): %s", rsmd.getColumnName(i), rsmd.getColumnLabel(i), results.getObject(i)));
      }
    }
  }
}
