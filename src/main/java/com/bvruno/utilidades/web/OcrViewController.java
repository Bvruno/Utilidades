package com.bvruno.utilidades.web;

import com.bvruno.utilidades.service.TesseractOCRService;
import com.bvruno.utilidades.repository.OcrRepository;
import com.bvruno.utilidades.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Controller
@Slf4j
public class OcrViewController {

	@Autowired
	private TesseractOCRService tesseractOCRService;

	@Autowired
	private OcrRepository ocrRepository;

	@GetMapping("/responses")
	public String getResponses(Model model) {
		List<Response> responses = ocrRepository.findAll();
		responses.sort(Comparator.comparing(Response::getId));
		model.addAttribute("responses", responses);
		return "responses";
	}

	@PostMapping("/responses")
	public String uploadImage(@RequestParam("image") MultipartFile image, Model model) throws IOException {
		Response response = tesseractOCRService.recognizeText(image.getInputStream());
		response.setImgBase64(image.isEmpty() ? "" : Base64.getEncoder().encodeToString(image.getBytes()));
		log.info(image.getBytes().length + " bytes");
		log.info(Base64.getEncoder().encodeToString(image.getBytes()).length() + " Base64");
		ocrRepository.save(response);
		List<Response> responses = ocrRepository.findAll();
		responses.sort(Comparator.comparing(Response::getId));
		model.addAttribute("responses", responses);
		return "responses";
	}


}
