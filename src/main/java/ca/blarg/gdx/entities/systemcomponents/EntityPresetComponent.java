package ca.blarg.gdx.entities.systemcomponents;

import ca.blarg.gdx.entities.Component;
import ca.blarg.gdx.entities.EntityPreset;

public class EntityPresetComponent extends Component {
	public Class<? extends EntityPreset> presetType;

	@Override
	public void reset() {
		presetType = null;
	}
}
