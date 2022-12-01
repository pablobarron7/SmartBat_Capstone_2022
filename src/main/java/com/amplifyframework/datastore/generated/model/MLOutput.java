package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the MLOutput type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "MLOutputs", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class MLOutput implements Model {
  public static final QueryField ID = field("MLOutput", "id");
  public static final QueryField LOCATION = field("MLOutput", "location");
  public static final QueryField SPEED = field("MLOutput", "speed");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String location;
  private final @ModelField(targetType="String", isRequired = true) String speed;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }

  public String getLocation() {
      return location;
  }

  public String getSpeed() {
      return speed;
  }

  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }

  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }

  private MLOutput(String id, String location, String speed) {
    this.id = id;
    this.location = location;
    this.speed = speed;
  }

  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      MLOutput mlOutput = (MLOutput) obj;
      return ObjectsCompat.equals(getId(), mlOutput.getId()) &&
              ObjectsCompat.equals(getLocation(), mlOutput.getLocation()) &&
              ObjectsCompat.equals(getSpeed(), mlOutput.getSpeed()) &&
              ObjectsCompat.equals(getCreatedAt(), mlOutput.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), mlOutput.getUpdatedAt());
      }
  }

  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getLocation())
      .append(getSpeed())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }

  @Override
   public String toString() {
    return new StringBuilder()
      .append("MLOutput {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("location=" + String.valueOf(getLocation()) + ", ")
      .append("speed=" + String.valueOf(getSpeed()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }

  public static LocationStep builder() {
      return new Builder();
  }

  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static MLOutput justId(String id) {
    return new MLOutput(
      id,
      null,
      null
    );
  }

  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      location,
      speed);
  }
  public interface LocationStep {
    SpeedStep location(String location);
  }


  public interface SpeedStep {
    BuildStep speed(String speed);
  }


  public interface BuildStep {
    MLOutput build();
    BuildStep id(String id);
  }


  public static class Builder implements LocationStep, SpeedStep, BuildStep {
    private String id;
    private String location;
    private String speed;
    @Override
     public MLOutput build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();

        return new MLOutput(
          id,
          location,
          speed);
    }

    @Override
     public SpeedStep location(String location) {
        Objects.requireNonNull(location);
        this.location = location;
        return this;
    }

    @Override
     public BuildStep speed(String speed) {
        Objects.requireNonNull(speed);
        this.speed = speed;
        return this;
    }

    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }


  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String location, String speed) {
      super.id(id);
      super.location(location)
        .speed(speed);
    }

    @Override
     public CopyOfBuilder location(String location) {
      return (CopyOfBuilder) super.location(location);
    }

    @Override
     public CopyOfBuilder speed(String speed) {
      return (CopyOfBuilder) super.speed(speed);
    }
  }

}
