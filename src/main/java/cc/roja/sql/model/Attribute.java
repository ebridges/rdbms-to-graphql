package cc.roja.sql.model;

import cc.roja.sql.types.TypeMap;

public class Attribute implements Comparable<Attribute> {
  private final String name;
  private final int position;
  private final TypeMap type;
  private final boolean isPrimaryKey;
  private final boolean isNullable;
  private Relation foreignKey;

  public Attribute(String name, int position, TypeMap type, boolean isPrimaryKey, boolean isNullable) {
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

  public TypeMap getType() {
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
}
