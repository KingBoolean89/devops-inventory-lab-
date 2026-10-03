package com.example.inventory.category;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.inventory.config.SecurityConfig;
import com.example.inventory.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "app.security.username=test-user",
        "app.security.password=test-password"
})
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ProductRepository productRepository;

    @Test
    @WithMockUser
    void returnsNotFoundForMissingCategory() throws Exception {
        mockMvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value(containsString("Category 99")));
    }

    @Test
    @WithMockUser
    void validatesCreateRequest() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(containsString("name")));
    }
}
