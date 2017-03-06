import java.util.List;
import org.sql2o.*;
import java.sql.Timestamp;

public class Sightings implements DatabaseManagement {
  private int id;
  private String location;
  private String ranger_name;
  private Timestamp spotted;

  public Sightings(String location, String ranger_name) {
    this.location = location;
    this.ranger_name = ranger_name;
  }

  public String getLocation() {
    return location;
  }

  public String getRangerName() {
    return ranger_name;
  }

  public void setLocation() {
    this.location = location;
  }

  public void setRangerName() {
    this.ranger_name = ranger_name;
  }

  public int getId() {
    return id;
  }

  public Timestamp getSpotted() {
    return spotted;
  }

  @Override
  public boolean equals(Object otherSighting) {
    if (!(otherSighting instanceof Sightings)) {
      return false;
    } else {
      Sightings newSighting = (Sightings) otherSighting;
      return this.getLocation().equals(newSighting.getLocation()) &&
             this.getRangerName().equals(newSighting.getRangerName());
    }
  }

  @Override
  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO sightings (location, ranger_name, spotted) VALUES (:location, :ranger_name, now())";
      this.id = (int) con.createQuery(sql, true)
      .addParameter("location", this.location)
      .addParameter("ranger_name", this.ranger_name)
      .executeUpdate()
      .getKey();
    }
  }

  public static Sightings find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM sightings WHERE id = :id";
      Sightings sighting = con.createQuery(sql)
                              .addParameter("id", id)
                              .executeAndFetchFirst(Sightings.class);
      return sighting;
    }
  }

  public static List<Sightings> all() {
    try(Connection con = DB.sql2o.open()) {
      String allSightingsQuery = "SELECT * FROM sightings";
      return con.createQuery(allSightingsQuery)
                .executeAndFetch(Sightings.class);
    }
  }

  public void addEndangeredAnimals(EndangeredAnimals endangeredAnimal) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO animals_sightings (sighting_id, general_animal_id) VALUES (:sighting_id, :general_animal_id)";
      con.createQuery(sql)
      .addParameter("sighting_id", this.getId())
      .addParameter("general_animal_id", endangeredAnimal.getId())
      .executeUpdate();
    }
  }

  public List<EndangeredAnimals> getEndangeredAnimals() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT general_animals.* FROM sightings " +
                   "JOIN animals_sightings ON (sightings.id = animals_sightings.sighting_id) " +
                   "JOIN general_animals ON (animals_sightings.general_animal_id = general_animals.id) " +
                   "WHERE sightings.id = :id";
      return con.createQuery(sql)
                .addParameter("id", this.id)
                .executeAndFetch(EndangeredAnimals.class);
    }
  }

  public void addAnimals(Animals animal) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO animals_sightings (sighting_id, general_animal_id) VALUES (:sighting_id, :general_animal_id)";
      con.createQuery(sql)
      .addParameter("sighting_id", this.getId())
      .addParameter("general_animal_id", animal.getId())
      .executeUpdate();
    }
  }

  public List<Animals> getAnimals() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT general_animals.* FROM sightings " +
                   "JOIN animals_sightings ON (sightings.id = animals_sightings.sighting_id) " +
                   "JOIN general_animals ON (animals_sightings.general_animal_id = general_animals.id) " +
                   "WHERE sightings.id = :id";
      return con.createQuery(sql)
                .addParameter("id", this.id)
                .executeAndFetch(Animals.class);
    }
  }

  @Override
  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM sightings WHERE id = :id";
      con.createQuery(sql)
      .addParameter("id", this.id)
      .executeUpdate();

      sql = "DELETE FROM animals_sightings WHERE sighting_id = :id";
      con.createQuery(sql)
      .addParameter("id", this.id)
      .executeUpdate();
    }
  }

  public void update(String location, String ranger_name) {
    this.location = location;
    this.ranger_name = ranger_name;
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE sightings SET location = :location, ranger_name = :ranger_name WHERE id = :id";
      con.createQuery(sql)
        .addParameter("id", this.id)
        .addParameter("location", location)
        .addParameter("ranger_name", ranger_name)
        .throwOnMappingFailure(false)
        .executeUpdate();
    }
  }

  public void removeEndangeredAnimal(EndangeredAnimals animal) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM animals_sightings WHERE general_animal_id = :id AND sighting_id = :sighting_id";
      con.createQuery(sql)
        .addParameter("id", animal.getId())
        .addParameter("sighting_id", this.id)
        .executeUpdate();
    }
  }

  public void removeAnimal(Animals animal) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM animals_sightings WHERE general_animal_id = :id AND sighting_id = :sighting_id";
      con.createQuery(sql)
        .addParameter("id", animal.getId())
        .addParameter("sighting_id", this.id)
        .executeUpdate();
    }
  }

}
