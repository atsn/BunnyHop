package ie.wit.cgd.bunnyhop.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ie.wit.cgd.bunnyhop.game.Assets;

public class Fox extends AbstractGameObject
{

	public static final String TAG = Fox.class.getName();

	public enum VIEW_DIRECTION
	{
		LEFT, RIGHT
	}

	private TextureRegion regFox;
	public VIEW_DIRECTION viewDirection;
	public float timeJumping;
	public boolean isdead;

	public Fox()
	{
		init();
	}

	public void update(float deltaTime)
	{
		super.update(deltaTime);

		if (viewDirection == VIEW_DIRECTION.LEFT)
		{
			velocity.x = -terminalVelocity.x / 4;
		}
		else velocity.x = terminalVelocity.x / 4;
	}

	public void ChangeDirection()
	{

		if (viewDirection == VIEW_DIRECTION.LEFT)
		{
			viewDirection = VIEW_DIRECTION.RIGHT;
		}
		else
		{
			viewDirection = VIEW_DIRECTION.LEFT;

		}
	}

	public void init()
	{
		dimension.set(0.5f, 0.5f);
		regFox = Assets.instance.fox.fox;

		origin.set(dimension.x / 2, dimension.y / 2); // Center image on game
														// object

		bounds.set(0, 0, dimension.x, dimension.y); // Bounding box for
													// collision detection

		terminalVelocity.set(3.0f, 4.0f); // Set physics values
		friction.set(12.0f, 0.0f);
		acceleration.set(0.0f, -25.0f);

		viewDirection = VIEW_DIRECTION.RIGHT; // View direction

		isdead = false;
	}

	public int getscore()
	{
		return 500;
	}

	@Override
	public void render(SpriteBatch batch)
	{
		if (isdead) return;
		TextureRegion reg = null;
		reg = regFox;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
				reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.RIGHT, false);

	}

}
