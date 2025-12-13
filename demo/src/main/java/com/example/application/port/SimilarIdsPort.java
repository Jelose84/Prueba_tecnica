package com.example.application.port;


import java.util.List;

public interface SimilarIdsPort {
  List<Long> getSimilarIds(long productId);
}
