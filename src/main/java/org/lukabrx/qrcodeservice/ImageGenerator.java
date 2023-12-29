package org.lukabrx.qrcodeservice;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageGenerator {
    public BufferedImage createWhiteImage(int width, int height) {
        BufferedImage image = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0,0, width, height);
        graphics.dispose();

        return image;
    }
}

