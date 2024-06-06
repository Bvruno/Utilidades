package com.consulta.consultamedica.controller;

import com.consulta.consultamedica.dao.ResponseRepository;
import com.consulta.consultamedica.model.Response;
import com.consulta.consultamedica.service.TesseractOCRService;
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
public class ResponseViewController {

	@Autowired
	private TesseractOCRService tesseractOCRService;

	@Autowired
	private ResponseRepository responseRepository;

	@GetMapping("/responses")
	public String getResponses(Model model) {
		List<Response> responses = responseRepository.findAll();
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
		responseRepository.save(response);
		List<Response> responses = responseRepository.findAll();
		responses.sort(Comparator.comparing(Response::getId));
		model.addAttribute("responses", responses);
		return "responses";
	}


}
