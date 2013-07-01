package com.blarg.gdx.processes;

import com.blarg.gdx.Strings;

class ProcessInfo {
	public final GameProcess process;
	public final String name;

	public boolean isTransitioning;
	public boolean isTransitioningOut;
	public boolean isTransitionStarting;
	public boolean isInactive;
	public boolean isBeingRemoved;

	String descriptor;

	public ProcessInfo(GameProcess process, String name) {
		if (process == null)
			throw new IllegalArgumentException("process cannot be null.");

		this.process = process;
		this.name = name;
		isInactive = true;

		if (Strings.isNullOrEmpty(this.name))
			descriptor = this.process.getClass().getSimpleName();
		else
			descriptor = String.format("%s[%s]", this.process.getClass().getSimpleName(), this.name);
	}

	@Override
	public String toString() {
		return descriptor;
	}
}
