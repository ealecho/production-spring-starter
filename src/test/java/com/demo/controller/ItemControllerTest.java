package com.demo.controller;

import com.demo.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItemControllerTest extends AbstractIntegrationTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void createAndFetchItem() throws Exception {
    String payload = """
        {
          "name": "Sample Item",
          "description": "Created via test"
        }
        """;

    String response = mockMvc.perform(post("/api/v1/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Number idValue = JsonPath.read(response, "$.data.id");
    long id = idValue.longValue();

    mockMvc.perform(get("/api/v1/items/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(id));
  }
}
