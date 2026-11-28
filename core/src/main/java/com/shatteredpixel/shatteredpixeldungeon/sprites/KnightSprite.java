package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

/**
 * Knight boss sprite. TEMPORARY: it uses the Warrior hero sprite wearing armor
 * until the Knight gets its own art.
 */
public class KnightSprite extends MobSprite {

	private static final int FRAME_WIDTH	= 12;
	private static final int FRAME_HEIGHT	= 15;

	//which armor look of the hero sheet to use: 0 = no armor, 1 = cloth armor (what the
	// hero starts with), 2 = leather, 3 = mail, 4 = scale, 5 = plate, 6 = class armor
	private static final int ARMOR_TIER		= 6;

	public KnightSprite() {
		super();

		texture( Assets.Sprites.WARRIOR );

		//the hero sheet has one row of frames per armor tier
		TextureFilm tiers = new TextureFilm( texture, texture.width, FRAME_HEIGHT );
		TextureFilm frames = new TextureFilm( tiers, ARMOR_TIER, FRAME_WIDTH, FRAME_HEIGHT );

		idle = new MovieClip.Animation( 1, true );
		idle.frames( frames, 0, 0, 0, 1, 0, 0, 1, 1 );

		run = new MovieClip.Animation( 20, true );
		run.frames( frames, 2, 3, 4, 5, 6, 7 );

		die = new MovieClip.Animation( 20, false );
		die.frames( frames, 8, 9, 10, 11, 12, 11 );

		attack = new MovieClip.Animation( 15, false );
		attack.frames( frames, 13, 14, 15, 0 );

		zap = attack.clone();

		play( idle );
	}
}
