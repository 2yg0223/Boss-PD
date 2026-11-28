package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
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
					//yellow "..."
					sprite.showStatus( CharSprite.NEUTRAL, "..." );
				}
			} else {
				//done waiting: go after the hero
				state = HUNTING;
				target = Dungeon.hero.pos;
				if (sprite != null) {
					//red "!" (shown as text: the usual alert icon is cleared again at the
					// start of the same act, so it would never be seen here)
					sprite.showStatus( CharSprite.NEGATIVE, "!" );
				}
				if (!BossHealthBar.isAssigned()) {
					BossHealthBar.assignBoss( this );
				}
			}
		}

		//pattern 1 is already running
		if (slamStep > 0) {
			if (state != HUNTING) {
				//something broke it off, so drop the pattern
				slamStep = 0;
				slamCells = null;
			} else if (paralysed > 0) {
				//CC: the pattern is reset, and the normal code spends the turn
				slamStep = 0;
				slamCells = null;
				return super.act();
			} else {
				return slamTurn();
			}
		}

		//start pattern 1 when the hero is close enough
		if (state == HUNTING && paralysed <= 0) {
			Char target = slamTarget();
			if (target != null && startSlam( target )) {
				return true;
			}
		}

		return super.act();
	}

	/* Pattern 1: slam forward
	 * turn 1: sword goes up, the direction is locked and the line is shown
	 * turn 2: holding
	 * turn 3: strike on the locked line
	 * turn 4: recovery (does nothing)
	 * turn 5: recovery (does nothing)
	 * turn 6: chasing again
	 */
	private static final int SLAM_RANGE = 5;

	//0 = not in the pattern, 1-4 = the next turn to play (see slamTurn)
	private int slamStep = 0;
	//the locked cells in front of the knight, nearest first
	private int[] slamCells = null;

	//the hero (or other enemy) if the knight should start the slam right now, else null
	private Char slamTarget() {
		Char target = chooseEnemy();
		if (target == null || !target.isAlive() || target.invisible > 0) return null;
		if (Dungeon.level.distance( pos, target.pos ) > SLAM_RANGE) return null;
		Ballistica sight = new Ballistica( pos, target.pos, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID );
		if (sight.collisionPos == null || sight.collisionPos != target.pos) return null;
		return target;
	}

	//turn 1
	private boolean startSlam( Char target ) {
		//the line goes toward the target and past it, up to SLAM_RANGE cells or the first wall
		Ballistica line = new Ballistica( pos, target.pos, Ballistica.STOP_SOLID );
		int len = Math.min( SLAM_RANGE, line.dist );
		if (len <= 0) return false;

		slamCells = new int[len];
		for (int i = 0; i < len; i++) {
			slamCells[i] = line.path.get( i + 1 );
		}
		slamStep = 1;

		//warning stays up until the strike turn
		for (int cell : slamCells) {
			GameScene.targetedCell( cell, 2 * TICK );
		}
		if (sprite != null) {
			sprite.turnTo( pos, slamCells[len - 1] );
		}
		if (Dungeon.hero != null) {
			Dungeon.hero.interrupt();
		}

		spend( TICK );
		return true;
	}

	//turns 2-5
	private boolean slamTurn() {
		switch (slamStep) {
			case 1: //turn 2: holding
				slamStep = 2;
				break;
			case 2: //turn 3: strike
				slamStrike();
				slamStep = 3;
				break;
			case 3: //turn 4: recovery
				slamStep = 4;
				break;
			default: //turn 5: recovery, chasing again from the next turn
				slamStep = 0;
				slamCells = null;
				break;
		}
		spend( TICK );
		return true;
	}

	private void slamStrike() {
		if (sprite != null && sprite.visible) {
			sprite.zap( slamCells[slamCells.length - 1] );
		}
		for (int cell : slamCells) {
			Char ch = Actor.findChar( cell );
			if (ch != null && ch != this && ch.isAlive() && ch.alignment != alignment) {
				attack( ch );
			}
		}
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
	private static final String SLAM_STEP_KEY = "slam_step";
	private static final String SLAM_CELLS_KEY = "slam_cells";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( INTRO_TURNS_KEY, introTurns );
		bundle.put( SLAM_STEP_KEY, slamStep );
		if (slamCells != null) {
			bundle.put( SLAM_CELLS_KEY, slamCells );
		}
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		introTurns = bundle.getInt( INTRO_TURNS_KEY );
		slamStep = bundle.getInt( SLAM_STEP_KEY );
		slamCells = bundle.contains( SLAM_CELLS_KEY ) ? bundle.getIntArray( SLAM_CELLS_KEY ) : null;
		if (slamCells == null || slamCells.length == 0) {
			slamStep = 0;
			slamCells = null;
		}
	}
}
