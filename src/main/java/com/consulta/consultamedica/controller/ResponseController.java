package com.consulta.consultamedica.controller;

import com.consulta.consultamedica.model.Response;
import com.consulta.consultamedica.service.ResponseService;
import com.consulta.consultamedica.service.TesseractOCRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/responses")
public class ResponseController {
    @Autowired
    private TesseractOCRService tesseractOCRService;
    @Autowired
    private ResponseService responseService;

    @PutMapping("/{id}/state")
    public ResponseEntity<Response> changeState(@PathVariable Integer id, @RequestBody String newState) {
        Response updatedResponse = responseService.changeState(id, newState);
        return ResponseEntity.ok(updatedResponse);
    }

    @PostMapping("/ocr")
	public Response recognizeText(@RequestParam("file") MultipartFile file) throws IOException {
		return tesseractOCRService.recognizeText(file.getInputStream());
	}
}
