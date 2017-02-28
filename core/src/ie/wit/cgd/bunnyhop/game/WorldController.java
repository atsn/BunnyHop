package ie.wit.cgd.bunnyhop.game;

import com.badlogic.gdx.Application.ApplicationType;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.ShortMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import ie.wit.cgd.bunnyhop.util.CameraHelper;
import com.badlogic.gdx.utils.Array;
import ie.wit.cgd.bunnyhop.game.objects.Rock;
import ie.wit.cgd.bunnyhop.game.objects.Star;
import ie.wit.cgd.bunnyhop.util.Constants;
import ie.wit.cgd.bunnyhop.util.Objectives;
import ie.wit.cgd.bunnyhop.util.Objectives.ObjectiveType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import ie.wit.cgd.bunnyhop.BunnyHopMain;
import ie.wit.cgd.bunnyhop.game.objects.BunnyHead;
import ie.wit.cgd.bunnyhop.game.objects.BunnyHead.JUMP_STATE;
import ie.wit.cgd.bunnyhop.game.objects.Carrot;
import ie.wit.cgd.bunnyhop.game.objects.ExtraLifeBunny;
import ie.wit.cgd.bunnyhop.game.objects.Feather;
import ie.wit.cgd.bunnyhop.game.objects.Fire;
import ie.wit.cgd.bunnyhop.game.objects.Fox;
import ie.wit.cgd.bunnyhop.game.objects.Fox.VIEW_DIRECTION;
import ie.wit.cgd.bunnyhop.game.objects.Goal;
import ie.wit.cgd.bunnyhop.game.objects.GoldCoin;
import ie.wit.cgd.bunnyhop.game.objects.Ladder;

public class WorldController extends InputAdapter
{

	private static final String TAG = WorldController.class.getName();
	public CameraHelper cameraHelper;
	public Level level;
	public int lives;
	public int score;
	public int liveslost = 0;
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();
	private float timeLeftGameOverDelay;
	private float timesinsstart;
	public int levelnow = 1;
	public boolean ishit;
	public boolean livecollected;
	private Array<Vector2> collectedpossition;
	public Objectives objectiv;
	public boolean forcedMovement;
	private float forcedcameraspeed;
	boolean hitLeftEdge;
	boolean islastlevel;
	BunnyHopMain main = new BunnyHopMain();
	ControllerEventListener xbox = new ControllerEventListener()
	{

		@Override
		public void controlChange(ShortMessage event)
		{
			// TODO Auto-generated method stub

		}
	};

	public boolean isGameOver()
	{
		return lives <= 0;
	}

	public boolean isjuststartet()
	{
		return timesinsstart > 0;
	}

	public boolean isGameWon()
	{
		return level.goal.collected && objectiv.ObjectiveMet();
	}

	public boolean isPlayerInWater()
	{
		return level.bunnyHead.position.y < -5;
	}

