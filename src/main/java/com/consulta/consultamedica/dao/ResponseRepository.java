package com.consulta.consultamedica.dao;

import com.consulta.consultamedica.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<Response, Integer> {
}
