package com.consulta.consultamedica;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfig {
    @Bean
    Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata"); //files of the example : https://github.com/tesseract-ocr/tessdata
        tesseract.setLanguage("spa");
        //tesseract.setTessVariable("tessedit_char_whitelist", "0123456789-.");
        tesseract.setTessVariable("preserve_interword_spaces", "1");
        tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_AUTO);
        tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY); // Sólo usa el modelo LSTM (redes neuronales)
        tesseract.setTessVariable("load_system_dawg", "F"); // Desactivar ciertos diccionarios para mejorar rendimiento
        tesseract.setTessVariable("load_freq_dawg", "F"); // Desactivar ciertos diccionarios para mejorar rendimiento



        return tesseract;
    }
}
