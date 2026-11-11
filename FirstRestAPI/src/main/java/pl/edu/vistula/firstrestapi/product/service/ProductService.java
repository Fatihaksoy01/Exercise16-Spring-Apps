package pl.edu.vistula.firstrestapi.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.vistula.firstrestapi.product.api.request.ProductRequest;
import pl.edu.vistula.firstrestapi.product.api.request.UpdateProductRequest;
import pl.edu.vistula.firstrestapi.product.api.response.ProductResponse;
import pl.edu.vistula.firstrestapi.product.domain.Product;
import pl.edu.vistula.firstrestapi.product.repository.ProductRepository;
import pl.edu.vistula.firstrestapi.product.support.ProductMapper;
import pl.edu.vistula.firstrestapi.product.support.ProductExceptionSupplier;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productRepository.save(productMapper.toProduct(request));
        return productMapper.toProductResponse(product);
    }

    public ProductResponse find(Long id) {
        return productMapper.toProductResponse(findProduct(id));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = findProduct(id);
        Product updated = productRepository.save(productMapper.toProduct(product, request));
        return productMapper.toProductResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findProduct(id);
        productRepository.deleteById(product.getId());
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(ProductExceptionSupplier.productNotFound(id));
    }
}
