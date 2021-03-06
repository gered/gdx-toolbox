package ca.blarg.gdx;

import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.graphics.*;
import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.assets.textureatlas.TextureAtlasLoader;
import ca.blarg.gdx.states.StateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

public abstract class GameApp implements Disposable {
	public final AssetManager assetManager;
	public final EventManager eventManager;
	public final StateManager stateManager;
	public final ViewportContext viewportContext;
	public final ExtendedSpriteBatch spriteBatch;
	public final BillboardSpriteBatch billboardSpriteBatch;
	public final ModelBatch modelBatch;
	public final SolidColorTextureCache solidColorTextures;
	public final DebugGeometryRenderer debugGeometryRenderer;
	public final ShapeRenderer shapeRenderer;

	boolean logHeapMemUsage = false;
	long lastHeapMemLogTime = 0;

	public GameApp() {
		Gdx.app.debug("GameApp", "ctor");

		assetManager = new AssetManager();
		eventManager = new EventManager();
		stateManager = new StateManager(this, eventManager);
		viewportContext = new ViewportContext(true);
		spriteBatch = new ExtendedSpriteBatch();
		billboardSpriteBatch = new BillboardSpriteBatch();
		modelBatch = new ModelBatch();
		solidColorTextures = new SolidColorTextureCache();
		debugGeometryRenderer = new DebugGeometryRenderer();
		shapeRenderer = new ShapeRenderer();

		assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));

		Services.register(assetManager);
		Services.register(eventManager);
		Services.register(stateManager);
		Services.register(viewportContext);
		Services.register(spriteBatch);
		Services.register(billboardSpriteBatch);
		Services.register(modelBatch);
		Services.register(solidColorTextures);
		Services.register(debugGeometryRenderer);
		Services.register(shapeRenderer);
	}

	protected void toggleHeapMemUsageLogging(boolean enable) {
		logHeapMemUsage = enable;
		if (enable)
			lastHeapMemLogTime = TimeUtils.millis();
	}

	public abstract void onCreate();

	public void onResize(int width, int height) {
		Gdx.app.debug("GameApp", String.format("onResize(%d, %d)", width, height));
		viewportContext.onResize(width, height);
		stateManager.onResize();
	}

	public void onRender(float interpolation) {
		viewportContext.onPreRender();
		spriteBatch.setProjectionMatrix(viewportContext.getOrthographicCamera().combined);

		stateManager.onRender(interpolation);

		viewportContext.onPostRender();
	}

	public void onUpdateGameState(float delta) {
		eventManager.onUpdate(delta);
		stateManager.onUpdateGameState(delta);
		if (stateManager.isEmpty()) {
			Gdx.app.debug("GameApp", "No states running. Quitting.");
			Gdx.app.exit();
			return;
		}

		if (logHeapMemUsage) {
			long currentTime = TimeUtils.millis();
			if (currentTime - lastHeapMemLogTime > 1000) {
				lastHeapMemLogTime = currentTime;
				Gdx.app.debug("GameApp", String.format("Heap memory usage: %d", Gdx.app.getJavaHeap()));
			}
		}
	}

	public void onUpdateFrame(float delta) {
		viewportContext.onUpdateFrame(delta);
		stateManager.onUpdateFrame(delta);
	}

	public void onPause() {
		Gdx.app.debug("GameApp", "onPause");
		stateManager.onAppPause();
		viewportContext.onPause();
		solidColorTextures.onPause();
	}

	public void onResume() {
		Gdx.app.debug("GameApp", "onResume");
		viewportContext.onResume();
		solidColorTextures.onResume();
		stateManager.onAppResume();
	}

	@Override
	public void dispose() {
		Gdx.app.debug("GameApp", "dispose");
		stateManager.dispose();
		solidColorTextures.dispose();
		modelBatch.dispose();
		billboardSpriteBatch.dispose();
		spriteBatch.dispose();
	}
}
