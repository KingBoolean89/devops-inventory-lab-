package com.example.inventory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void apiRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE));
    }

    @Test
    @WithMockUser
    void managesCategoriesProductsAndInventory() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Hardware",
                                  "description": "Physical products"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hardware"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sku": "HAMMER-001",
                                  "name": "Claw Hammer",
                                  "description": "16 oz steel hammer",
                                  "price": 14.99,
                                  "categoryId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.categoryName").value("Hardware"));

        mockMvc.perform(put("/api/inventory/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 42,
                                  "reorderLevel": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(42))
                .andExpect(jsonPath("$.reorderLevel").value(10));

        mockMvc.perform(get("/api/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("HAMMER-001"))
                .andExpect(jsonPath("$.productName").value("Claw Hammer"));

        mockMvc.perform(delete("/api/inventory/1"))
                .andExpect(status().isNoContent());
    }
}
