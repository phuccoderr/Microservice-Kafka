package com.phuc.productservice.service;

import com.phuc.productservice.controller.CreateProductRestModel;

public interface ProductService {
    String createProduct(CreateProductRestModel productRestModel)  throws Exception;
}
