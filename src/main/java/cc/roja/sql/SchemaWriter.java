package cc.roja.sql;

import static cc.roja.sql.model.Cardinality.MANY;
import static java.lang.String.format;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import cc.roja.sql.model.Attribute;
import cc.roja.sql.model.Entity;
import cc.roja.sql.model.Relation;

@SuppressWarnings("WeakerAccess")
public class SchemaWriter {
  private final String outputDir;

  public SchemaWriter(String outputDir) {
    this.outputDir = outputDir;
  }

  public void writeEntities(List<Entity> entities) throws IOException {
    for(Entity entity : entities) {
      Path outputPath = Paths.get(outputDir, entity.getName() + ".schema");
      try(BufferedWriter writer = newBufferedWriter(outputPath, WRITE, CREATE, TRUNCATE_EXISTING)) {
        writeEntity(writer, entity);
      }
    }
  }

  /*
type Character {
  name: String!
  appearsIn: [Episode]!
}
   */

  public void writeEntity(BufferedWriter writer, Entity entity) throws IOException {
    String attributeString = formatAttributes(entity.getAttributes());
    String entityString = format("type %s {\n%s}\n", entity.getName(), attributeString);
    writer.write(entityString);
  }

  public String formatAttributes(Set<Attribute> attributes) {
    StringBuilder sb = new StringBuilder();
    for (Attribute attribute : attributes) {
      String attributeType = formatAttributeType(attribute);
      sb.append(format("\t%s: %s\n", attribute.getName(), attributeType));
    }
    return sb.toString();
  }

  public String formatAttributeType(Attribute attribute) {
    if(attribute.isPrimaryKey()) {
      return formatIDType(attribute);
    }

    if(attribute.getForeignKey() != null) {
      return formatRelationType(attribute);
    }

    return attribute.getType().getAsGraphQLTypeString();
  }

  private String formatRelationType(Attribute attribute) {
    Relation relation = attribute.getForeignKey();
    String type = relation.getForeignEntity().getName();

    if(!attribute.isNullable()) {
      type += "!";
    }

    if(relation.getCardinality() == MANY) {
      type = format("[%s]", type);
      if(!relation.getForeignAttribute().isNullable()) {
        type += "!";
      }
    }

    return type;
  }

  private String formatIDType(Attribute attribute) {
    String type = "ID";
    if(!attribute.isNullable()) {
      type += "!";
    }
    return type;
  }
}
