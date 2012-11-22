package net.blackenvelope.clusteringlib.clusterer.mapviewcluster;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

/**
 * A MapView that supports double tap to zoom
 * 
 */
public class ClusterMapView extends DoubleTapMapView {

	public ClusterMapView(Context context, String api_key,
			OnMapZoomListener zoomCallback) {
		super(context, api_key, zoomCallback);
	}


}