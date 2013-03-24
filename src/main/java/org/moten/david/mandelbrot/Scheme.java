package org.moten.david.mandelbrot;

import static java.awt.Color.black;
import static java.awt.Color.blue;
import static java.awt.Color.green;
import static java.awt.Color.red;
import static java.awt.Color.white;
import static java.awt.Color.yellow;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Scheme {

	public static final List<Color> primary = new ArrayList<Color>() {
		{
			add(blue);
			add(white);
			add(yellow);
			add(black);
			add(green);
			add(white);
			add(red);
			add(black);
		}
	};

	public static final List<Color> wikipedia = new ArrayList<Color>() {
		{
			add(blue);
			add(white);
			add(yellow);
			add(black);
		}
	};

	public static final List<Color> buzzsaw = new ArrayList<Color>() {
		{
			// http://upload.wikimedia.org/wikipedia/commons/5/55/Fractal-zoom-1-03-Mandelbrot_Buzzsaw.ogg
			add(red);
			add(yellow);
			add(black);
			add(white);
			add(black);
			add(yellow);
		}
	};
	public static final List<Color> browny = new ArrayList<Color>() {
		{
			final Color brown = new Color(205, 183, 158);
			final Color salmon = new Color(250, 128, 114);
			add(brown);
			add(white);
			add(salmon);
			add(black);
		}
	};

}
