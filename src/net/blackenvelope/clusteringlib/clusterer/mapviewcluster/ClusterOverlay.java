package net.blackenvelope.clusteringlib.clusterer.mapviewcluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

public abstract class ClusterOverlay extends
		BalloonItemizedOverlay<ClusterOverlayItem> {
	private static final String TAG = "ClusterOverlay";

	private static final int DEFAULT_RADIUS = 10;
	private OverlayItemType type;
	// TODO toch nog concurrency issues, vooral als je snel inzoomt en uitzoomt
	// en dingetjes met je vingers doet
	private volatile List<ClusterOverlayItem> allCopies = new ArrayList<ClusterOverlayItem>();
	private volatile List<ClusterOverlayItem> allItems = new ArrayList<ClusterOverlayItem>();
	private volatile List<ClusterOverlayItem> masterItems = new ArrayList<ClusterOverlayItem>();

	// Fields for drawing markers:
	private Point pointMarker = new Point();
	private Point pointO = new Point();
	private Paint transparentPaint;
	private Paint innerCirclePaint;
	private Paint strokePaint;

	private Paint fogPaint = new Paint();
	private Paint whitePaint = new Paint();


	volatile Collection<ClusterOverlayItem> toDraw = new HashSet<ClusterOverlayItem>();
	private boolean clusterInvalidated = false;

	private ClusterTask task;

	
	public void clusterInvalidate(){
		clusterInvalidated = true;
	}
	
	public boolean isClusterInvalidated(){
		return clusterInvalidated;
	}
	
	public void setTask(ClusterTask taks){
		this.task = taks;
	}
	
	public ClusterTask getTask() {
		return task;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// Don't draw shadow
		
		if(isClusterInvalidated()){
			clusterInvalidated = false;
			recluster();
		}
		
		if (!shadow) {
			// TODO pre-render circles if possible?
			toDraw.clear();
			for (ClusterOverlayItem m : masterItems) {
				if (m.isSlave()) {
					Log.e(TAG, "masterItems contained a slave while it should not. ");
				}
				Projection proj = mapView.getProjection();
				Point p = proj.toPixels(m.getMedian(), null);

				if (Utils.isWithinView(p, mapView, DEFAULT_RADIUS)) {
					toDraw.add(m);
				}
			}

			// Log.w("DRAW", onScreen.size() + " and " + (mClusterOverlays ==
			// null));
			Projection projection = mapView.getProjection();
			// Draw clusters
			for (ClusterOverlayItem marker : toDraw) {
				// Log.w("Overlay", "Drawing clusters");
				drawClusterMarker(canvas, mapView, marker, projection);
			}
		}
	}

	private void recluster() {
		ClusterTask task = new ClusterTask(getOnCanceltask(this));
		overlay.setTask(task);
		asyncTaskStarted();
		task.execute(new ClusterInput(mapView, overlay, overlayDataO
				.getClusterRadius(), clusterTaskCallback));
	}

	private void drawClusterMarker(Canvas canvas, MapView mapView,
			ClusterOverlayItem marker, Projection projection) {
		GeoPoint geoPoint = marker.getMedian();
		projection.toPixels(geoPoint, pointMarker);

		int pixRadius = Utils.toPixels(mapView, (int) marker.getRadius(),
				pointO);
		int fogRadius = pixRadius < DEFAULT_RADIUS ? DEFAULT_RADIUS : pixRadius;

		// projection.toPixels(arg0, arg1)
		// TODO solid and stroke... no better solution?
		
		//transparent circle
		canvas.drawCircle(pointMarker.x, pointMarker.y, fogRadius, transparentPaint);
		//opaque centre
		canvas.drawCircle(pointMarker.x, pointMarker.y, DEFAULT_RADIUS/3, innerCirclePaint);
		//stroke
		canvas.drawCircle(pointMarker.x, pointMarker.y, fogRadius, strokePaint);
		
		// Log.w("X",pointC.x+ "");
		// canvas.drawText(marker.getClusterSize() + "", pointMarker.x,
		// pointMarker.y, textPaint);

		// projection.toPixels(marker.getPoint(), pointMarker);
		// canvas.drawCircle(pointMarker.x, pointMarker.y,
		// LocationsActivity.RADIUS_PIXELS, fogPaint);
		//
		// int radius = Utils.toPixels(mapView,
		// (int)Math.round(marker.debugRadius), pointO);
		// canvas.drawCircle(pointMarker.x, pointMarker.y, radius, whitePaint);
		//
		// for (ClusterOverlayItem slave : marker.getSlaves()) {
		//
		// GeoPoint slavePoint = slave.getPoint();
		// projection.toPixels(slavePoint, pointO);
		//
		// canvas.drawLine(pointMarker.x, pointMarker.y, pointO.x, pointO.y,
		// blackPaint);
		// }
	}

	public ClusterOverlay(Drawable defaultMarker, MapView mapView,
			OverlayItemType type) {
		super(defaultMarker, mapView);
		this.type = type;

		this.transparentPaint = new Paint();
		this.transparentPaint.setAntiAlias(true);
		this.transparentPaint.setColor(type.getColor());
		this.transparentPaint.setAlpha(40);
		
		this.innerCirclePaint = new Paint();
		this.innerCirclePaint.setAntiAlias(true);
		this.innerCirclePaint.setAlpha(80);
		this.innerCirclePaint.setColor(type.getColor());
		// Stroke color is not editable seperate of fill
		// this.solidPaint.setStyle(Style.FILL_AND_STROKE);

		this.strokePaint = new Paint();
		this.strokePaint.setAntiAlias(true);
		this.strokePaint.setStyle(Style.STROKE);
		this.strokePaint.setColor(type.getColor());

		this.fogPaint.setColor(Color.BLACK);
		this.fogPaint.setAlpha(15);

		this.whitePaint.setColor(Color.WHITE);
		this.whitePaint.setAlpha(100);
	}

	public OverlayItemType getType() {
		return type;
	}

	public void setType(OverlayItemType type) {
		this.type = type;
	}

	public List<ClusterOverlayItem> getCopies() {
		return allCopies;
	}

	synchronized public void switchCopies() {
		List<ClusterOverlayItem> temp = allItems;
		allItems = allCopies;
		allCopies = temp;
		masterItems.clear();
		for (ClusterOverlayItem it : allItems) {
			if (!it.isSlave()) {
				masterItems.add(it);
			}
		}
		Log.v(TAG, masterItems.size() + " masters and "
				+ (allItems.size() - masterItems.size()) + " slaves.");
		 populate();
	}

	@Override
	protected ClusterOverlayItem createItem(int i) {
		// return masterItems.get(i);
		return allItems.get(i);
	}

	@Override
	public int size() {
		// return masterItems.size();
		return allItems.size();
	}

	public void addOverlay(ClusterOverlayItem item) {
		allItems.add(item);
		ClusterOverlayItem copy = new ClusterOverlayItem(item.getPoint(),
				item.getTitle(), item.getAddress());
		allCopies.add(copy);

		populate();
	}

	// public void setDrawItems(List<ClusterOverlayItem> items) {
	// allItems = items;
	// }

	public void addOverlays(Collection<ClusterOverlayItem> items) {
		Log.v(TAG, "Adding " + items.size() + " items to "
				+ getType().toString() + " layer.");
		allItems.addAll(items);
		for (ClusterOverlayItem item : items) {
			ClusterOverlayItem copy = new ClusterOverlayItem(item.getPoint(),
					item.getTitle(), item.getAddress());
			allCopies.add(copy);
		}
		populate();
	}

	@Override
	public String toString() {
		return getType().toString();
	}

	// Unused:

	// private void drawAll(Canvas canvas, MapView mapView,
	// ClusterOverlayItem marker, Projection projection) {
	// drawMarker(canvas, marker, marker.getPoint(), projection);
	// for (ClusterOverlayItem ovi : marker.getSlaves()) {
	// drawMarker(canvas, ovi, ovi.getPoint(), projection);
	// }
	// }
	//
	// private void drawMarker(Canvas canvas, ClusterOverlayItem marker,
	// GeoPoint geoPoint, Projection projection) {
	// projection.toPixels(geoPoint, pointMarker);
	//
	// // TODO: radius fog
	// canvas.drawCircle(pointMarker.x, pointMarker.y, DEFAULT_RADIUS,
	// solidPaint);
	// }

}
