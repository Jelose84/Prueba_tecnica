package com.example.demo.adapter.in.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.shared.exception.DownstreamException;
import com.example.demo.shared.exception.ProductNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ProblemDetail notFound(ProductNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    pd.setTitle("Not Found");
    pd.setDetail(ex.getMessage());
    return pd;
  }

  @ExceptionHandler(DownstreamException.class)
  public ProblemDetail downstream(DownstreamException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
    pd.setTitle("Downstream Unavailable");
    pd.setDetail(ex.getMessage());
    return pd;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail generic(Exception ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    pd.setTitle("Internal Error");
    pd.setDetail("Unexpected error");
    return pd;
  }
}