package com.github.lenemarix.autoplay.dq11.poker.util;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

/**
 * キャプチャイメージの保存に関するユーティリティ。
 */
@Component
public class ImageUtil {

    public void imageToFile(BufferedImage image, Rectangle rectangle, String filepath)
            throws IOException {
        BufferedImage subImage = image.getSubimage(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
        imageToFile(subImage, filepath);
    }

    public void imageToFile(BufferedImage image, String filepath) throws IOException {
        File file = new File(filepath);
        file.mkdirs();

        ImageIO.write(image, "png", file);
    }

}
