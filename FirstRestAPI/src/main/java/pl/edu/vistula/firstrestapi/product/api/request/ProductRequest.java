package pl.edu.vistula.firstrestapi.product.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductRequest {
    private final String name;

    @JsonCreator
    public ProductRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
