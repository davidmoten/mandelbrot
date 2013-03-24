package org.moten.david.mandelbrot;

/** {{{ http://code.activestate.com/recipes/577158/ (r1) */
// <applet code="MandelbrotFractal" width=800 height=600></applet>
// MandelbrotFractal.java (FB - 201003276)
// N Multi-threaded!

import static java.math.BigDecimal.valueOf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class MandelbrotFractal extends JPanel {

	final int numThreads = 10; // number of threads to run

	// drawing area (must be xa<xb and ya<yb)
	// private BigDecimal xa = valueOf(-2.0);
	// BigDecimal xb = valueOf(1.0);
	// BigDecimal ya = valueOf(-1.5);
	// BigDecimal yb = valueOf(1.5);

	private BufferedImage image = null;

	private final int maxIterations;

	private final int resolutionFactor;

	private final List<MandelbrotFractalThread> threads = new ArrayList<MandelbrotFractalThread>();

	private final List<Color> colors;

	private Runnable onImageRedraw;

	private BigDecimal xa;

	private BigDecimal ya;

	private BigDecimal xb;

	private BigDecimal yb;

	public void setOnImageRedraw(Runnable onImageRedraw) {
		this.onImageRedraw = onImageRedraw;
	}

	public BufferedImage getImage() {
		return image;
	}

	public MandelbrotFractal(int maxIterations, int resolutionFactor,
			List<Color> colors, BigDecimal xa, BigDecimal ya, BigDecimal xb,
			BigDecimal yb) {
		this.maxIterations = maxIterations;
		this.resolutionFactor = resolutionFactor;
		this.colors = colors;
		this.xa = xa;
		this.ya = ya;
		this.xb = xb;
		this.yb = yb;
		setPreferredSize(new Dimension(1000, 700));
		addMouseListener(createMouseListener());
	}

	private MouseListener createMouseListener() {
		return new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				double propX = (double) e.getPoint().x / getSize().width;
				double propY = (double) e.getPoint().y / getSize().height;
				double zoomFactor = 1000;
				{
					BigDecimal newDiffX = xb.subtract(xa).divide(
							valueOf(zoomFactor));
					xa = xb.subtract(xa).multiply(valueOf(propX)).add(xa)
							.subtract(newDiffX.divide(valueOf(2)));
					xb = xa.add(newDiffX);
				}
				{
					BigDecimal newDiffY = yb.subtract(ya).divide(
							valueOf(zoomFactor));
					ya = yb.subtract(ya).multiply(valueOf(propY)).add(ya)
							.subtract(newDiffY.divide(valueOf(2)));
					yb = ya.add(newDiffY);
				}
				redraw();
			}
		};
	}

	private void redraw() {
		this.image = paintFractal(numThreads, maxIterations, resolutionFactor
				* getSize().width, resolutionFactor * getSize().height, xa, ya,
				xb, yb, colors);
		repaint();
		if (onImageRedraw != null)
			onImageRedraw.run();
	}

	public static BufferedImage paintFractal(int numThreads, int maxIterations,
			int w, int h, BigDecimal xa, BigDecimal ya, BigDecimal xb,
			BigDecimal yb, List<Color> colors) {
		int alpha = 255;
		int[] pix = new int[w * h];
		System.out.println("painting (" + xa + "," + ya + "," + xb + "," + yb
				+ ")");

		FractalMonitorThread monitor = paintFractal(numThreads, maxIterations,
				pix, w, h, xa, ya, xb, yb, alpha, colors);
		try {
			monitor.join();
		} catch (InterruptedException e) {
			// do nothing
		}
		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, w, h, pix, 0, w);
		return image;
		// return createImage(new MemoryImageSource(w, h, pix, 0, w));
	}

	@Override
	public void paintComponent(Graphics g) {
		System.out.println("painting component " + getSize());
		if (image == null)
			redraw();
		else
			g.drawImage(image, 0, 0, getSize().width, getSize().height, this);
	}

	private static FractalMonitorThread paintFractal(int numThreads,
			int maxIterations, final int[] pix, final int w, final int h,
			BigDecimal xa, BigDecimal ya, BigDecimal xb, BigDecimal yb,
			int alpha, List<Color> colors) {

		long startTime = System.currentTimeMillis();
		List<MandelbrotFractalThread> threads = new ArrayList<MandelbrotFractalThread>();
		for (int i = 0; i < numThreads; i++) {
			MandelbrotFractalThread thread = new MandelbrotFractalThread(
					maxIterations, i, numThreads, pix, w, h, xa, ya, xb, yb,
					alpha, colors);
			threads.add(thread);
			thread.start();
		}
		FractalMonitorThread monitor = new FractalMonitorThread(startTime,
				threads);
		monitor.start();
		return monitor;
	}

}
/** end of http://code.activestate.com/recipes/577158/ }}} */
