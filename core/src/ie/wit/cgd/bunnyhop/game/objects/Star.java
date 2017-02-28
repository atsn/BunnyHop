package ie.wit.cgd.bunnyhop.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ie.wit.cgd.bunnyhop.game.Assets;

public class Star extends AbstractGameObject {
    private TextureRegion   regStar;
    public boolean          collected;

    public Star() {
        init();
    }

    private void init() {
        dimension.set(0.5f, 0.5f);
        regStar = Assets.instance.star.star;
        // Set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        collected = false;
    }

    public void render(SpriteBatch batch) {
        if (collected) return;
        TextureRegion reg = null;
        reg = regStar;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
                scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);
    }

    public int getScore() {
        return 680;
    }

}
