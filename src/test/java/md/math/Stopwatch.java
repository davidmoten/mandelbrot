package md.math;

public class Stopwatch {

	private final long start;
	private long stop;

	public Stopwatch() {
		this.start = System.currentTimeMillis();
	}

	public void stop() {
		this.stop = System.currentTimeMillis();
	}

	public double getTimeSeconds() {
		return (stop - start) / 1000.0;
	}

	public String getTimeString() {
		return getTimeSeconds() + "s";
	}

}
