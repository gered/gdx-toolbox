package com.blarg.gdx.entities;

import com.blarg.gdx.entities.systemcomponents.EntityPresetComponent;

// Yes, this class SHOULD be marked final. No, you ARE wrong for wanting to subclass this.
// There IS a better way to do what you were thinking of doing that DOESN'T involve
// subclassing Entity!
// Still don't agree with me? Read this (or really, any other article on entity systems in games):
// http://t-machine.org/index.php/2010/05/09/entity-system-1-javaandroid/

public final class Entity {
	EntityManager entityManager;

	/**
	 * Do not instantiate Entity's directly. Use EntityManager.add().
	 */
	public Entity(EntityManager entityManager) {
		if (entityManager == null)
			throw new IllegalArgumentException("entityManager can not be null.");

		this.entityManager = entityManager;
	}

	public <T extends Component> T get(Class<T> componentType) {
		return entityManager.getComponent(componentType, this);
	}

	public <T extends Component> T add(Class<T> componentType) {
		return entityManager.addComponent(componentType, this);
	}

	public <T extends Component> void remove(Class<T> componentType) {
		entityManager.removeComponent(componentType, this);
	}

	public <T extends Component> boolean has(Class<T> componentType) {
		return entityManager.hasComponent(componentType, this);
	}

	public boolean wasCreatedViaPreset() {
		return entityManager.hasComponent(EntityPresetComponent.class, this);
	}

	public Class<? extends EntityPreset> getPresetUsedToCreate() {
		EntityPresetComponent presetComponent = entityManager.getComponent(EntityPresetComponent.class, this);
		if (presetComponent != null)
			return presetComponent.presetType;
		else
			return null;
	}
}
