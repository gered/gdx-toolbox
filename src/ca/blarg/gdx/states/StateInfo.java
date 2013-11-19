package ca.blarg.gdx.states;

import ca.blarg.gdx.Strings;
import ca.blarg.gdx.Strings;

class StateInfo {
	public final GameState state;
	public final String name;

	public boolean isOverlay;
	public boolean isOverlayed;
	public boolean isTransitioning;
	public boolean isTransitioningOut;
	public boolean isTransitionStarting;
	public boolean isInactive;
	public boolean isBeingPopped;

	String descriptor;

	public StateInfo(GameState state, String name) {
		if (state == null)
			throw new IllegalArgumentException("state cannot be null.");

		this.state = state;
		this.name = name;
		isInactive = true;

		if (Strings.isNullOrEmpty(this.name))
			descriptor = this.state.getClass().getSimpleName();
		else
			descriptor = String.format("%s[%s]", this.state.getClass().getSimpleName(), this.name);
	}

	@Override
	public String toString() {
		return descriptor;
	}
}
