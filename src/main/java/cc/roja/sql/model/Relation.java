package cc.roja.sql.model;

public class Relation {
  private final Entity foreignEntity;
  private final Attribute foreignAttribute;
  private final Cardinality cardinality;

  public Relation(Entity foreignEntity, Attribute foreignAttribute, Cardinality cardinality) {
    this.foreignEntity = foreignEntity;
    this.foreignAttribute = foreignAttribute;
    this.cardinality = cardinality;
  }

  public Entity getForeignEntity() {
    return foreignEntity;
  }

  public Attribute getForeignAttribute() {
    return foreignAttribute;
  }

  public Cardinality getCardinality() {
    return cardinality;
  }

  @Override
  public String toString() {
    return "Relation{" +
        "foreignEntity=" + (foreignEntity != null ? foreignEntity.getName() : "null") +
        ", foreignAttribute=" + (foreignAttribute != null ? foreignAttribute.getName() : "null") +
        ", cardinality=" + (cardinality != null ? cardinality.toString() : "null") +
        '}';
  }
}
