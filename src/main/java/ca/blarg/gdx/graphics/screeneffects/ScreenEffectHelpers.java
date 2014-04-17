package ca.blarg.gdx.graphics.screeneffects;

import com.badlogic.gdx.graphics.Color;

public class ScreenEffectHelpers {
	public static boolean doFadingTransition(ScreenEffectManager effectManager, boolean isTransitioningOut, boolean started) {
		if (started) {
			FadeScreenEffect fade = effectManager.add(FadeScreenEffect.class, false);
			if (isTransitioningOut)
				fade.fadeOut(1.0f, Color.BLACK, 0.01f);
			else
				fade.fadeIn(0.0f, Color.BLACK, 0.01f);
		} else {
			if (effectManager.get(FadeScreenEffect.class).isDoneFading()) {
				effectManager.remove(FadeScreenEffect.class);
				return true;
			}
		}
		return false;
	}
}
