package com.midori.bot;

import org.apache.http.NameValuePair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class AICaptcha {


    static class BDCaptcha {
        public BufferedImage image;
        public String random;
        public String response;
    }


    public static boolean IsTrainedCaptcha(BufferedImage img) {
        int whitePixelCount = 0;
        int grayPixelCount = 0;
        int[] rgb;
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                rgb = getPixelData(img, i, j);
                if ((rgb[0] == rgb[1]) && (rgb[0] == rgb[2])) {
                    if (rgb[0] == 255) {
                        whitePixelCount++;
                    } else if (rgb[0] < 237) {
                        grayPixelCount++;
                    }
                }
            }
        }
        if ((whitePixelCount > 6300 && whitePixelCount < 13762) &&
                (grayPixelCount > 2055 && grayPixelCount < 3859)) {
            return true;
        } else {
            return false;
        }
    }

    private static int[] getPixelData(BufferedImage img, int x, int y) {
        int argb = img.getRGB(x, y);
        return new int[]{
                (argb >> 16) & 0xff, //red
                (argb >> 8) & 0xff, //green
                (argb) & 0xff  //blue
        };
    }
}
