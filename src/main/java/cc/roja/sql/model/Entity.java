package cc.roja.sql.model;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class Entity {
  private final String name;
  private final Set<Attribute> attributes;

  public Entity(String name) {
    this.name = name;
    this.attributes = new TreeSet<>();
  }

  public void addAttribute(Attribute attribute) {
    this.attributes.add(attribute);
  }

  public String getName() {
    return name;
  }

  public Set<Attribute> getAttributes() {
    return Collections.unmodifiableSet(attributes);
  }

  @Override
  public String toString() {
    return "Entity{" +
        "name='" + name + '\'' +
        ", attributes=" + attributes +
        '}';
  }
}
