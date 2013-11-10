package com.blarg.gdx.graphics.screeneffects;

import com.badlogic.gdx.utils.Disposable;

import java.util.LinkedList;

public class ScreenEffectManager implements Disposable
{
	LinkedList<EffectInfo> effects;
	int numLocalEffects;
	int numGlobalEffects;

	public ScreenEffectManager() {
		effects = new LinkedList<EffectInfo>();
		numLocalEffects = 0;
		numGlobalEffects = 0;
	}

	/*** Get / Add / Remove ***/

	public <T extends ScreenEffect> T add(Class<T> effectType) {
		return add(effectType, true);
	}

	public <T extends ScreenEffect> T add(Class<T> effectType, boolean isLocalEffect) {
		int existingIndex = getIndexFor(effectType);
		if (existingIndex != -1)
			throw new UnsupportedOperationException("Cannot add an effect of the same type as an existing effect already being managed.");

		T newEffect;
		try {
			newEffect = effectType.newInstance();
		} catch (Exception e) {
			return null;
		}

		EffectInfo effectInfo = new EffectInfo(newEffect, isLocalEffect);
		add(effectInfo);
		return newEffect;
	}

	public <T extends ScreenEffect> T get(Class<T> effectType) {
		int index = getIndexFor(effectType);
		if (index == -1)
			return null;
		else
			return effectType.cast(effects.get(index).effect);
	}

	public <T extends ScreenEffect> void remove(Class<T> effectType) {
		int existingIndex = getIndexFor(effectType);
		if (existingIndex != -1)
			remove(existingIndex);
	}

	public void removeAll() {
		while (effects.size() > 0)
			remove(0);
	}

	private void add(EffectInfo newEffectInfo) {
		if (newEffectInfo == null)
			throw new IllegalArgumentException("newEffectInfo cannot be null.");

		effects.add(newEffectInfo);
		newEffectInfo.effect.onAdd();

		if (newEffectInfo.isLocal)
			++numLocalEffects;
		else
			++numGlobalEffects;
	}

	private void remove(int index) {
		if (index < 0 || index >= effects.size())
			throw new IllegalArgumentException("Invalid effect index.");

		EffectInfo effectInfo = effects.get(index);
		if (effectInfo.isLocal)
			--numLocalEffects;
		else
			--numGlobalEffects;

		effectInfo.effect.onRemove();
		effectInfo.effect.dispose();
		effects.remove(index);
	}

	/*** events ***/

	public void onAppPause() {
		for (int i = 0; i < effects.size(); ++i)
			effects.get(i).effect.onAppPause();
	}

	public void onAppResume() {
		for (int i = 0; i < effects.size(); ++i)
			effects.get(i).effect.onAppResume();
	}

	public void onResize() {
		for (int i = 0; i < effects.size(); ++i)
			effects.get(i).effect.onResize();
	}

	public void onRenderLocal(float delta) {
		if (numLocalEffects == 0)
			return;

		for (int i = 0; i < effects.size(); ++i) {
			EffectInfo effectInfo = effects.get(i);
			if (effectInfo.isLocal)
				effectInfo.effect.onRender(delta);
		}
	}

	public void onRenderGlobal(float delta) {
		if (numGlobalEffects == 0)
			return;

		for (int i = 0; i < effects.size(); ++i) {
			EffectInfo effectInfo = effects.get(i);
			if (!effectInfo.isLocal)
				effectInfo.effect.onRender(delta);
		}
	}

	public void onUpdate(float delta) {
		int i = 0;
		while (i < effects.size()) {
			EffectInfo effectInfo = effects.get(i);
			if (!effectInfo.effect.isActive) {
				// index doesn't change, we're removing one, so next index now equals this index
				remove(i);
			} else {
				effectInfo.effect.onUpdate(delta);
				++i;
			}
		}
	}

	/*** misc ***/

	private <T extends ScreenEffect> int getIndexFor(Class<T> effectType) {
		for (int i = 0; i < effects.size(); ++i) {
			if (effects.get(i).effect.getClass() == effectType)
				return i;
		}
		return -1;
	}

	/*** cleanup ***/

	public void dispose() {
	}
}
