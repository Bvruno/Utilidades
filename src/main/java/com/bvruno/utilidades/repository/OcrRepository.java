package com.bvruno.utilidades.repository;

import com.bvruno.utilidades.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcrRepository extends JpaRepository<Response, Integer> {
}
