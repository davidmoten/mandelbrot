package md.math;

import java.math.BigDecimal;

/**
 * Times evaluating floating-point expressions using various extended precision
 * APIs.
 * 
 * @author Martin Davis
 * 
 */
public class FPExpressionTime {

	public static void main(String[] args) throws Exception {
		FPExpressionTime test = new FPExpressionTime();
		test.run();
	}

	public FPExpressionTime() {
	}

	public void run() {
		double doubleTime = runDouble(10000000);
		double ddTime = runDoubleDouble(10000000);
		// double ddSelfTime = runDoubleDoubleSelf(10000000);
		double bigDecTime = runBigDecimal(10000000);

		System.out.println("BigDecimal VS double performance factor = "
				+ bigDecTime / doubleTime);
		System.out.println("DoubleDouble VS double performance factor = "
				+ ddTime / doubleTime);
		// System.out.println("DoubleDoubleSelf VS double performance factor = "
		// + ddSelfTime/doubleTime);

		System.out.println("DoubleDouble VS BigDouble performance factor = "
				+ ddTime / bigDecTime);
	}

	public double runDouble(int nIter) {
		double a = 9.0;
		double factor = 10.0;
		Stopwatch sw = new Stopwatch();
		for (int i = 0; i < nIter; i++) {

			double aMul = factor * a;
			double aDiv = a / factor;

			double det = a * a - aMul * aDiv;
			// System.out.println(det);
		}
		sw.stop();
		System.out.println("double:          nIter = " + nIter + "   time = "
				+ sw.getTimeString());
		return sw.getTimeSeconds() / nIter;
	}

	public double runBigDecimal(int nIter) {
		BigDecimal a = (new BigDecimal(9.0)).setScale(20);
		BigDecimal factor = (new BigDecimal(10.0)).setScale(20);
		Stopwatch sw = new Stopwatch();
		for (int i = 0; i < nIter; i++) {

			BigDecimal aMul = factor.multiply(a);
			BigDecimal aDiv = a.divide(factor, BigDecimal.ROUND_HALF_UP);

			BigDecimal det = a.multiply(a).subtract(aMul.multiply(aDiv));
			// System.out.println(aDiv);
			// System.out.println(det);
		}
		sw.stop();
		System.out.println("BigDecimal:      nIter = " + nIter + "   time = "
				+ sw.getTimeString());
		return sw.getTimeSeconds() / nIter;
	}

	public double runDoubleDouble(int nIter) {
		DoubleDouble a = new DoubleDouble(9.0);
		DoubleDouble factor = new DoubleDouble(10.0);
		Stopwatch sw = new Stopwatch();
		for (int i = 0; i < nIter; i++) {

			DoubleDouble aMul = factor.multiply(a);
			DoubleDouble aDiv = a.divide(factor);

			DoubleDouble det = a.multiply(a).subtract(aMul.multiply(aDiv));
			// System.out.println(aDiv);
			// System.out.println(det);
		}
		sw.stop();
		System.out.println("DoubleDouble:    nIter = " + nIter + "   time = "
				+ sw.getTimeString());
		return sw.getTimeSeconds() / nIter;
	}

	/*
	 * public double runDoubleDoubleSelf(int nIter) { DoubleDouble a = new
	 * DoubleDouble(9.0); DoubleDouble factor = new DoubleDouble(10.0);
	 * Stopwatch sw = new Stopwatch(); for (int i = 0; i < nIter; i++) {
	 * 
	 * DoubleDouble c = new DoubleDouble(9.0); c.selfMultiply(factor);
	 * DoubleDouble b = new DoubleDouble(9.0); b.selfDivide(factor);
	 * 
	 * DoubleDouble a2 = new DoubleDouble(a); a2.selfMultiply(a); DoubleDouble
	 * b2 = new DoubleDouble(b); b2.selfMultiply(c); a2.selfDivide(b2);
	 * DoubleDouble det = a2; // System.out.println(aDiv); //
	 * System.out.println(det); } sw.stop();
	 * System.out.println("DoubleDoubleSelf:nIter = " + nIter + "   time = " +
	 * sw.getTimeString()); return sw.getTime() / (double) nIter; }
	 */

}