package pl.edu.vistula.firstrestapi;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pl.edu.vistula.firstrestapi.product.repository.ProductRepository;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductApiTests {
    @Autowired private MockMvc mvc;
    @Autowired private ProductRepository repository;
    @Autowired private JdbcTemplate jdbc;

    @BeforeEach
    void clearProducts() {
        repository.deleteAll();
    }

    private long createProduct(String name) throws Exception {
        String response = mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(name))
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    @Test
    void completesCrudCycleAndPersistsEveryChangeInH2() throws Exception {
        long id = createProduct("First product");
        assertEquals("First product", jdbc.queryForObject(
                "select name from products where id = ?", String.class, id));

        mvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("First product"));

        mvc.perform(put("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated product\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Updated product"));
        assertEquals("Updated product", jdbc.queryForObject(
                "select name from products where id = ?", String.class, id));
        assertEquals(1, jdbc.queryForObject("select count(*) from products", Integer.class));

        mvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated product"));
        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Updated product"));

        mvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertEquals(0, jdbc.queryForObject("select count(*) from products", Integer.class));
        mvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with " + id + " not found"));
    }

    @Test
    void generatesDifferentIdsAndReturnsAllProducts() throws Exception {
        long first = createProduct("First product");
        long second = createProduct("Second product");
        assertTrue(second > first);
        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("First product", "Second product")));
    }

    @Test
    void returnsEmptyListWhenDatabaseIsEmpty() throws Exception {
        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "PUT", "DELETE"})
    void returnsDescriptive404ForMissingProduct(String method) throws Exception {
        MockHttpServletRequestBuilder request = switch (method) {
            case "PUT" -> put("/api/v1/products/999999")
                    .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Missing\"}");
            case "DELETE" -> delete("/api/v1/products/999999");
            default -> get("/api/v1/products/999999");
        };
        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Product with 999999 not found"));
        assertEquals(0, repository.count());
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON).content("{broken"))
                .andExpect(status().isBadRequest());
        assertEquals(0, repository.count());
    }

    @Test
    void rejectsNonNumericId() throws Exception {
        mvc.perform(get("/api/v1/products/not-a-number"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publishesEveryCrudOperationInOpenApi() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("First REST API - Products"))
                .andExpect(jsonPath("$.paths['/api/v1/products'].post").exists())
                .andExpect(jsonPath("$.paths['/api/v1/products'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/products'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/products/{id}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/products/{id}'].put").exists())
                .andExpect(jsonPath("$.paths['/api/v1/products/{id}'].delete").exists())
                .andExpect(jsonPath("$.paths['/api/v1/products/{id}'].delete.responses['204']").exists());
    }

    @Test
    void servesSwaggerUi() throws Exception {
        mvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Swagger UI")));
    }
}
