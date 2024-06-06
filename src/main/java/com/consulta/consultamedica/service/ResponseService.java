package com.consulta.consultamedica.service;

import com.consulta.consultamedica.dao.ResponseRepository;
import com.consulta.consultamedica.exception.ResourceNotFoundException;
import com.consulta.consultamedica.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {
    @Autowired
    private ResponseRepository responseRepository;

    public Response changeState(Integer id, String newState) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Response not found with id " + id));

        response.setState(newState);
        return responseRepository.save(response);
    }
}
