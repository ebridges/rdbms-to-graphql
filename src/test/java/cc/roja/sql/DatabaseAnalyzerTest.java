package cc.roja.sql;

import static cc.roja.sql.model.Cardinality.MANY;
import static cc.roja.sql.model.Cardinality.ONE;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import cc.roja.sql.model.Attribute;
import cc.roja.sql.model.Cardinality;
import cc.roja.sql.model.Entity;
import cc.roja.sql.model.Relation;
import cc.roja.sql.types.TypeMap;

public class DatabaseAnalyzerTest {
/*
To mock:
connection#getMetaData
connection#getCatalog
TypeMap


To test:

#initializeAttribute
  -- mock
  ResultSet:
          getString("COLUMN_NAME")
          getInt("ORDINAL_POSITION")
          getInt("DATA_TYPE")
          getInt("NULLABLE")
  List<String> primaryKeys with getString("COLUMN_NAME")
  typeMap.getAsGraphQLTypeString(dataType)



#initializeRelations
  -- mock
  Map<String, Entity>:
    2 entities that are related

  databaseMetadata.getImportedKeys


 */
  private static final String CATALOG = "dummyCatalog";
  private static final String SCHEMA = "dummySchema";

  private DatabaseAnalyzer underTest;

  @Mock
  private Connection connection;

  @Mock
  private DatabaseMetaData metadata;

  @Mock
  private TypeMap typeMap;

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Before
  public void setup() throws SQLException {
    when(connection.getMetaData()).thenReturn(metadata);
    when(connection.getCatalog()).thenReturn(CATALOG);
    underTest = new DatabaseAnalyzer(connection, SCHEMA, typeMap);
  }

  @Test
  public void testInitializeRelations() {
    String entityAName = "entity-a";
    String entityBName = "entity-b";
    String entityAAttr = "entity-b-attr";
    String entityBAttr = "entity-a-attr";
    String pkDatatype = "pk-datatype";
    String fkDatatype = "fk-datatype";

    try {
      when(this.typeMap.getAsGraphQLTypeString(Types.ARRAY))
          .thenReturn(pkDatatype)
          .thenReturn(fkDatatype);

      ResultSet results = mock(ResultSet.class);
      when(results.getString("PKTABLE_NAME"))
          .thenReturn(entityAName)
          .thenReturn(null);
      when(results.getString("PKCOLUMN_NAME"))
          .thenReturn(entityAAttr)
          .thenReturn(null);
      when(results.getString("FKTABLE_NAME"))
          .thenReturn(entityBName)
          .thenReturn(null);
      when(results.getString("FKCOLUMN_NAME"))
          .thenReturn(entityBAttr)
          .thenReturn(null);

      Entity entityA = new Entity(entityAName);
      Entity entityB = new Entity(entityBName);

      Attribute pk = new Attribute(entityAAttr, 1, pkDatatype, true, false);
      entityA.addAttribute(pk);

      Attribute fk = new Attribute(entityBAttr, 1, fkDatatype, false, false);
      entityB.addAttribute(fk);

      Relation pkRelation = new Relation(entityB, fk, MANY);
      pk.setForeignKey(pkRelation);

      Relation fkRelation = new Relation(entityA, pk, ONE);
      fk.setForeignKey(fkRelation);

      Map<String, Entity> mockRelations = new HashMap<>();
      mockRelations.put(entityA.getName(), entityA);
      mockRelations.put(entityB.getName(), entityB);

      for(Entity entity : mockRelations.values()) {
        this.underTest.initializeRelations(entity, results, mockRelations);
        // assert relation is present
      }
    } catch (SQLException e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  @Test
  public void testInitializeAttribute() {
    String columnName = "mockColumn";
    int position = 99;
    boolean isPrimaryKey = true;
    int nullable = 1;
    int dataType = 999;
    String dataTypeString = "datatype";

    //noinspection ConstantConditions
    Attribute expected = mockAttribute(
        columnName,
        position,
        dataTypeString,
        isPrimaryKey,
        nullable == 1
    );

    try {
      @SuppressWarnings("unchecked")
      List<String> mockPrimaryKeys = mock(List.class);

      //noinspection ConstantConditions
      when(mockPrimaryKeys.contains(columnName))
          .thenReturn(isPrimaryKey);
      ResultSet resultSet = mock(ResultSet.class);
      when(resultSet.getString("COLUMN_NAME"))
          .thenReturn(columnName);
      when(resultSet.getInt("ORDINAL_POSITION"))
          .thenReturn(position);
      when(resultSet.getInt("DATA_TYPE"))
          .thenReturn(dataType);
      when(resultSet.getInt("NULLABLE"))
          .thenReturn(nullable);

      when(typeMap.getAsGraphQLTypeString(dataType))
          .thenReturn(dataTypeString);

      Attribute actual = this.underTest.initializeAttribute(resultSet, mockPrimaryKeys);

      assertEquals(expected, actual);
    } catch (SQLException e) {
      fail("Unexpected exception: " + e.getMessage());
    }
  }

  private Attribute mockAttribute(String name, int position, String type, boolean isPrimaryKey, boolean isNullable) {
    return new Attribute(
        name,
        position,
        type,
        isPrimaryKey,
        isNullable);
  }
}
