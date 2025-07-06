package pay.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private ERole name;

  public Role() {}

  @JsonCreator
  public static Role fromString(String value) {
    if (value == null) {
      return null;
    }
    return new Role(ERole.valueOf(value.toUpperCase()));
  }

  public Role(ERole name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @JsonValue
  public ERole getName() {
    return name;
  }

  public void setName(ERole name) {
    this.name = name;
  }
}