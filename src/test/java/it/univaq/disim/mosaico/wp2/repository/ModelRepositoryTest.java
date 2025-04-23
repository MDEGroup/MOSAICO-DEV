package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;




@SpringBootTest
public class ModelRepositoryTest {

    private static final Model  model = new Model("1", "Model Name", "Model Description", "1.0", "Author Name", "MIT");
    @Autowired
    private ModelRepository repository;

    

    @Test
    
    public void createModel() {
        Model model = new Model("1", "Model Name", "Model Description", "1.0", "Author Name", "MIT");
        Model savedModel = repository.save(model);
        assertNotNull(savedModel);
        assertEquals("1", savedModel.id());
        assertEquals("Model Name", savedModel.name());
        //verify(repository, times(1)).save(model);
    }

    @Test
    public void findModelById() {
        
        Optional<Model> foundModel = repository.findById("1");

        assertTrue(foundModel.isPresent());
        assertEquals("1", foundModel.get().id());
        assertEquals("Model Name", foundModel.get().name());
        
    }

    @Test
    public void findModelByIdException() {
        Optional<Model> foundModel = repository.findById("2");
        assertTrue(!foundModel.isPresent());
    }

    @Test
    public void findModelByName() {
        var models = repository.findByName("Model Name");
        assertNotNull(models);
        assertEquals(1, models.size());
        assertEquals("Model Name", models.get(0).name());
    }

    @Test
    public void deleteModel() {
        repository.save(model);
        var i = repository.count();
        repository.deleteById("1");
        assertEquals(i-1, repository.count());
    }
}
