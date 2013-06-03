package net.pjtb.celdroids.client.battle;

import java.util.EnumMap;
import java.util.Map;

import net.pjtb.celdroids.Constants;
import net.pjtb.celdroids.client.Button;
import net.pjtb.celdroids.client.CeldroidMonster;
import net.pjtb.celdroids.client.ConfirmPopupScene;
import net.pjtb.celdroids.client.Model;
import net.pjtb.celdroids.client.Scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.utils.NumberUtils;

public class BattleScene implements Scene {
	private enum BattleSubSceneType { CONFIRM_FLEE_POPUP }

	private final BattleModel model;

	protected final Map<BattleSubSceneType, Scene> subScenes;
	private Scene subScene;

	private final FanSelect partySwitcher, attackList;
	private final Button runButton;

	private float fontTint;
	private String text;
	private float remainingTextTime;

	public BattleScene(Model m) {
		this.model = new BattleModel(m);

		subScenes = new EnumMap<BattleSubSceneType, Scene>(BattleSubSceneType.class);
		subScenes.put(BattleSubSceneType.CONFIRM_FLEE_POPUP, new ConfirmPopupScene(m, "Are you sure you want to flee?", Model.SceneType.WORLD));

		runButton = new Button(m, "Flee", new Runnable() {
			@Override
			public void run() {
				flee();
			}
		}, 1172, 576, 108, 144, "ui/battleScene/run", "ui/battleScene/selectedRun", 255, 255, 255, 255, 255, 0, 0, 63);

		partySwitcher = new FanSelect(model.parent, 1 + 120 / 2, Constants.HEIGHT / 2, -Math.PI / 3, Math.PI / 3, 60, 200, "Party");
		attackList = new FanSelect(model.parent, Constants.WIDTH - 200 - 120 / 2, Constants.HEIGHT / 2, 2 * Math.PI / 3, 4 * Math.PI / 3, 60, 200, "Attack");

		final CeldroidMonster[] restOfParty = model.party.subList(1, model.party.size()).toArray(new CeldroidMonster[model.party.size() - 1]);
		final String[] selectablePartyNames = new String[restOfParty.length];
		for (int i = 0; i < restOfParty.length; i++)
			selectablePartyNames[i] = restOfParty[i].getName();
		final FanSelect.SelectTask[] partySwitchTask = new FanSelect.SelectTask[1];
		partySwitchTask[0] = new FanSelect.SelectTask() {
			@Override
			public void selected(int index) {
				model.swapPartyLead(index + 1);
				restOfParty[index] = model.party.get(index + 1);
				for (int i = 0; i < restOfParty.length; i++)
					selectablePartyNames[i] = restOfParty[i].getName();
				partySwitcher.setSelections(partySwitchTask[0], selectablePartyNames);
				text = "Go, " + model.party.get(0).getName() + "!";
				remainingTextTime = 2;
			}
		};
		partySwitcher.setSelections(partySwitchTask[0], selectablePartyNames);

		attackList.setSelections(new FanSelect.SelectTask() {
			@Override
			public void selected(int index) {
				text = model.party.get(0).getName() + " used Move " + (index + 1) + "!";
				remainingTextTime = 2;
			}
		}, "Move 1", "Move 2", "Move 3", "Move 4", "Move 5", "Move 6");

		fontTint = NumberUtils.intToFloatColor(0xFF << 24 | 0x00 << 16 | 0x00 << 8 | 0xFF);
	}

	private void flee() {
		subScene = subScenes.get(BattleSubSceneType.CONFIRM_FLEE_POPUP);
		subScene.swappedIn(true);
	}

	@Override
	public void swappedIn(boolean transition) {
		
	}

	@Override
	public void pause() {
		if (subScene != null)
			subScene.pause();
	}

	@Override
	public void resume() {
		if (subScene != null)
			subScene.resume();
	}

	@Override
	public void update(float tDelta) {
		runButton.hidden = (subScene != null);
		runButton.update(tDelta);
		partySwitcher.hidden = (subScene != null);
		partySwitcher.update(tDelta);
		attackList.hidden = (subScene != null);
		attackList.update(tDelta);
		if (remainingTextTime > 0) {
			remainingTextTime -= tDelta;
			if (remainingTextTime <= 0) {
				remainingTextTime = 0;
				text = null;
			}
		}

		if (subScene == null) {
			if (model.parent.controller.wasBackPressed && !Gdx.input.isKeyPressed(Keys.ESCAPE) && !Gdx.input.isKeyPressed(Keys.BACK)) {
				flee();
			} else if (model.parent.controller.wasMenuPressed && !Gdx.input.isKeyPressed(Keys.ENTER) && !Gdx.input.isKeyPressed(Keys.MENU)) {
				
			}
		} else {
			subScene.update(tDelta);
		}
	}

	@Override
	public void draw(SpriteBatch batch) {
		Sprite s = model.parent.sprites.get("character/human1/left/0");
		s.setBounds(10, (Constants.HEIGHT - 120) / 2, 120, 120);
		if (!s.isFlipX())
			s.flip(true, false);
		s.draw(batch);
		s = model.parent.sprites.get(model.party.get(0).monsterType.sprite);
		s.setBounds(500, (Constants.HEIGHT - 120) / 2, 120, 120);
		if (!s.isFlipX())
			s.flip(true, false);
		s.draw(batch);

		s = model.parent.sprites.get("character/human2/left/0");
		s.setBounds(Constants.WIDTH - 10 - 120, (Constants.HEIGHT - 120) / 2, 120, 120);
		if (s.isFlipX())
			s.flip(true, false);
		s.draw(batch);
		s = model.parent.sprites.get("monster/rock/evol2");
		s.setBounds(Constants.WIDTH - 200 - 120, (Constants.HEIGHT - 120) / 2, 120, 120);
		if (s.isFlipX())
			s.flip(true, false);
		s.draw(batch);

		if (text != null) {
			BitmapFont fnt = model.parent.assets.get("fonts/buttons.fnt", BitmapFont.class);
			TextBounds bnds = fnt.getBounds(text);
			fnt.setColor(fontTint);
			fnt.draw(batch, text, (float) ((Constants.WIDTH - bnds.width) / 2), (float) (50 + bnds.height));
		}

		runButton.draw(batch);
		partySwitcher.draw(batch);
		attackList.draw(batch);

		if (subScene != null)
			subScene.draw(batch);
	}

	@Override
	public void swappedOut(boolean transition) {
		
	}

	@Override
	public Scene getSubscene() {
		return subScene;
	}

	@Override
	public void setSubscene(Scene scene) {
		this.subScene = scene;
	}
}
