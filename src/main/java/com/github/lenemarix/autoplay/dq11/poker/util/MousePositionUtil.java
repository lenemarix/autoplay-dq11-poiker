package com.github.lenemarix.autoplay.dq11.poker.util;

import java.awt.MouseInfo;
import java.awt.PointerInfo;

/**
 * マウスポインタの位置を定期的に標準出力に出力する。 開発中の補助用。
 */
public class MousePositionUtil {

    /** マウスポインタの位置を出力する間隔 (ミリ秒) */
    private static final int SLEEP_MS = 1000;

    public static void main(String[] args) {
        while (true) {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            System.out.println("Location of Mouse : " + pointerInfo.getLocation());

            try {
                Thread.sleep(SLEEP_MS);
            } catch (InterruptedException ex) {
                break;
            }
        }

    }

}
