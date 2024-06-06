package com.consulta.consultamedica.service;

import com.consulta.consultamedica.model.Response;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TesseractOCRService {
    @Autowired
    private Tesseract tesseract;
    @Autowired
    private ImageProcessingService imageProcessingService;

    private static final List<String> BILLETERAS = List.of("PLIN", "YAPE");
    private static final List<String> ENTIDADES = List.of("PLIN", "YAPE", "BCP", "INTERBANK", "BBVA", "SCOTIABANK", "BANBIF", "BANCO DE LA NACION");
    private static final List<String> METODOS_DE_PAGO = List.of("TRANSFERENCIA", "DEPOSITO", "BILLETERA ELECTRONICA");

    public Response recognizeText(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        Response response = new Response();

        try {// Mejora el contraste y convierte a escala de grises
            BufferedImage processedImage = imageProcessingService.improveContrast(image);

            String textDetected = tesseract.doOCR(processedImage).toUpperCase();
            log.info("Texto detectado: " + textDetected);

            response.setEntidad(findEntity(textDetected));
            response.setMetodoDePago(findPaymentMethod(textDetected));
            response.setBilletera(findWallet(textDetected));
            response.setCodigoOperacion(findEightConsecutiveNumbers(textDetected));
            response.setFechaString(findDate(textDetected));
            response.setDestinatario(findNames(textDetected));
            response.setMonto(findAmount(textDetected));
            response.setState("PENDING");

            log.info("Cantidad de lineas: " + countLines(textDetected));
            log.info("Cantidad de lineas: " + getDataFromThirdLine(textDetected));
            log.info("N` Operacion: " + findEightConsecutiveNumbers(textDetected));
            response.setError(false);
            return response;
        } catch (TesseractException e) {
            e.printStackTrace();
        }

        response.setError(true);
        return response;
    }

    private String findEntity(String text) {
        return ENTIDADES.stream()
                .filter(text::contains)
                .findFirst()
                .orElse(null);
    }

    private String findPaymentMethod(String text) {
        return METODOS_DE_PAGO.stream()
                .filter(text::contains)
                .findFirst()
                .orElse(null);
    }

    private String findWallet(String text) {
        return BILLETERAS.stream()
                .filter(text::contains)
                .findFirst()
                .orElse(null);
    }

    private String findOperationCode(String text) {
        String operationCodePattern = "(?<=OPERACIÓN:)\\s*\\d+";
        Pattern pattern = Pattern.compile(operationCodePattern);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().trim();
        }

        return null;
    }

    private String findDate(String text) {
        String datePattern = "\\d{2} [A-Z]{3}\\. \\d{4} - \\d{2}:\\d{2} [AP]M";
        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private String findNames(String text) {
        String namePattern = "([A-Z\\s]+)(?=\\n\\d{2} [A-Z]{3}\\. \\d{4} - \\d{2}:\\d{2} [AP]M)";
        Pattern pattern = Pattern.compile(namePattern);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group().trim().replace("\n", " ");
        }

        return null;
    }

    private BigDecimal findAmount(String text) {
        String amountPattern = "(?<=¡YAPEASTE!)\\s*\\d+(\\.\\d{1,2})?";
        Pattern pattern = Pattern.compile(amountPattern);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return new BigDecimal(matcher.group().trim());
        }

        return null;
    }

    private int countLines(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int startIndex = text.indexOf("¡YAPEASTE!");
        if (startIndex == -1) {
            return 0;
        }

        String subText = text.substring(startIndex);
        String[] lines = subText.split("\n");
        return lines.length;
    }

    private String getDataFromThirdLine(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        String[] lines = text.split("\n");
        if (lines.length >= 3) {
            return lines[2];
        }

        return null;
    }

    private String findEightConsecutiveNumbers(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\d{8}");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }
}