package com.bvruno.utilidades.service;

import com.bvruno.utilidades.repository.OcrRepository;
import com.bvruno.utilidades.exception.ResourceNotFoundException;
import com.bvruno.utilidades.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {
    @Autowired
    private OcrRepository ocrRepository;

    public Response changeState(Integer id, String newState) {
        Response response = ocrRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Response not found with id " + id));

        response.setState(newState);
        return ocrRepository.save(response);
    }
}
