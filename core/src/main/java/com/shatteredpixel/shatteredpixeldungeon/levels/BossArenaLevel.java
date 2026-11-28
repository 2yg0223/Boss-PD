package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Rect;

/**
 * Shared map of the boss floors (boss1 ~ boss5): Tengu's phase-2 room, an elliptical
 * arena in the middle of a 32x32 map, same shape and position as in PrisonBossLevel.
 * No mobs. Each boss floor is a small subclass that only picks its tiles and colors.
 *
 * (Levels are re-created by class name when a game is loaded, so they need a
 * no-argument constructor. That's why this isn't one class with parameters.)
 */
public abstract class BossArenaLevel extends Level {

	{
		//the player should be able to see the whole arena
		viewDistance = 12;
	}

	private static final int W = 32;
	private static final int H = 32;

	//same rect as Tengu's arena in PrisonBossLevel
	private static final Rect arena = new Rect(3, 1, 18, 16);

	@Override
	protected boolean build() {

		setSize(W, H);

		Painter.fill(this, 0, 0, W, H, Terrain.WALL);
		Painter.fill(this, arena, Terrain.WALL);
		Painter.fillEllipse(this, arena, 1, Terrain.EMPTY);

		//find the top-most and bottom-most floor cells in the middle column of the arena
		int cx = arena.left + arena.width() / 2;
		int yTop = -1, yBottom = -1;
		for (int y = arena.top; y <= arena.bottom; y++) {
			if (map[cx + y * W] == Terrain.EMPTY) {
				if (yTop == -1) yTop = y;
				yBottom = y;
			}
		}
		if (yTop == -1) return false;

		//the way in is at the bottom of the room, the way down at the top
		int entrance = cx + (yBottom - 1) * W;
		transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
		map[entrance] = Terrain.ENTRANCE;

		int exit = cx + (yTop + 1) * W;
		transitions.add(new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT));
		map[exit] = Terrain.EXIT;

		return true;
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
		return entrance() - width();
	}
}
