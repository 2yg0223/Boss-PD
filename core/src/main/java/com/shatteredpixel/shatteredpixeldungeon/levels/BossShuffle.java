package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
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
