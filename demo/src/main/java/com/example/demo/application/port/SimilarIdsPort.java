package com.example.demo.application.port;


import java.util.List;

public interface SimilarIdsPort {
  List<Long> getSimilarIds(long productId);
}
