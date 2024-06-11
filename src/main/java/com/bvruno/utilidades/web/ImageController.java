package com.bvruno.utilidades.web;

import com.bvruno.utilidades.model.MethodResize;
import com.bvruno.utilidades.service.TinifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
public class ImageController {

    private final TinifyService tinifyService;

    @Autowired
    public ImageController(TinifyService tinifyService) {
        this.tinifyService = tinifyService;
    }


    @GetMapping("/tinify")
    public String tinify() {
        return "tinify";
    }

    @PostMapping("/compressImage")
    public ResponseEntity<byte[]> compressImage(@RequestParam("image") List<MultipartFile> image) throws IOException {

        return tinifyService.processImages(
                image,
                "compressed_",
                (file, destination) -> {
                    try {
                        tinifyService.compressImage(file.getInputStream(), destination);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                null
        );
    }

    @PostMapping("/resizeImage")
    public ResponseEntity<byte[]> resizeImage(@RequestParam("image") List<MultipartFile> image,
                                              @RequestParam("width") int width,
                                              @RequestParam("height") int height,
                                              @RequestParam("methodResize") MethodResize methodResize) throws IOException {
        return tinifyService.processImages(
                image,
                "resized_",
                (file, destination) -> {
                    try {
                        tinifyService.resizeImage(file.getInputStream(), destination, width, height, methodResize);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                null
        );
    }

    @PostMapping("/convertImage")
    public ResponseEntity<byte[]> convertImage(@RequestParam("image") List<MultipartFile> image,
                                               @RequestParam("format") String format) throws IOException {
        return tinifyService.processImages(
                image,
                "converted_",
                (file, destination) -> {
                    try {
                        tinifyService.convertImage(file.getInputStream(), destination, format);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                format
        );
    }
}
