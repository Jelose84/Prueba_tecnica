package com.example.demo.application.port;

import com.example.demo.domain.model.ProductDetail;

public interface ProductDetailPort {
  ProductDetail getProductDetail(long productId);
}