import java.util.List;
import org.sql2o.*;
import java.sql.Timestamp;

public class Animals extends GeneralAnimal implements DatabaseManagement {
  public static final String DATABASE_TYPE = "animal";
  public static final String NEWBORN = "newborn";
  public static final String YOUNG = "young";
  public static final String ADULT = "adult";

  public Animals(String name, String age) {
    this.name = name;
    this.age = age;
    type = DATABASE_TYPE;
  }


  public static List<Animals> all() {
    String sql = "SELECT * FROM general_animals WHERE type = 'animal';";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql)
      .throwOnMappingFailure(false)
      .executeAndFetch(Animals.class);
    }
  }

  public static Animals find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM general_animals WHERE id = :id";
      Animals animal = con.createQuery(sql)
                          .addParameter("id", id)
                          .throwOnMappingFailure(false)
                          .executeAndFetchFirst(Animals.class);
    return animal;
    }
  }

    public void update(String name, String age) {
    this.name = name;
    this.age = age;
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE general_animals SET name = :name, age = :age WHERE id = :id";
      con.createQuery(sql)
        .addParameter("id", this.id)
        .addParameter("name", name)
        .addParameter("age", age)
        .throwOnMappingFailure(false)
        .executeUpdate();
    }
  }

  public List<Sightings> getSightings() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT sightings.* FROM general_animals " +
                   "JOIN animals_sightings ON (general_animals.id = animals_sightings.general_animal_id) " +
                   "JOIN sightings ON (animals_sightings.sighting_id = sightings.id) " +
                   "WHERE general_animals.id = :id";
      return con.createQuery(sql)
                .addParameter("id", this.id)
                .executeAndFetch(Sightings.class);
    }
  }

  public void leaveSightings(Sightings sighting) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM animals_sightings WHERE sighting_id = :sighting_id AND general_animal_id = :general_animal_id";
      con.createQuery(sql)
        .addParameter("sighting_id", sighting.getId())
        .addParameter("general_animal_id", this.getId())
        .executeUpdate();
    }
  }

  @Override
  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM general_animals WHERE id = :id";
      con.createQuery(sql)
      .addParameter("id", this.id)
      .executeUpdate();
      String joinDeleteQuery = "DELETE FROM animals_sightings WHERE general_animal_id = :newid";
      con.createQuery(joinDeleteQuery)
        .addParameter("newid", this.id)
        .executeUpdate();
    }
  }

  @Override
  public boolean equals(Object otherAnimal) {
    if (!(otherAnimal instanceof Animals)) {
      return false;
    } else {
      Animals newAnimal = (Animals) otherAnimal;
      return this.getName().equals(newAnimal.getName()) &&
             this.getId() == newAnimal.getId();
    }
  }

}
