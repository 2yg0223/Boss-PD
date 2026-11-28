package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Knight;

/** boss1 floor (the Knight is planned for it). Sewers look. */
public class Boss1Level extends BossArenaLevel {

	{
		color1 = 0x48763c;
		color2 = 0x59994a;
	}

	//the boss of this floor stands in the middle of the room
	@Override
	protected void createMobs() {
		Knight knight = new Knight();
		knight.pos = arenaCenterCell();
		mobs.add( knight );
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_SEWERS;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_SEWERS;
	}
}
