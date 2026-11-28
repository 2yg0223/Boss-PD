package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

/**
 * Boss-only dungeon: every floor is a boss floor, and the order of the bosses is
 * shuffled once per seed.
 */
public class BossShuffle {

	//number of boss floors in a run (temporary: the 5 vanilla bosses)
	public static final int BOSS_COUNT = 5;

	//false: boss floors are generated with no mobs at all (no bosses, no NPCs),
	//and with their exits already open, so that the floor order can be tested.
	public static final boolean SPAWN_MOBS = false;

	//seed-based order of the bosses, e.g. {3, 0, 4, 1, 2}. Same seed -> same order.
	public static int[] order() {
		int[] order = new int[BOSS_COUNT];
		for (int i = 0; i < BOSS_COUNT; i++) {
			order[i] = i;
		}

		//offset the seed so this doesn't mirror other seed uses
		Random.pushGenerator( Dungeon.seed + 7 );
			Random.shuffle( order );
		Random.popGenerator();

		return order;
	}

	//TEMPORARY (to check that updates install over old builds):
	// remains right next to the way down of floor 0 (start room), holding 2 random potions.
	// Looks for the closest free cell around the exit, starting at 1 tile away.
	public static void placeFirstFloorRemains( Level level ) {
		LevelTransition exit = level.getTransition( LevelTransition.Type.REGULAR_EXIT );
		if (exit == null) return;

		int cell = -1;
		for (int r = 1; r <= 6 && cell == -1; r++) {
			for (int y = exit.top - r; y <= exit.bottom + r && cell == -1; y++) {
				for (int x = exit.left - r; x <= exit.right + r; x++) {
					//only the ring that is exactly r tiles away from the exit
					if (x != exit.left - r && x != exit.right + r
							&& y != exit.top - r && y != exit.bottom + r) continue;
					if (x < 0 || y < 0 || x >= level.width() || y >= level.height()) continue;

					int c = x + y * level.width();
					if (isFreeForRemains( level, c, x, y )) {
						cell = c;
						break;
					}
				}
			}
		}
		if (cell == -1) return;

		//separate generator so this doesn't disturb anything else
		Random.pushGenerator( Dungeon.seedCurDepth() + 3 );
			Heap heap = level.drop( Generator.randomUsingDefaults( Generator.Category.POTION ), cell );
			level.drop( Generator.randomUsingDefaults( Generator.Category.POTION ), cell );
			heap.type = Heap.Type.SKELETON;
		Random.popGenerator();
	}

	private static boolean isFreeForRemains( Level level, int c, int x, int y ) {
		if (!level.passable[c] || level.solid[c] || level.avoid[c] || level.water[c]) return false;
		if (level.heaps.get( c ) != null || level.findMob( c ) != null) return false;

		int terrain = level.map[c];
		if (terrain == Terrain.ENTRANCE || terrain == Terrain.EXIT
				|| terrain == Terrain.LOCKED_EXIT || terrain == Terrain.UNLOCKED_EXIT) return false;

		//never on top of any stairs/transition
		Point p = new Point( x, y );
		for (LevelTransition t : level.transitions) {
			if (t.inside( p )) return false;
		}
		return true;
	}

	//depth is 1-based: depth 1 is the first boss floor
	public static Level levelForDepth( int depth ) {
		switch (order()[depth - 1]) {
			case 0: default:
				return new SewerBossLevel();   //Goo
			case 1:
				return new PrisonBossLevel();  //Tengu
			case 2:
				return new CavesBossLevel();   //DM-300
			case 3:
				return new CityBossLevel();    //Dwarf King
			case 4:
				return new HallsBossLevel();   //Yog-Dzewa
		}
	}
}
