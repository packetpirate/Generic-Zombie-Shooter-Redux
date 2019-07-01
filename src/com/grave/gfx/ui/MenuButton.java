package com.gzsr.gfx.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;

import com.gzsr.AssetManager;
import com.gzsr.gfx.Camera;
import com.gzsr.misc.Pair;

public class MenuButton extends Button {
	private static final Color DEFAULT_TEXT = new Color(0xFFFFFF);
	private static final Color DEFAULT_HOVER = new Color(0xABCDEF);
	private static final String FONT_NAME = "PressStart2P-Regular_large";

	private boolean useCamOff;

	public MenuButton(Pair<Float> position_, String text_) {
		this(position_, text_, null);
	}

	public MenuButton(Pair<Float> position_, String text_, String image_) {
		this(position_, text_, image_, false);
	}

	public MenuButton(Pair<Float> position_, String text_, String image_, boolean useCamOff_) {
		super();

		this.image = image_;
		this.position = position_;
		this.text = text_;
		this.useCamOff = useCamOff_;
	}

	@Override
	public void render(Graphics g, long cTime) {
		Image img = AssetManager.getManager().getImage(image);

		float x = position.x;
		float y = position.y;
		if(useCamOff) {
			x += Camera.getCamera().getOffset().x;
			y += Camera.getCamera().getOffset().y;
		}

		if(img == null) {
			// Draw the text representing the button.
			UnicodeFont fnt = AssetManager.getManager().getFont(FONT_NAME);
			Color color = mouseOver() ? DEFAULT_HOVER : DEFAULT_TEXT;

			g.setColor(color);
			g.setFont(fnt);
			g.drawString(text, x, y);
		} else g.drawImage(img, x, y);
	}

	@Override
	public void click() {
		click(true);
	}

	@Override
	public void click(boolean left) {

	}

	public float getWidth() {
		UnicodeFont large = AssetManager.getManager().getFont(FONT_NAME);
		float textWidth = large.getWidth(text);
		if(image == null) return textWidth;

		Image img = AssetManager.getManager().getImage(image);
		return Math.max(img.getWidth(), textWidth);
	}

	@Override
	public boolean inBounds(float x, float y) {
		UnicodeFont fnt = AssetManager.getManager().getFont(FONT_NAME);

		float w = fnt.getWidth(text);
		float h = fnt.getHeight(text);
		float px = position.x;
		float py = position.y;

		if(useCamOff) {
			px += Camera.getCamera().getOffset().x;
			py += Camera.getCamera().getOffset().y;
		}

		return ((x > px) && (y > py) && (x < (px + w)) && (y < (py + h)));
	}
}