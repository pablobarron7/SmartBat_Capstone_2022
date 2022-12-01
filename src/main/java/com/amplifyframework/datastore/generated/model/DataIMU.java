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

/** This is an auto generated class representing the DataIMU type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "DataIMUS", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class DataIMU implements Model {
  public static final QueryField ID = field("DataIMU", "id");
  public static final QueryField INPUT_DATA = field("DataIMU", "inputData");
  public static final QueryField NAME = field("DataIMU", "name");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String inputData;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getInputData() {
      return inputData;
  }
  
  public String getName() {
      return name;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private DataIMU(String id, String inputData, String name) {
    this.id = id;
    this.inputData = inputData;
    this.name = name;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      DataIMU dataImu = (DataIMU) obj;
      return ObjectsCompat.equals(getId(), dataImu.getId()) &&
              ObjectsCompat.equals(getInputData(), dataImu.getInputData()) &&
              ObjectsCompat.equals(getName(), dataImu.getName()) &&
              ObjectsCompat.equals(getCreatedAt(), dataImu.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), dataImu.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getInputData())
      .append(getName())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("DataIMU {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("inputData=" + String.valueOf(getInputData()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static InputDataStep builder() {
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
  public static DataIMU justId(String id) {
    return new DataIMU(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      inputData,
      name);
  }
  public interface InputDataStep {
    NameStep inputData(String inputData);
  }
  

  public interface NameStep {
    BuildStep name(String name);
  }
  

  public interface BuildStep {
    DataIMU build();
    BuildStep id(String id);
  }
  

  public static class Builder implements InputDataStep, NameStep, BuildStep {
    private String id;
    private String inputData;
    private String name;
    @Override
     public DataIMU build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new DataIMU(
          id,
          inputData,
          name);
    }
    
    @Override
     public NameStep inputData(String inputData) {
        Objects.requireNonNull(inputData);
        this.inputData = inputData;
        return this;
    }
    
    @Override
     public BuildStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
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
    private CopyOfBuilder(String id, String inputData, String name) {
      super.id(id);
      super.inputData(inputData)
        .name(name);
    }
    
    @Override
     public CopyOfBuilder inputData(String inputData) {
      return (CopyOfBuilder) super.inputData(inputData);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
  }
  
}
