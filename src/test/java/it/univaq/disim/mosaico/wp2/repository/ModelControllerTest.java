package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;
import it.univaq.disim.mosaico.wp2.repository.controller.ModelController;
import it.univaq.disim.mosaico.wp2.repository.data.Model;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ModelControllerTest {

    // @Autowired
    // private MockMvc mockMvc;

    // @Test
    // void all() throws Exception {
	// 	mockMvc.perform(get("/model"))
	// 		   .andExpect(status().isOk());
    // }
	// @Test
    // void all2() throws Exception {
	// 	mockMvc.perform(get("/models"))
	// 		   .andExpect(status().is4xxClientError());
    // }
	@Autowired
    private MockMvc mockMvc;

    @Mock
    private ModelRepository repository;

    @InjectMocks
    private ModelController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    void testGetAllModels() throws Exception {
        when(repository.findAll()).thenReturn(Arrays.asList(
            new Model("1","1", "Model1", "Description1", "Type1", "Owner1"),
            new Model("1","2", "Model2", "Description2", "Type2", "Owner2")
        ));

        mockMvc.perform(get("/model"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[1].id").value("2"));

        verify(repository, times(1)).findAll();
    }

    void testGetModelById() throws Exception {
        // when(repository.findById("1")).thenReturn(Optional.of(
        //     new Model("1","1", "Model1", "Description1", "Type1", "Owner1")
        // ));
        // mockMvc.perform(get("/model/1"))
        //     .andExpect(status().isOk())
        //     .andExpect(jsonPath("$.id").value("1"))
        //     .andExpect(jsonPath("$.name").value("1"));

        // verify(repository, times(1)).findById("1");
		when(repository.findById("1")).thenReturn(Optional.of(
            new Model("1","1", "Model1", "Description1", "Type1", "Owner1")
        ));

        Model response = controller.one("1");

        assertNotNull(response);
        System.out.println(response); 

    }

    void testGetModelByIdNotFound() throws Exception {
        when(repository.findById("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/model/1"))
            .andExpect(status().isNotFound());

        verify(repository, times(1)).findById("1");
    }

    
    void testUpdateModel() throws Exception {
        Model updatedModel = new Model("1", "1", "UpdatedModel", "UpdatedDescription", "UpdatedType", "UpdatedOwner");
        when(repository.findById("1")).thenReturn(Optional.of(new Model("1", "1", "Model1", "Description1", "Type1", "Owner1")));
        when(repository.save(any(Model.class))).thenReturn(updatedModel);

        mockMvc.perform(put("/model/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"1\",\"name\":\"UpdatedModel\",\"description\":\"UpdatedDescription\",\"type\":\"UpdatedType\",\"owner\":\"UpdatedOwner\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UpdatedModel"));

        verify(repository, times(1)).findById("1");
        verify(repository, times(1)).save(any(Model.class));
    }

    void testDeleteModel() throws Exception {
        doNothing().when(repository).deleteById("1");

        mockMvc.perform(delete("/model/1"))
            .andExpect(status().isOk());

        verify(repository, times(1)).deleteById("1");
    }

}