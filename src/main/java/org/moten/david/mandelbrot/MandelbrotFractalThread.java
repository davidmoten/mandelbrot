package org.moten.david.mandelbrot;

import static md.math.DoubleDouble.valueOf;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import md.math.DoubleDouble;

public class MandelbrotFractalThread extends Thread {
	private static final double MODULUS_THRESHOLD = 4;
	int k; // id number of this thread
	private final int numThreads;
	private final int[] pix;
	private final int w;
	private final int h;
	private final BigDecimal xa;
	private final BigDecimal ya;
	private final BigDecimal xb;
	private final BigDecimal yb;
	private final int alpha;
	private final int maxIterations = 1024;
	private boolean keepGoing = true;
	private final List<RGB> colors = new ArrayList<RGB>();;

	MandelbrotFractalThread(int maxIterations, int k, int maxThr, int[] pix,
			int w, int h, BigDecimal xa, BigDecimal ya, BigDecimal xb,
			BigDecimal yb, int alpha, List<Color> colors) {
		this.k = k;
		this.numThreads = maxThr;
		this.pix = pix;
		this.w = w;
		this.h = h;
		this.xa = xa;
		this.ya = ya;
		this.xb = xb;
		this.yb = yb;
		this.alpha = alpha;
		for (Color c : colors) {
			float[] f = c.getRGBColorComponents(null);
			this.colors.add(new RGB(f[0], f[1], f[2]));
		}
		this.numSteps = colors.size();
		this.step = 1f / numSteps;

	}

	@Override
	public void interrupt() {
		keepGoing = false;
		super.interrupt();
	}

	// @Override
	public void runFast() {
		int imax = w * h;

		double xb = this.xb.doubleValue();
		double xa = this.xa.doubleValue();
		double ya = this.ya.doubleValue();
		double yb = this.yb.doubleValue();
		// Each thread only calculates its own share of pixels!
		for (int i = k; i < imax && keepGoing; i += numThreads) {
			int kx = i % w;
			int ky = (i - kx) / w;
			double a = (double) kx / w * (xb - xa) + xa;
			double b = (double) ky / h * (yb - ya) + ya;
			double x = a;
			double y = b;
			int v = 0;
			pix[w * ky + kx] = (alpha << 24) | (v << 16) | (v << 8) | v;

			for (int kc = 0; kc < maxIterations && keepGoing; kc++) {
				double x2 = x * x;
				double y2 = y * y;
				double x0 = x2 - y2 + a;
				y = 2 * x * y + b;
				x = x0;
				double modulus = x2 + y2;
				if (modulus > MODULUS_THRESHOLD) {
					pix[w * ky + kx] = getColor(kc, modulus);
					break;
				}
			}
		}
		if (!keepGoing)
			System.out.println("cancelled");
	}

	@Override
	public void run() {
		runAccurate();
	}

	public void runAccurate() {
		int imax = w * h;

		DoubleDouble xa = new DoubleDouble(this.xa.toPlainString());
		DoubleDouble xb = new DoubleDouble(this.xb.toPlainString());
		DoubleDouble ya = new DoubleDouble(this.ya.toPlainString());
		DoubleDouble yb = new DoubleDouble(this.yb.toPlainString());
		// Each thread only calculates its own share of pixels!
		for (int i = k; i < imax && keepGoing; i += numThreads) {
			int kx = i % w;
			int ky = (i - kx) / w;
			// double a = (double) kx / w * (xb - xa) + xa;
			DoubleDouble a = valueOf(kx).divide(valueOf(w))
					.multiply(xb.subtract(xa)).add(xa);
			// double b = (double) ky / h * (yb - ya) + ya;
			DoubleDouble b = valueOf(ky).divide(valueOf(h))
					.multiply(yb.subtract(ya)).add(ya);
			DoubleDouble x = a;
			DoubleDouble y = b;
			int v = 0;
			pix[w * ky + kx] = (alpha << 24) | (v << 16) | (v << 8) | v;

			for (int kc = 0; kc < maxIterations && keepGoing; kc++) {
				// double x2 = x * x;
				DoubleDouble x2 = x.multiply(x);
				// double y2 = y * y;
				DoubleDouble y2 = y.multiply(y);
				// double x0 = x2 - y2 + a;
				DoubleDouble x0 = x2.subtract(y2).add(a);
				// y = 2 * x * y + b;
				y = valueOf(2).multiply(x).multiply(y).add(b);
				// x = x0;
				x = x0;
				// double modulus = x2 + y2;
				double modulus = x2.doubleValue() + y2.doubleValue();
				if (modulus > MODULUS_THRESHOLD) {
					pix[w * ky + kx] = getColor(kc, modulus);
					break;
				}
			}
		}
		if (!keepGoing)
			System.out.println("cancelled");
	}

	private static class RGB {
		static ThreadLocal<RGB> threadLocal = new ThreadLocal<RGB>() {

			@Override
			protected RGB initialValue() {
				return new RGB(0, 0, 0);
			}
		};

		static RGB get() {
			return threadLocal.get();
		}

		void move(RGB a, RGB b, float proportion) {
			red = a.red + (b.red - a.red) * proportion;
			green = a.green + (b.green - a.green) * proportion;
			blue = a.blue + (b.blue - a.blue) * proportion;
		}

		float red;
		float green;
		float blue;

		public RGB(float red, float green, float blue) {
			super();
			this.red = red;
			this.green = green;
			this.blue = blue;
		}
	}

	private final float numSteps;
	private final float step;

	/**
	 * From http://linas.org/art-gallery/escape/escape.html, corrected using
	 * http://en.wikipedia.org/wiki/Mandelbrot_set
	 * 
	 * @param iteration
	 * @param x
	 * @param y
	 * @return
	 */
	private int getColor(int iteration, double zModulus) {
		float v = (float) (iteration - Math.log(Math.log(zModulus)
				/ Math.log(4))
				/ Math.log(2));
		if (v < 0)
			v = 0;
		float p = v / maxIterations;

		RGB color = RGB.get();
		float threshold = step;

		for (int i = 0; i < numSteps; i++) {
			if (p <= threshold) {
				RGB toColor;
				if (i == numSteps - 1)
					toColor = colors.get(0);
				else
					toColor = colors.get(i + 1);
				color.move(colors.get(i), toColor, (p - threshold + step)
						* numSteps);
				return new Color(color.red, color.green, color.blue).getRGB();
			}
			threshold += step;
		}
		throw new RuntimeException("should not get to here");

	}

	private int getColor(int iteration) {
		// various color palettes can be created here!
		int red = 255 - (iteration % 16) * 16;
		int green = (16 - iteration % 16) * 16;
		int blue = (iteration % 16) * 16;

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	private int getColor2(int iteration) {
		// various color palettes can be created here!
		final int MAX_COLOR = 255;
		int red;
		int green = 0;
		int blue = 0;
		if (iteration < maxIterations / 2) {
			red = 2 * iteration * MAX_COLOR / maxIterations;
		} else {
			iteration -= maxIterations / 2;
			red = 255;
			green = blue = 2 * iteration * MAX_COLOR / maxIterations;
		}
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}
}
