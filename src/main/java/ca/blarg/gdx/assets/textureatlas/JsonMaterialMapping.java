package ca.blarg.gdx.assets.textureatlas;

import java.util.ArrayList;

public class JsonMaterialMapping {
	public class Material {
		public String name;
		public int tile;
		public float minU;
		public float maxU;
		public float minV;
		public float maxV;
	}

	public ArrayList<Material> materials;
}
