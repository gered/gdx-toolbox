package ca.blarg.gdx.assets;

public class AssetLoadingException extends RuntimeException {
	String assetFilename;
	String detailMessage;

	public AssetLoadingException(String assetFilename, String message) {
		super(message);
		this.assetFilename = assetFilename;
		setDetailMessage(assetFilename, message);
	}

	public AssetLoadingException(String assetFilename, String message, Throwable cause) {
		super(message, cause);
		this.assetFilename = assetFilename;
		setDetailMessage(assetFilename, message);
	}

	private void setDetailMessage(String assetFilename, String message) {
		detailMessage = String.format("%s: %s", assetFilename, message);
	}

	@Override
	public String getMessage() {
		return detailMessage;
	}

	public String getAssetFilename() {
		return assetFilename;
	}
}
