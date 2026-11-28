package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KnightSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

/**
 * The Knight: boss of the boss1 floor.
 * PLACEHOLDER for now. Stats below are temporary. Name and description are in the
 * messages files.
 *
 * Behaviour: it is awake from the start (no sleeping effect) but stands still. For the
 * first 3 turns it shows "..." above its head, once per turn, then it starts chasing the hero.
 */
public class Knight extends Mob {

	{
		spriteClass = KnightSprite.class;

		HP = HT = 100;
		EXP = 0;
		defenseSkill = 5;

		properties.add(Property.BOSS);

		//not asleep (that would show the sleeping effect), just standing still for now
		state = PASSIVE;
	}

	//how many turns it shows "..." before it starts chasing
	private static final int INTRO_TURNS = 3;
	private int introTurns = 0;

	@Override
	public boolean act() {
		if (state == PASSIVE) {
			if (introTurns < INTRO_TURNS) {
				introTurns++;
				if (sprite != null) {
					sprite.showStatus( CharSprite.DEFAULT, "..." );
				}
			} else {
				//done waiting: go after the hero
				state = HUNTING;
				target = Dungeon.hero.pos;
				notice();
			}
		}
		return super.act();
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

	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss( this );
		}
	}

	private static final String INTRO_TURNS_KEY = "intro_turns";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( INTRO_TURNS_KEY, introTurns );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		introTurns = bundle.getInt( INTRO_TURNS_KEY );
	}
}
