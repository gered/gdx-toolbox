package ca.blarg.gdx.assets;

import ca.blarg.gdx.Strings;
import ca.blarg.gdx.io.FileHelpers;

public class AssetLoadingUtils {
	public static String addPathIfNone(String filename, String path) {
		if (Strings.isNullOrEmpty(filename))
			return filename;

		if (FileHelpers.hasPath(filename))
			return filename;
		else
			return FileHelpers.combine(path, filename);
	}
}
