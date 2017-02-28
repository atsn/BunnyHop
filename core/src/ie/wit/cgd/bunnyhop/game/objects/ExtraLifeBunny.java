package ie.wit.cgd.bunnyhop.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ie.wit.cgd.bunnyhop.game.Assets;

public class ExtraLifeBunny extends AbstractGameObject
{
	private TextureRegion regExtralifebunny;
	public boolean collected;

	public ExtraLifeBunny()
	{
		init();
	}

	private void init()
	{
		dimension.set(0.5f, 0.5f);
		regExtralifebunny = Assets.instance.extraLifeBunny.head;
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;

	}

	@Override
	public void render(SpriteBatch batch)
	{
		if (collected) return;
		TextureRegion reg = null;
		reg = regExtralifebunny;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
				reg.getRegionHeight(), false, false);

	}
}