package com.consulta.consultamedica.service;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class ImageProcessingService {

    public BufferedImage improveContrast(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage contrastImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                int gray = (red + green + blue) / 3;
                int contrast = (int) (((((gray / 255.0) - 0.5) * 5) * 255));

                if (contrast < 0) contrast = 0;
                if (contrast > 255) contrast = 255;

                int newPixel = 0xFF000000 | (contrast << 16) | (contrast << 8) | contrast;
                contrastImage.setRGB(x, y, newPixel);
            }
        }

        return contrastImage;
    }
}
