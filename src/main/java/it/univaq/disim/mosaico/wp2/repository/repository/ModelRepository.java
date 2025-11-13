package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.Model;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {
    // This interface extends the JpaRepository interface, which provides CRUD operations for the Model entity.
    // The @RepositoryRestResource annotation exposes the repository as a RESTful resource.
    // The Model entity is identified by a String ID.
    
    List<Model> findByName(String name);
    // This method retrieves a list of Model entities by their name.
    // The method name follows the Spring Data naming convention, which allows for automatic query generation based on the method name.

 
     
}
