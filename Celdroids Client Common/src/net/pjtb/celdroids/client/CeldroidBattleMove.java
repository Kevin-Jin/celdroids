package net.pjtb.celdroids.client;

import java.util.ArrayList;

public class CeldroidBattleMove {
	public String file;

	public final String name;
	public final String spriteDirectory;
	public final int frameCount;
	public final ArrayList<CeldroidElement> element;
	public final int damage;
	public final ArrayList<CeldroidDebuff> inflicts;
	public final boolean selfOrigin;
	public final boolean loop;

	/**
	 * Private constructor can still be called final variables can still be
	 * assigned by reflection in Json class.
	 * Just set values to garbage.
	 */
	private CeldroidBattleMove() {
		name = null;
		spriteDirectory = null;
		frameCount = 0;
		element = null;
		damage = 0;
		inflicts = null;
		selfOrigin = false;
		loop = false;
	}
}
