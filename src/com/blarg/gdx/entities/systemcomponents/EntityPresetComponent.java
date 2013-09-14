package com.blarg.gdx.entities.systemcomponents;

import com.blarg.gdx.entities.Component;
import com.blarg.gdx.entities.EntityPreset;

public class EntityPresetComponent extends Component {
	public Class<? extends EntityPreset> presetType;

	@Override
	public void reset() {
		presetType = null;
	}
}
