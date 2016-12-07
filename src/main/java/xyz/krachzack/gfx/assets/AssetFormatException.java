package xyz.krachzack.gfx.assets;

public class AssetFormatException extends RuntimeException {

	private static final long serialVersionUID = 2160850647557416598L;

	public AssetFormatException() {
	}

	public AssetFormatException(String message) {
		super(message);
	}

	public AssetFormatException(Throwable cause) {
		super(cause);
	}

	public AssetFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssetFormatException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
