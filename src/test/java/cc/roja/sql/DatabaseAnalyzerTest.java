package cc.roja.sql;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import cc.roja.sql.model.Attribute;
import cc.roja.sql.types.TypeMap;

public class DatabaseAnalyzerTest {
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
