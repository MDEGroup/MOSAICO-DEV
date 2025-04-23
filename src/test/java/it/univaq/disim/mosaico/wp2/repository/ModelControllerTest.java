package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class ModelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void all() throws Exception {
    mockMvc.perform(get("/model"))
        .andExpect(status().isOk());
  }

  @Test
  void all2() throws Exception {
    mockMvc.perform(get("/models"))
        .andExpect(status().is4xxClientError());
  }
}