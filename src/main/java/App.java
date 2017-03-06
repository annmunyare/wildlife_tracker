import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

public class App {
  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("sightings", Sightings.all());
      model.put("animals", Animals.all());
      model.put("endangered-animals", EndangeredAnimals.all());
      model.put("template", "templates/index.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/sightings", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      String location = request.queryParams("location");
      String rangerName = request.queryParams("ranger_name");
      Animals animal = Animals.find(Integer.parseInt(request.queryParams("animalId")));
      EndangeredAnimals endangeredAnimal = EndangeredAnimals.find(Integer.parseInt(request.queryParams("endangered-animalId")));
      String url = String.format("/whoops");
      if(location.equals("") || rangerName.equals("")) {
        response.redirect(url);
        throw new Exception("You need to enter more information!");
      }
      Sightings newSighting = new Sightings(location, rangerName);
      newSighting.save();
      newSighting.addAnimals(animal);
      newSighting.addEndangeredAnimals(endangeredAnimal);
      model.put("sightings", Sightings.all());
      model.put("spotted", newSighting.getSpotted());
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/sighting-success.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/sightings/:id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Sightings sightingId = Sightings.find(Integer.parseInt(request.params(":id")));
      model.put("sighting", sightingId);
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/sighting.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/sightings/:id/update", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Sightings sightingId = Sightings.find(Integer.parseInt(request.params(":id")));
      String location = request.queryParams("location");
      String rangerName = request.queryParams("ranger-name");
      Sightings newSighting = Sightings.find(sightingId.getId());
      sightingId.update(location, rangerName);
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/sighting-success.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/sightings/:id/delete", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Sightings sightingId = Sightings.find(Integer.parseInt(request.params(":id")));
      sightingId.delete();
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/sighting-delete.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/animals", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      String url = String.format("/whoops");
      String animalName = request.queryParams("animal-name");
        if(animalName.equals("")) {
          response.redirect(url);
          throw new UnsupportedOperationException("You need to enter a Species!");
        }
      String age = request.queryParams("animal-age");
      Animals newAnimal = new Animals(animalName, age);
      newAnimal.save();
      model.put("sightings", newAnimal.getSightings());
      model.put("animals", Animals.all());
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/sighting-success.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/animals/:id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Animals animalId = Animals.find(Integer.parseInt(request.params(":id")));
      model.put("animal", animalId);
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/animal.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/animals/:id/update", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Animals animalId = Animals.find(Integer.parseInt(request.params(":id")));
      String animalName = request.queryParams("animal-name");
      String age = request.queryParams("animal-age");
      Animals newAnimal = Animals.find(animalId.getId());
      animalId.update(animalName, age);
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/animal-success.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/animals/:id/delete", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      Animals animalId = Animals.find(Integer.parseInt(request.params(":id")));
      animalId.delete();
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/animal-delete.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/endangered-animals", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      String animalName = request.queryParams("endangered-animal-name");
      String health = request.queryParams("health");
      String age = request.queryParams("endangered-animal-age");
      int amount = Integer.parseInt(request.queryParams("amount"));
      EndangeredAnimals newAnimal = new EndangeredAnimals(animalName, health, age, amount);
      newAnimal.save();
      model.put("endangered-animals", EndangeredAnimals.all());
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/animal-success.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/endangered-animals/:id", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      EndangeredAnimals animalId = EndangeredAnimals.find(Integer.parseInt(request.params(":id")));
      model.put("endangered-animal", animalId);
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/endangered-animal.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/endangered-animals/:id/update", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      EndangeredAnimals animalId = EndangeredAnimals.find(Integer.parseInt(request.params(":id")));
      String animalName = request.queryParams("endangered-animal-name");
      String health = request.queryParams("health");
      String age = request.queryParams("animal-age");
      EndangeredAnimals newAnimal = EndangeredAnimals.find(animalId.getId());
      animalId.update(animalName, health, age);
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/animal-success.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    post("/endangered-animals/:id/delete", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      EndangeredAnimals animal = EndangeredAnimals.find(Integer.parseInt(request.params(":id")));
      animal.delete();
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/animal-delete.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

    get("/whoops", (request, response) -> {
      Map<String, Object> model = new HashMap<String, Object>();
      model.put("header", "templates/header.vtl");
      model.put("template", "templates/whoops.vtl");
      return new ModelAndView(model, layout);
    }, new VelocityTemplateEngine());

  }
}
