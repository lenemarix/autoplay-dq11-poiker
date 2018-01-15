package com.github.lenemarix.autoplay.dq11.poker.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 画像の比較を行うクラス。
 */
@Component
@ConfigurationProperties(prefix = "autoplay.dq11.poker.image-comparetor")
public class ImageComparator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageComparator.class);

    private int minDiff;
    private int maxDiff;

    /**
     * 画像イメージと画像ファイルの比較を行う。
     * 
     * @param filepath 比較対象となる画像ファイルのパス。
     * @param image 比較対象となる画像イメージ。
     * @return 一致していればtrue。それ以外はfalse。
     * @throws IOException 画像ファイルが読み込めなかった場合。
     */
    public boolean compare(String filepath, BufferedImage image) throws IOException {
        BufferedImage original = ImageIO.read(new File(filepath));
        return compare(original, image);
    }

    /**
     * 画像イメージと画像ファイルの比較を行う。
     * 
     * @param read1 比較対象となる画像イメージ。
     * @param read2 比較対象となる画像イメージ。
     * @return 一致していればtrue。それ以外はfalse。
     */
     public boolean compare(BufferedImage read1, BufferedImage read2) {
        int w1 = read1.getWidth();
        int h1 = read1.getHeight();

        int w2 = read2.getWidth();
        int h2 = read2.getHeight();

        if (w1 != w2 || h1 != h2) {
            throw new IllegalArgumentException("image size is different.");
        }

        int correctCount = 0;
        int warnningCount = 0;
        int irregularCount = 0;
        int rgbSum = 0;

        for (int y = 0; y < h1; y++) {
            for (int x = 0; x < w1; x++) {

                Color color1 = new Color(read1.getRGB(x, y));
                Color color2 = new Color(read2.getRGB(x, y));

                int r = color1.getRed() - color2.getRed();
                int g = color1.getGreen() - color2.getGreen();
                int b = color1.getBlue() - color2.getBlue();

                int rgb = Math.abs(r) + Math.abs(g) + Math.abs(b);
                rgbSum = rgbSum + rgb;

                if (rgb <= minDiff) {
                    correctCount++;
                } else if (maxDiff <= rgb) {
                    irregularCount++;
                } else {
                    warnningCount++;
                }
            }
        }

        LOGGER.debug("correctCount = {} / warnningCount = {} / irregularCount = {} / rgbSum = {}", 
                correctCount, warnningCount, irregularCount, rgbSum);

        return irregularCount == 0;

    }

    public int getMinDiff() {
        return minDiff;
    }

    public void setMinDiff(int minDiff) {
        this.minDiff = minDiff;
    }

    public int getMaxDiff() {
        return maxDiff;
    }

    public void setMaxDiff(int maxDiff) {
        this.maxDiff = maxDiff;
    }
}
