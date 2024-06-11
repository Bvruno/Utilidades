package com.bvruno.utilidades.controller;

import com.bvruno.utilidades.model.MethodResize;
import com.bvruno.utilidades.service.TinifyService;
import com.tinify.Tinify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/images")
@Slf4j
public class TinifyController {

    @Autowired
    private TinifyService imageService;

    @PostMapping("/compress")
    public ResponseEntity<byte[]> compressImages(
            @RequestParam List<MultipartFile> files
    ) throws IOException {
        return imageService.processImages(
                files,
                "compressed_",
                (file, destination) -> {
                    try {
                        imageService.compressImage(file.getInputStream(), destination);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                null
        );
    }

    @PostMapping("/resize")
    public ResponseEntity<byte[]> resizeImages(
            @RequestParam List<MultipartFile> files,
            @RequestParam int width,
            @RequestParam int height,
            @RequestParam MethodResize methodResize
    ) throws IOException {
        return imageService.processImages(
                files,
                "resized_",
                (file, destination) -> {
                    try {
                        imageService.resizeImage(file.getInputStream(), destination, width, height, methodResize);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                null
        );
    }

    @PostMapping("/convert")
    public ResponseEntity<byte[]> convertImages(
            @RequestParam List<MultipartFile> files,
            @RequestParam String format
    ) throws IOException {
        return imageService.processImages(
                files,
                "converted_",
                (file, destination) -> {
                    try {
                        imageService.convertImage(file.getInputStream(), destination, format);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                format
        );
    }
}
