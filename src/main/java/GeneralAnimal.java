import java.util.List;
import org.sql2o.*;
import java.sql.Timestamp;

public abstract class GeneralAnimal {
  public String name;
  public String type;
  public String age;
  public int id;


  public String getName() {
    return name;
  }

  public String getAge() {
    return age;
  }

  public int getId() {
    return id;
  }

  @Override
  public boolean equals(Object otherGeneralAnimal) {
    if (!(otherGeneralAnimal instanceof GeneralAnimal)) {
      return false;
    } else {
      GeneralAnimal newGeneralAnimal = (GeneralAnimal) otherGeneralAnimal;
      return this.getName().equals(newGeneralAnimal.getName());
    }
  }

  public void save() {
    try (Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO general_animals (name, type, age) VALUES (:name, :type, :age)";
      this.id = (int) con.createQuery(sql, true)
                         .addParameter("name", this.name)
                         .addParameter("type", this.type)
                         .addParameter("age", this.age)
                         .executeUpdate()
                         .getKey();
    }
  }

}
