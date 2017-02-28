package ie.wit.cgd.bunnyhop.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ie.wit.cgd.bunnyhop.game.Assets;

public class Ladder extends AbstractGameObject
{
	private TextureRegion regLadder;

	public Ladder()
	{
		init();
	}

	private void init()
	{
		dimension.set(0.5f, 0.5f);
		regLadder = Assets.instance.ladder.ladder;
		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
	}

	public void render(SpriteBatch batch)
	{
		float relY = 0;

		TextureRegion reg = null;
		
		reg = regLadder;
		relY -= (dimension.y)-0.009; 

		batch.draw(reg.getTexture(), position.x , position.y +0.2f + relY, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);

	}
}