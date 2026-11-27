package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

/**
 * Floor 0: a small dead-end-like room where a run starts.
 * The hero begins on the (unusable) way out at the bottom, and the stairs down
 * to the first boss floor are at the top.
 */
public class StartLevel extends Level {

	private static final int SIZE = 5;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_CAVES;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_HALLS;
	}

	@Override
	protected boolean build() {

		setSize(7, 7);

		for (int i=2; i < SIZE; i++) {
			for (int j=2; j < SIZE; j++) {
				map[i * width() + j] = Terrain.EMPTY;
			}
		}

		for (int i=1; i <= SIZE; i++) {
			map[width() + i] =
			map[width() * SIZE + i] =
			map[width() * i + 1] =
			map[width() * i + SIZE] =
				Terrain.WATER;
		}

		//the way out sits at the bottom, like the entrance of a dead end
		int entrance = SIZE * width() + SIZE / 2 + 1;
		transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.SURFACE));
		map[entrance] = Terrain.ENTRANCE;

		//the stairs down are at the top
		int exit = width() + SIZE / 2 + 1;
		transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT));
		map[exit] = Terrain.EXIT;

		return true;
	}

	//there is no leaving the dungeon from the start
	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition.type == LevelTransition.Type.SURFACE) {
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show( new WndMessage( Messages.get(hero, "leave") ) );
				}
			});
			return false;
		}
		return super.activateTransition(hero, transition);
	}

	@Override
	public Mob createMob() {
		return null;
	}

	@Override
	protected void createMobs() {
	}

	public Actor addRespawner() {
		return null;
	}

	@Override
	protected void createItems() {
	}

	@Override
	public int randomRespawnCell( Char ch ) {
		return entrance()-width();
	}
}
