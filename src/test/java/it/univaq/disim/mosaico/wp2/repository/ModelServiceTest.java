
package it.univaq.disim.mosaico.wp2.repository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.service.impl.ModelServiceImpl;
import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;








public class ModelServiceTest {

    private static final Model  model1 = new Model("1", "Model Name", "Model Description", "1.0", "Author Name", "MIT");
    private static final Model  model2 = new Model("2", "Model Name2", "Model Description2", "2.0", "Author Name", "MIT");
    @Mock
    private ModelRepository modelRepository;

    @InjectMocks
    private ModelServiceImpl modelService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        List<Model> mockModels = Arrays.asList(model1, model2);
        when(modelRepository.findAll()).thenReturn(mockModels);

        List<Model> models = modelService.findAll();

        assertEquals(2, models.size());
        verify(modelRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        Model mockModel = model1;
        when(modelRepository.findById("1")).thenReturn(Optional.of(mockModel));

        Model model = modelService.findById("1");

        assertNotNull(model);
        assertEquals("1", model.id());
        assertEquals("Model Name", model.name());
        verify(modelRepository, times(1)).findById("1");
    }

    @Test
    public void testSave() {
        Model mockModel = model1;
        when(modelRepository.save(mockModel)).thenReturn(mockModel);

        Model savedModel = modelService.save(mockModel);

        assertNotNull(savedModel);
        assertEquals("1", savedModel.id());
        assertEquals("Model Name", savedModel.name());
        verify(modelRepository, times(1)).save(mockModel);
    }

    @Test
    public void testDeleteById() {
        doNothing().when(modelRepository).deleteById("1");

        modelService.deleteById("1");

        verify(modelRepository, times(1)).deleteById("1");
    }

    @Test
    public void testUpdate() {
        Model mockModel = new Model(model1.id(), "UpdatedModel", model1.description(), model1.version(), model1.author(), model1.license());
        when(modelRepository.existsById("1")).thenReturn(true);
        when(modelRepository.save(mockModel)).thenReturn(mockModel);

        Model updatedModel = modelService.update(mockModel);

        assertNotNull(updatedModel);
        assertEquals("1", updatedModel.id());
        assertEquals("UpdatedModel", updatedModel.name());
        verify(modelRepository, times(1)).existsById("1");
        verify(modelRepository, times(1)).save(mockModel);
    }
}
