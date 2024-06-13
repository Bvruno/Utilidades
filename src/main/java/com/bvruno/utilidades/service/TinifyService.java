package com.bvruno.utilidades.service;

import com.bvruno.utilidades.model.MethodResize;
import com.tinify.Options;
import com.tinify.Result;
import com.tinify.Source;
import com.tinify.Tinify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class TinifyService {

    @Value("${tinify.api.key}")
    private String TINIFY_API_KEY;

    public void compressImage(InputStream inputStream, String destination) throws IOException {
        // Copia el InputStream a un archivo temporal
        File tempFile = File.createTempFile("tinify", "tmp");
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Comprime la imagen con Tinify
        Tinify.setKey(TINIFY_API_KEY);
        byte[] compressedImage = Tinify.fromFile(tempFile.getAbsolutePath()).toBuffer();

        // Guarda la imagen comprimida en el destino
        File outputFile = new File(destination);
        Files.write(outputFile.toPath(), compressedImage);

        // Elimina el archivo temporal
        tempFile.deleteOnExit();

    }

    public void resizeImage(InputStream inputStream, String destination, int width, int height, MethodResize methodResize) throws IOException {
        // Copia el InputStream a un archivo temporal
        File tempFile = File.createTempFile("tinify", "tmp");
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Comprime y redimensiona la imagen con Tinify
        Tinify.setKey(TINIFY_API_KEY);
        byte[] resizedImage = Tinify.fromFile(tempFile.getAbsolutePath())
                .resize(new Options()
                        .with("method", methodResize)
                        .with("width", width)
                        .with("height", height))
                .toBuffer();
        // Guarda la imagen redimensionada en el destino
        File outputFile = new File(destination);
        Files.write(outputFile.toPath(), resizedImage);

        // Elimina el archivo temporal
        tempFile.deleteOnExit();

    }

    public void convertImage(InputStream inputStream, String destination, String format) throws IOException {
        // Copia el InputStream a un archivo temporal
        File tempFile = File.createTempFile("tinify", "tmp");
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Comprime y convierte la imagen con Tinify
        Tinify.setKey(TINIFY_API_KEY);

        Source source = Tinify.fromFile(tempFile.getAbsolutePath());

        Result converted = source.convert(new Options().with("type",new String[]{"image/"+format} ))
                .result();

        String pathname = destination.substring(0, destination.indexOf(".") + 1) + format;
        converted.toFile(pathname);

        byte[] convertedImage = converted.toBuffer();

        File outputFile = new File(pathname);

        Files.write(outputFile.toPath(), convertedImage);

        // Elimina el archivo temporal
        tempFile.deleteOnExit();
    }

    public ResponseEntity<byte[]> processImages(
            List<MultipartFile> files,
            String prefix,
            BiConsumer<MultipartFile, String> imageProcessor,
            String format
    ) throws IOException {

        String zipFileName = prefix + "images.zip";
        FileOutputStream fos = new FileOutputStream(zipFileName);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (MultipartFile file : files) {
            String destination = prefix + file.getOriginalFilename();
            imageProcessor.accept(file, destination);

            if (format != null) {
                destination = destination.substring(0, destination.indexOf(".") + 1) + format;
            }

            addToZipFile(destination, zos);
            Path path = Paths.get(destination);
            Files.deleteIfExists(path);
        }

        zos.close();
        fos.close();

        Path path = Paths.get(zipFileName);
        byte[] zip = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(zip.length);

        // Elimina el archivo después de enviarlo
        Files.deleteIfExists(path);

        return ResponseEntity.ok().headers(headers).body(zip);
    }

    public void addToZipFile(String fileName, ZipOutputStream zos) throws IOException {
        File file = new File(fileName);

        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }
}
