package net.pjtb.celdroids.client.desktop;

import net.pjtb.celdroids.client.Game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class DesktopGame {
	public static void main(String[] args) {
		new LwjglApplication(new Game(), "Celdroids", 1280, 720, false);
	}
}