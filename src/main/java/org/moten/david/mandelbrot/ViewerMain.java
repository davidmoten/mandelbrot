package org.moten.david.mandelbrot;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class ViewerMain {
	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 1));

		final MandelbrotFractal fractal = new MandelbrotFractal(256, 1,
				Scheme.buzzsaw, new BigDecimal("-3.5"), new BigDecimal("-2.5"),
				new BigDecimal("1.5"), new BigDecimal("2.5"));
		fractal.setPreferredSize(new Dimension(500, 500));
		Runnable onRedraw = new Runnable() {
			int number = 0;

			public void run() {
				BufferedImage image = fractal.getImage();
				FileOutputStream fos;
				try {
					fos = new FileOutputStream("target/m" + (++number) + ".png");
					ImageIO.write(image, "png", fos);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		fractal.setOnImageRedraw(onRedraw);
		frame.getContentPane().add(fractal);
		frame.pack();
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				frame.setVisible(true);

			}
		});

		// http://fractaljourney.blogspot.com/2010/01/mandelbrot-ultra-zoom-5-21e275.html
	}
}