	private void handleInputGame(float deltaTime)
	{

		if (cameraHelper.hasTarget(level.bunnyHead) && !forcedMovement)
		{

			// Player Movement
			if (Gdx.input.isKeyPressed(Keys.LEFT))
			{
				level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
			}
			else if (Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
			}
			else
			{
				// Execute auto-forward movement on non-desktop platform
				if (Gdx.app.getType() != ApplicationType.Desktop)
				{
					level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
				}
			}
		}

		if (forcedMovement)
		{
			level.bunnyHead.velocity.x = forcedcameraspeed;
		}

		// Bunny Jump
		if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) level.bunnyHead.setJumping(true);
		else
		{
			level.bunnyHead.setJumping(false);
		}
	}

	private void onCollisionBunnyHeadWithRock(Rock rock)
	{
		BunnyHead bunnyHead = level.bunnyHead;
		float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height));
		if (heightDifference > 0.25f)
		{
			hitLeftEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);

			if (hitLeftEdge)
			{
				bunnyHead.position.x = rock.position.x + rock.bounds.width;
			}

			else
			{
				bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
			}

			return;
		}

		switch (bunnyHead.jumpState)
		{
		case GROUNDED:
			break;
		case FALLING:
		case JUMP_FALLING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
			bunnyHead.jumpState = JUMP_STATE.GROUNDED;
			break;
		case JUMP_RISING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
			break;
		}
	}

	private void onCollisionBunnyWithGoldCoin(GoldCoin goldcoin)
	{
		goldcoin.collected = true;
		score += goldcoin.getScore();
		Gdx.app.log(TAG, "Gold coin collected");
		if (objectiv.isobectivetype(ObjectiveType.COLECT_COINS))
		{
			objectiv.parameterCounter++;
		}
	};

	private void onCollisionBunnyWithFeather(Feather feather)
	{
		feather.collected = true;
		score += feather.getScore();
		level.bunnyHead.setFeatherPowerup(true);
		Gdx.app.log(TAG, "Feather collected");
	};

	private void onCollisionBunnyWithGoal(Goal goal)
	{
		goal.collected = true;
		level.goal = goal;
		Gdx.app.log(TAG, "Goal collected");
	}

	private void onCollisionBunnyWithCarrot(Carrot carrot)
	{
		carrot.collected = true;
		level.carrots.add(carrot);
		level.bunnyHead.setCarrotPowerup(true);
		score += carrot.getScore();
		Gdx.app.log(TAG, "Carrot collected");
		if (objectiv.isobectivetype(ObjectiveType.COLLECT_CARROTS))
		{
			objectiv.parameterCounter++;
		}
	}

	private void onCollisionBunnyWithfox(Fox fox)
	{

		float heightDifference = Math.abs(level.bunnyHead.position.y - (fox.position.y + fox.bounds.height));
		if (heightDifference < 0.1f)
		{
			level.bunnyHead.jumpState = JUMP_STATE.GROUNDED;
			level.bunnyHead.setJumping(true);
			fox.isdead = true;
			score += fox.getscore();
			return;
		}

		if (!level.bunnyHead.hasstar)
		{
			ishit = true;
			level.bunnyHead.ishit = true;
		}
		else
		{
			fox.isdead = true;
			score += fox.getscore();
			return;
		}

	}

	private void onCollisionBunnyWithextraLifeBunny(ExtraLifeBunny extraLifeBunny)
	{
		if (lives != 3 && !extraLifeBunny.collected)
		{
			livecollected = true;
			extraLifeBunny.collected = true;
			collectedpossition.add(extraLifeBunny.position);
		}
	}

	private void onCollisionBunnyWithFire()
	{
		if (!level.bunnyHead.hasStarPowerup())
		{
			ishit = true;
			level.bunnyHead.ishit = true;
		}

	}

	private void onCollisionBunnyWithladder()
	{

		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			level.bunnyHead.velocity.y = level.bunnyHead.terminalVelocity.y;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			level.bunnyHead.velocity.y = -level.bunnyHead.terminalVelocity.y;
		}

		else
		{
			level.bunnyHead.velocity.y = 0;
		}

	}

	private void onCollisionBunnyWithstar(Star star)
	{
		star.collected = true;
		level.bunnyHead.setStarPowerup(true);
		Gdx.app.log(TAG, "star collected");
	}

	private void testCollisions()
	{
		r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y, level.bunnyHead.bounds.width, level.bunnyHead.bounds.height);

		// Test collision: Bunny Head <-> Rocks
		for (Rock rock : level.rocks)
		{
			r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyHeadWithRock(rock);
			// IMPORTANT: must do all collisions for valid
			// edge testing on rocks.
		}

		for (Ladder ladder : level.ladders)
		{
			r2.set(ladder.position.x, ladder.position.y, ladder.bounds.width, ladder.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyWithladder();
			break;
		}

		// Test collision: Bunny Head <-> Gold Coins
		for (GoldCoin goldCoin : level.goldCoins)
		{
			if (goldCoin.collected) continue;
			r2.set(goldCoin.position.x, goldCoin.position.y, goldCoin.bounds.width, goldCoin.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyWithGoldCoin(goldCoin);
			break;
		}

		// Test collision: Bunny Head <-> Feathers
		for (Feather feather : level.feathers)
		{
			if (feather.collected) continue;
			r2.set(feather.position.x, feather.position.y, feather.bounds.width, feather.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyWithFeather(feather);
			break;
		}

		// Test collision: Bunny Head <-> Goal
		if (!level.goal.collected)
		{
			r2.set(level.goal.position.x, level.goal.position.y, level.goal.bounds.width, level.goal.bounds.height);
			if (r1.overlaps(r2)) onCollisionBunnyWithGoal(level.goal);
		}

		// Test collision: Bunny Head <-> Carrots
		for (Carrot carrot : level.carrots)
		{
			if (carrot.collected) continue;
			r2.set(carrot.position.x, carrot.position.y, carrot.bounds.width, carrot.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyWithCarrot(carrot);
			break;
		}

		// Test collision: Bunny Head <-> Foxes
		for (Fox fox : level.foxes)
		{
			r2.set(fox.position.x, fox.position.y, fox.bounds.width, fox.bounds.height);
			if (!r1.overlaps(r2) || fox.isdead) continue;
			onCollisionBunnyWithfox(fox);
			break;
		}

		// Test collision: Bunny Head <-> ExtralifeBunnies
		for (ExtraLifeBunny extraLifeBunny : level.extraLifeBunnies)
		{
			r2.set(extraLifeBunny.position.x, extraLifeBunny.position.y, extraLifeBunny.bounds.width, extraLifeBunny.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyWithextraLifeBunny(extraLifeBunny);
			break;
		}

		// Test collision: Bunny Head <-> Fire
		for (Fire fire : level.fire)
		{
			r2.set(fire.position.x, fire.position.y, fire.bounds.width, fire.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyWithFire();

			break;
		}

		for (Star star : level.stars)
		{
			r2.set(star.position.x, star.position.y, star.bounds.width, star.bounds.height);
			if (!r1.overlaps(r2)) continue;
			onCollisionBunnyWithstar(star);

			break;
		}

	}

	// This methods make sure that the fox is on a rock && that they move both
	// ways
	public void keepFoxOnRock()
	{
		for (Fox fox : level.foxes)
		{

			boolean collison = false;
			r1.set(fox.position.x, fox.position.y, fox.bounds.width, fox.bounds.height);
			for (Rock rock : level.rocks)
			{
				r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);

				if ((r1.x < r2.x + r2.width - 0.4 && r1.x + r1.width - 0.4 > r2.x))
				{
					collison = true;
					fox.position.y = rock.position.y + r2.height / 2 + fox.bounds.height + fox.bounds.height / 2;
				}

			}
			if (!collison)
			{
				fox.ChangeDirection();

				if (fox.viewDirection == VIEW_DIRECTION.LEFT)
				{
					fox.position.x = (float) (fox.position.x - 0.1);
				}

				else fox.position.x = (float) (fox.position.x + 0.1);
			}

		}
	}

	// This methods make sure that the Fire is on a rock
	public void keepFiregroundlevel()
	{
		for (Fire object : level.fire)
		{

			r1.set(object.position.x, object.position.y, object.bounds.width, object.bounds.height);
			for (Rock rock : level.rocks)
			{
				r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);

				if ((r1.x < r2.x + r2.width - 0.4 && r1.x + r1.width - 0.4 > r2.x))
				{
					object.position.y = rock.position.y + r2.height / 2 + object.bounds.height + object.bounds.height / 7;
				}
			}
		}
	}

	private void initLevel()
	{
		islastlevel = false;
		forcedMovement = false; // if true the bunny will run by itself
		score = 0;
		objectiv = new Objectives();
		switch (levelnow)
		{
		case 1:
			level = new Level(Constants.LEVEL_01);
			objectiv.setObjective(30, ObjectiveType.COLECT_COINS); // set
																	// objectiv

			break;
		case 2:
			level = new Level(Constants.LEVEL_02);
			objectiv.setObjective(20, ObjectiveType.TIMELIMIT); // set objectiv
			break;
		case 3:
			level = new Level(Constants.LEVEL_03);
			objectiv.setObjective(2, ObjectiveType.COLLECT_CARROTS); // set
																		// objectiv
			break;
		case 4:
			level = new Level(Constants.LEVEL_04);
			objectiv.setObjective(1500, ObjectiveType.GET_SCORE); // set
																	// objectiv
			break;
		case 5:
			level = new Level(Constants.LEVEL_05);
			break;
		case 6:
			level = new Level(Constants.LEVEL_06);
			objectiv.setObjective(5000, ObjectiveType.GET_SCORE); // set
																	// objectiv
			forcedMovement = true;
			forcedcameraspeed = 2.5f; // set the speed of the bunny
			islastlevel = true;
			break;
		default:
			levelnow = 1;
			lives = Constants.LIVES_START;
			liveslost = 0;
			initLevel();
			break;
		}
		ishit = false;
		cameraHelper.setTarget(level.bunnyHead);
		timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
		for (Vector2 position : collectedpossition)
		{
			for (ExtraLifeBunny levelExtraLifeBunny : level.extraLifeBunnies)
				if (levelExtraLifeBunny.position.x == position.x)
				{
					levelExtraLifeBunny.collected = true;
				}
		}
	}

	public WorldController()
	{
		init();
	}

	private void init()
	{

		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		lives = Constants.LIVES_START - liveslost;
		collectedpossition = new Array<Vector2>();
		initLevel();
		timesinsstart = 3;

	}

	public void update(float deltaTime)
	{

		timesinsstart -= deltaTime;
		handleDebugInput(deltaTime);
		if (!isGameWon())
		{

			// if (forcedcamera)
			// {
			// focemoveCamera((float) forcedcameraspeed, 0);
			// }
			if (isGameOver())
			{
				timeLeftGameOverDelay -= deltaTime;
				levelnow = 1;
				liveslost = 0;
				if (timeLeftGameOverDelay < 0) init();
			}

			if (ishit && !isGameOver())
			{
				if (lives != 1)
				{

					timeLeftGameOverDelay -= deltaTime;
					if (timeLeftGameOverDelay < 1)
					{
						lives--;
						liveslost++;

						initLevel();
					}
				}

				else
				{
					lives--;
				}
			}
			if (livecollected)
			{
				lives++;
				liveslost--;
				livecollected = false;
			}

			if (objectiv.levellost(level.goal.collected) && !isGameOver())
			{
				if (lives != 1)
				{

					timeLeftGameOverDelay -= deltaTime;
					if (timeLeftGameOverDelay < 1)
					{
						lives--;
						liveslost++;
						initLevel();
					}
				}

			}

			if (objectiv.isobectivetype(ObjectiveType.GET_SCORE))
			{
				objectiv.parameterCounter = score;
			}
		}
		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);
		objectiv.update(deltaTime, level.goal.collected);
		if (!isGameOver() && isPlayerInWater())
		{
			lives--;
			liveslost++;
			if (!isGameOver()) initLevel();// timeLeftGameOverDelay =
											// Constants.TIME_DELAY_GAME_OVER;
			// else initLevel();
		}

		if (isGameWon())
		{
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0)
			{
				levelnow++;
				init();
			}
			;
		}
		else
		{
			handleInputGame(deltaTime);
		}

		keepFoxOnRock();
		keepFiregroundlevel();

	}

	private void handleDebugInput(float deltaTime)
	{
		if (Gdx.app.getType() != ApplicationType.Desktop) return;

		// Camera Controls (move)
		if (!cameraHelper.hasTarget(level.bunnyHead))
		{
			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0, -camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.setPosition(0, 0);
		}

		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
	}

	private void moveCamera(float x, float y)
	{
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	@Override
	public boolean keyUp(int keycode)
	{

		if (keycode == Keys.R)
		{ // Reset game world
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		if (keycode == Keys.NUMPAD_4) onCollisionBunnyWithFeather(new Feather());
		if (keycode == Keys.NUMPAD_3)
		{
			onCollisionBunnyWithGoal(new Goal());
			objectiv.init();
		}
		if (keycode == Keys.NUMPAD_5) onCollisionBunnyWithCarrot(new Carrot());
		if (keycode == Keys.NUMPAD_6) onCollisionBunnyWithstar(new Star());
		else if (keycode == Keys.ENTER)
		{ // Toggle camera follow
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
			if (forcedMovement)
			{
				forcedMovement = false;
			}

			else
			{
				forcedMovement = true;
			}
			Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
		}
		return false;
	}
	//

}