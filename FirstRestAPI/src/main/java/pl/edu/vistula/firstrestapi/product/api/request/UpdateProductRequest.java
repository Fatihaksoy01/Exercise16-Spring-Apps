package pl.edu.vistula.firstrestapi.product.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateProductRequest extends ProductRequest {
    private final Long id;

    @JsonCreator
    public UpdateProductRequest(@JsonProperty("name") String name, @JsonProperty("id") Long id) {
        super(name);
        this.id = id;
    }

    public Long getId() { return id; }
}
