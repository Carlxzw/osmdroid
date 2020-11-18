package org.osmdroid.util;

import android.graphics.Rect;
import android.util.Log;

/**
 * A class that will loop around all the map tiles in the given viewport.
 */
public abstract class TileLooper {

	protected final Rect mTiles = new Rect();
	protected int mTileZoomLevel;
	private boolean horizontalWrapEnabled = true;
	private boolean verticalWrapEnabled = true;

	public TileLooper() {
		this(false, false);
	}

	public TileLooper(boolean horizontalWrapEnabled, boolean verticalWrapEnabled) {
		this.horizontalWrapEnabled = horizontalWrapEnabled;
		this.verticalWrapEnabled = verticalWrapEnabled;
	}

	protected void loop(final double pZoomLevel, final RectL pMercatorViewPort) {
		//这里把屏幕像素区间转为瓦片行列号区间
		TileSystem.getTileFromMercator(pMercatorViewPort, TileSystem.getTileSize(pZoomLevel), mTiles);
		mTileZoomLevel = TileSystem.getInputTileZoomLevel(pZoomLevel);

		initialiseLoop();

		//1 << mTileZoomLevel == 1 * 2^i
		final int mapTileXUpperBound = 1 << mTileZoomLevel;
		//xzw 如果这里不限制行的数量，则地图只有一半会有地图显示
//		final int mapTileYUpperBound = MyMath.floorToInt((1 << mTileZoomLevel) /2);
		final int mapTileYUpperBound = (1 << mTileZoomLevel);
		/* Draw all the MapTiles (from the upper left to the lower right). */
		for (int i = mTiles.left ; i <= mTiles.right ; i ++) {
			for (int j = mTiles.top; j <= mTiles.bottom ; j ++) {
				if ((horizontalWrapEnabled || (i >= 0 && i < mapTileXUpperBound)) && (verticalWrapEnabled
						|| (j >= 0 && j < mapTileYUpperBound))) {
					final int tileX = MyMath.mod(i, mapTileXUpperBound);
					final int tileY = MyMath.mod(j, mapTileYUpperBound);
					final long tile = MapTileIndex.getTileIndex(mTileZoomLevel, tileX, tileY);
					handleTile(tile, i, j);
				}
			}
		}
		finaliseLoop();
	}

	public void initialiseLoop() {}

	public abstract void handleTile(final long pMapTileIndex, final int pX, final int pY);

	public void finaliseLoop() {}

	public boolean isHorizontalWrapEnabled() {
		return horizontalWrapEnabled;
	}

	public void setHorizontalWrapEnabled(boolean horizontalWrapEnabled) {
		this.horizontalWrapEnabled = horizontalWrapEnabled;
	}

	public boolean isVerticalWrapEnabled() {
		return verticalWrapEnabled;
	}

	public void setVerticalWrapEnabled(boolean verticalWrapEnabled) {
		this.verticalWrapEnabled = verticalWrapEnabled;
	}

}
