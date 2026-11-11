package pl.edu.vistula.firstrestapi.product.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import pl.edu.vistula.firstrestapi.product.api.request.ProductRequest;
import pl.edu.vistula.firstrestapi.product.api.request.UpdateProductRequest;
import pl.edu.vistula.firstrestapi.product.api.response.ProductResponse;
import pl.edu.vistula.firstrestapi.product.service.ProductService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Create, read, update and delete products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a product")
    @ApiResponse(responseCode = "201", description = "Product created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.create(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a product by ID")
    public ResponseEntity<ProductResponse> find(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.find(id));
    }

    @GetMapping
    @Operation(summary = "List all products")
    public ResponseEntity<List<ProductResponse>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<ProductResponse> update(
            @PathVariable("id") Long id, @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    @ApiResponse(responseCode = "204", description = "Product deleted", content = @Content)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
