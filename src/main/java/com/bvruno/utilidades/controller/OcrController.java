package com.bvruno.utilidades.controller;

import com.bvruno.utilidades.service.ResponseService;
import com.bvruno.utilidades.service.TesseractOCRService;
import com.bvruno.utilidades.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/responses")
public class OcrController {
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
