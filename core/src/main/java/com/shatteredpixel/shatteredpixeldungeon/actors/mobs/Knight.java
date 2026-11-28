package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KnightSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.utils.Random;

/**
 * The Knight: boss of the boss1 floor.
 * PLACEHOLDER for now: it just stands where it is (it never moves) and hits whoever is
 * next to it. Stats below are temporary. Name and description are in the messages files.
 */
public class Knight extends Mob {

	{
		spriteClass = KnightSprite.class;

		HP = HT = 100;
		EXP = 0;
		defenseSkill = 5;

		properties.add(Property.BOSS);
		//can't be pushed or swapped around either
		properties.add(Property.IMMOVABLE);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 6 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 10;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange( 0, 2 );
	}

	//never moves: not towards its target, not away from it
	@Override
	protected boolean getCloser( int target ) {
		return false;
	}

	@Override
	protected boolean getFurther( int target ) {
		return false;
	}

	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss( this );
		}
	}
}
