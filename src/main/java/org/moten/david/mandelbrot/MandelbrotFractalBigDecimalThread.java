package org.moten.david.mandelbrot;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MandelbrotFractalBigDecimalThread extends Thread {
	int k; // id number of this thread
	private final int maxThr;
	private final int[] pix;
	private final int w;
	private final int h;
	private final BigDecimal xa;
	private final BigDecimal ya;
	private final BigDecimal xb;
	private final BigDecimal yb;
	private final int alpha;
	private final int maxIterations = 256;
	private final int SCALE = 100;

	MandelbrotFractalBigDecimalThread(int k, int maxThr, int[] pix, int w,
			int h, BigDecimal xa, BigDecimal ya, BigDecimal xb, BigDecimal yb,
			int alpha) {
		this.k = k;
		this.maxThr = maxThr;
		this.pix = pix;
		this.w = w;
		this.h = h;
		this.xa = xa;
		this.ya = ya;
		this.xb = xb;
		this.yb = yb;
		this.alpha = alpha;
	}

	@Override
	public void run() {
		int imax = w * h;

		int[] palette = new int[maxIterations];
		for (int j = 0; j < palette.length; j++)
			palette[j] = getColor(j);

		BigDecimal diffX = xb.subtract(xa);
		BigDecimal diffY = yb.subtract(ya);
		BigDecimal four = BigDecimal.valueOf(4);
		// Each thread only calculates its own share of pixels!
		for (int i = k; i < imax; i += maxThr) {
			if (i % 1000 == 0)
				System.out.println(i + "/" + imax);
			int kx = i % w;
			int ky = (i - kx) / w;
			// double a = (double) kx / w * (xb - xa) + xa;
			BigDecimal a = valueOf(kx)
					.divide(valueOf(w), SCALE, RoundingMode.HALF_UP)
					.multiply(diffX).add(xa);
			// double b = (double) ky / h * (yb - ya) + ya;
			BigDecimal b = valueOf(ky)
					.divide(valueOf(h), SCALE, RoundingMode.HALF_UP)
					.multiply(diffY).add(ya);
			BigDecimal x = a;
			BigDecimal y = b;
			int v = 255;
			pix[w * ky + kx] = (alpha << 24) | (v << 16) | (v << 8) | v;

			for (int kc = 0; kc < maxIterations; kc++) {
				if (i == 6263)
					System.out.println("iteration " + kc);

				BigDecimal x2 = x.multiply(x);
				BigDecimal y2 = y.multiply(y);
				BigDecimal x0 = x2.subtract(y2).add(a);
				// BigDecimal x0 = x.pow(2).subtract(y.pow(2)).add(a);
				y = valueOf(2).multiply(x).multiply(y).add(b);
				// y = valueOf(2).multiply(x).multiply(y).add(b);
				x = x0;

				if (x2.add(y2).compareTo(four) > 0) {
					pix[w * ky + kx] = palette[kc];
					break;
				}
			}
		}
	}

	private int getColor(int iteration) {
		final int modulus = 16;
		// various color palettes can be created here!
		int red = 255 - (iteration % modulus) * modulus;
		int green = (modulus - iteration % modulus) * modulus;
		int blue = (iteration % modulus) * modulus;
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

}
