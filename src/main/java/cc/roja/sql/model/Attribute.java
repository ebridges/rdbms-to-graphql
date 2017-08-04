package cc.roja.sql.model;

import java.util.Objects;

import cc.roja.sql.types.TypeMap;

public class Attribute implements Comparable<Attribute> {
  private final String name;
  private final int position;
  private final String type;
  private final boolean isPrimaryKey;
  private final boolean isNullable;
  private Relation foreignKey;

  public Attribute(String name, int position, String type, boolean isPrimaryKey, boolean isNullable) {
    this.name = name;
    this.position = position;
    this.type = type;
    this.isPrimaryKey = isPrimaryKey;
    this.isNullable = isNullable;
  }

  public String getName() {
    return name;
  }

  public int getPosition() {
    return position;
  }

  public String getType() {
    return type;
  }

  public boolean isPrimaryKey() {
    return isPrimaryKey;
  }

  public boolean isNullable() {
    return isNullable;
  }

  public Relation getForeignKey() {
    return foreignKey;
  }

  public void setForeignKey(Relation foreignKey) {
    this.foreignKey = foreignKey;
  }

  @Override
  public int compareTo(Attribute that) {
    return Integer.compare(this.position, that.position);
  }

  @Override
  public String toString() {
    return "Attribute{" +
        "name='" + name + '\'' +
        ", position=" + position +
        ", type=" + type +
        ", isPrimaryKey=" + isPrimaryKey +
        ", isNullable=" + isNullable +
        ", foreignKey=" + foreignKey +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Attribute attribute = (Attribute) o;
    return position == attribute.position &&
        isPrimaryKey == attribute.isPrimaryKey &&
        isNullable == attribute.isNullable &&
        Objects.equals(name, attribute.name) &&
        Objects.equals(type, attribute.type) &&
        Objects.equals(foreignKey, attribute.foreignKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, position, type, isPrimaryKey, isNullable, foreignKey);
  }
}
