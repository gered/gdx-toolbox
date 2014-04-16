package ca.blarg.gdx.io;

import ca.blarg.gdx.Strings;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public final class FileHelpers {
	public static String getPath(FileHandle file) {
		return getPath(file.path());
	}

	public static String getPath(String filename) {
		int pos = filename.lastIndexOf('/');
		if (pos == -1)
			return "";
		else
			return filename.substring(0, pos + 1);
	}

	public static boolean hasPath(String filename) {
		return filename.contains("/");
	}

	public static String combine(String path, String filename) {
		if (filename == null)
			throw new IllegalArgumentException("filename cannot be null.");

		if (Strings.isNullOrEmpty(path))
			return filename;

		if (path.endsWith("/")) {
			if (filename.startsWith("/"))
				return path + filename.substring(1);
			else
				return path + filename;
		} else {
			if (filename.startsWith("/"))
				return path + filename;
			else
				return path + "/" + filename;
		}
	}

	public static FileHandle open(Files.FileType type, String path) {
		if (path == null)
			throw new IllegalArgumentException("path can not be null.");

		switch (type) {
			case Classpath: return Gdx.files.classpath(path);
			case Internal:  return Gdx.files.internal(path);
			case External:  return Gdx.files.external(path);
			case Absolute:  return Gdx.files.absolute(path);
			case Local:     return Gdx.files.local(path);
			default:        throw new UnsupportedOperationException();
		}
	}
}
