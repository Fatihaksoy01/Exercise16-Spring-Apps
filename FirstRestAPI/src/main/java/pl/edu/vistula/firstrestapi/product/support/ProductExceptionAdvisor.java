package pl.edu.vistula.firstrestapi.product.support;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.edu.vistula.firstrestapi.product.support.exception.ProductNotFoundException;
import pl.edu.vistula.firstrestapi.shared.api.response.ErrorMessageResponse;

@ControllerAdvice
public class ProductExceptionAdvisor {
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorMessageResponse productNotFound(ProductNotFoundException exception) {
        return new ErrorMessageResponse(exception.getMessage());
    }
}
