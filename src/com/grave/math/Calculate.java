package com.grave.math;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.FontUtils;

import com.grave.misc.Pair;

public class Calculate {
	/**
	 * Calculates the hypotenuse of the triangle between two points.
	 * @param src The source position.
	 * @param target The target position.
	 * @return The theta value representing the hypotenuse between the two points.
	 */
	public static float Hypotenuse(Pair<Float> src, Pair<Float> target) {
		return (float)Math.atan2((target.y - src.y), (target.x - src.x));
	}

	/**
	 * Calculates the distance between two points.
	 * @param src The source position.
	 * @param target The target position.
	 * @return The distance between the two points.
	 */
	public static float Distance(Pair<Float> src, Pair<Float> target) {
		float xD = (target.x - src.x);
		float yD = (target.y - src.y);
		return (float)Math.sqrt((xD * xD) + (yD * yD));
	}

	/**
	 * Faster distance comparison method that returns a value representing the relation between the calculated distance and the distance you're comparing it to.
	 * This is faster than the Calculate.Distance() method because it does not make use of Math.sqrt().
	 * @param src The source position.
	 * @param target The target position.
	 * @param comp The value to compare the distance between the source and target to.
	 * @return Returns -1 if the distance between the two points is less than comp, 1 if the distance is greater than comp, and 0 if the distance is equal to comp.
	 */
	public static int FastDistanceCompare(Pair<Float> src, Pair<Float> target, float comp) {
		float compSq = (comp * comp);
		float xD = (target.x - src.x);
		float yD = (target.y - src.y);
		float dist = ((xD * xD) + (yD * yD));

		if(dist < compSq) return -1;
		else if(dist > compSq) return 1;
		else return 0;
	}

	public static Pair<Float> rotateAboutPoint(Pair<Float> pivot, Pair<Float> point, float theta) {
		float x = (pivot.x + ((point.x - pivot.x) * (float)Math.cos(theta)) - ((point.y - pivot.y) * (float)Math.sin(theta)));
		float y = (pivot.y + ((point.x - pivot.x) * (float)Math.sin(theta)) + ((point.y - pivot.y) * (float)Math.cos(theta)));

		return new Pair<Float>(x, y);
	}

	/**
	 * Draws the given text using the given font to the graphics context and wraps
	 * the text according to the specified max width.
	 * @param g The graphics context to draw the text on.
	 * @param text The text to draw on the screen.
	 * @param font The font to render the text with.
	 * @param x The x-coordinate at which to draw the text.
	 * @param y The y-coordinate at which to draw the text.
	 * @param maxWidth The max width for the line before text wraps.
	 * @param center Whether or not to draw the text centered.
	 */
	public static void TextWrap(Graphics g, String text, Font font, float x, float y, float maxWidth, boolean center) {
		TextWrap(g, text, font, x, y, maxWidth, center, Color.white);
	}

	public static float TextHeight(String text, Font font, float maxWidth) {
		float height = 0.0f;
		String [] words = text.split(" ");
		String str = "";
		int i = 0;

		while(i < words.length) {
			boolean lineTerminate = false;

			String word = words[i];
			if(font.getWidth(str + word) <= maxWidth) {
				str += (word + " ");
				i++;
				if(i == words.length) lineTerminate = true;
			} else lineTerminate = true;

			if(lineTerminate) {
				str = "";
				height += font.getLineHeight();
			}
		}

		return height;
	}

	/**
	 * Draws the given text using the given font to the graphics context and wraps
	 * the text according to the specified max width. Draws the text with the specified color.
	 * @param g The graphics context to draw the text on.
	 * @param text The text to draw on the screen.
	 * @param font The font to render the text with.
	 * @param x The top-left x-coordinate at which to draw the text.
	 * @param y The top-left y-coordinate at which to draw the text.
	 * @param maxWidth The max width for the line before text wraps.
	 * @param center Whether or not to draw the text centered.
	 * @param color The color to use when rendering the text.
	 * @return Returns a float representing the size of the text field generated.
	 */
	public static float TextWrap(Graphics g, String text, Font font, float x, float y, float maxWidth, boolean center, Color color) {
		g.setColor(color);
		g.setFont(font);

		String [] words = text.split(" ");
		String str = "";
		int line = 0;
		int i = 0;
		float height = 0.0f;

		while(i < words.length) {
			boolean draw = false;

			String word = words[i];
			if(font.getWidth(str + word) <= maxWidth) {
				str += (word + " ");
				i++;
				if(i == words.length) draw = true;
			} else draw = true;

			if(draw) {
				str = str.trim();

				float cy = (y + (line * font.getLineHeight()));

				if(center) FontUtils.drawCenter(font, str, (int)x, (int)cy, (int)maxWidth, color);
				else g.drawString(str, x, cy);

				str = "";
				line++;
				height += font.getLineHeight();
			}
		}

		return height;
	}
}
