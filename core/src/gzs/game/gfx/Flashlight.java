package gzs.game.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import gzs.entities.Player;
import gzs.game.misc.Pair;

public class Flashlight {
	private static final Color COLOR = Color.WHITE;
	private static final double INTENSITY = 400.0;
	private static final double BEAM_WIDTH = 40.0;
	
	private double [] xPoints;
	private double [] yPoints;
	private Pair<Double> origin;
	private double theta;
	
	public Flashlight() {
		this.xPoints = new double[] { 0.0, 0.0, 0.0 };
		this.yPoints = new double[] { 0.0, 0.0, 0.0 };
		this.origin = new Pair<Double>(0.0, 0.0);
		this.theta = 0.0;
	}
	
	public void update(Player player, long cTime) {
		origin.x = player.getPosition().x;
		origin.y = player.getPosition().y;
		theta = player.getDoubleAttribute("theta");
		
		xPoints[0] = origin.x;
		yPoints[0] = origin.y;
		
		xPoints[1] = (origin.x + (Flashlight.INTENSITY * Math.cos(theta - Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		yPoints[1] = (origin.y + (Flashlight.INTENSITY * Math.sin(theta - Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		
		xPoints[2] = (origin.x + (Flashlight.INTENSITY * Math.cos(theta + Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
		yPoints[2] = (origin.y + (Flashlight.INTENSITY * Math.sin(theta + Math.toRadians((Flashlight.BEAM_WIDTH / 2)))));
	}

	public void render(SpriteBatch batch, Player player, long cTime) {
		// TODO: Replace with LibGDX polygon drawing method.
	}
}