package ca.blarg.gdx.graphics.screeneffects;

class EffectInfo
{
	public final ScreenEffect effect;
	public boolean isLocal;

	public EffectInfo(ScreenEffect effect, boolean isLocal) {
		if (effect == null)
			throw new IllegalArgumentException("effect can not be null.");

		this.effect = effect;
		this.isLocal = isLocal;
	}
}
