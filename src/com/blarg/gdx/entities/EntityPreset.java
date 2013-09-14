package com.blarg.gdx.entities;

public abstract class EntityPreset {
	public interface CreationArgs {
	}

	public final EntityManager entityManager;

	public EntityPreset(EntityManager entityManager) {
		if (entityManager == null)
			throw new IllegalArgumentException("entityManager can not be null.");

		this.entityManager = entityManager;
	}

	public abstract Entity create(CreationArgs args);
}
