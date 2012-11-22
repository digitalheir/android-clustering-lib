package net.blackenvelope.clusteringlib.clusterer.mapviewcluster;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

public class DoubleTapMapView extends MapView {
//	public interface OnLongpressListener {
//		public void onLongpress(Context context, MapView view,
//				GeoPoint longpressLocation);
//	}

	int oldZoomLevel = -1;
	private long lastTouchTime = -1;

	private OnMapZoomListener zoomCallback;

	// Fields for double tapping:
	private static final int DOUBLE_TAP_TIME = 250;
	@SuppressWarnings("unused")
	private static final String TAG = "MapView";

	// Constructors:
	public DoubleTapMapView(Context context, String api_key, OnMapZoomListener zoomCallback) {
		super(context, api_key);
		this.zoomCallback = zoomCallback;
	}

	// Methods:
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO: moet dit niet in onTouchEvent?
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			long thisTime = System.currentTimeMillis();
			// check double tap
			if (thisTime - lastTouchTime < DOUBLE_TAP_TIME) {
				// Double tap
				this.getController().zoomInFixing((int) ev.getX(),
						(int) ev.getY());
				lastTouchTime = -1;
			} else {
				// Too slow for double tap
				lastTouchTime = thisTime;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		checkLongpress(event);
		return super.onTouchEvent(event);
	}

//	private void checkLongpress(final MotionEvent event) {
//		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Finger has touched screen, check for longpress
			//TODO also checks if touched zoom controls :(
//			longpressTimer = new Timer();
//			longpressTimer.schedule(new TimerTask() {
//				@Override
//				public void run() {
//					GeoPoint longpressLocation = getProjection().fromPixels(
//							(int) event.getX(), (int) event.getY());
//					longpressListener.onLongpress(getContext(),
//							DoubleTapMapView.this, longpressLocation);
//				}
//			}, LONGPRESS_THRESHOLD);
//			lastMapCenter = getMapCenter();
//		}
//		if (event.getAction() == MotionEvent.ACTION_MOVE) {
//			if (!getMapCenter().equals(lastMapCenter)) {
//				longpressTimer.cancel();
//			}
//			lastMapCenter = getMapCenter();
//		}
//		if (event.getAction() == MotionEvent.ACTION_UP) {
//			longpressTimer.cancel();
//		}
//
//		if (event.getPointerCount() > 1) {
			// Multi-touch event
//			longpressTimer.cancel();
//		}
//	}

//	public void setOnLongpressListener(
//			DoubleTapMapView.OnLongpressListener listener) {
//		longpressListener = listener;
//	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		int newZoomLevel = getZoomLevel();
		if (newZoomLevel != oldZoomLevel) {
			zoomCallback.onZoom(oldZoomLevel, newZoomLevel);
			oldZoomLevel = newZoomLevel;
		}
	}

	public interface OnMapZoomListener {
		public void onZoom(int oldZoomLevel, int newZoomLevel);
	}
}
