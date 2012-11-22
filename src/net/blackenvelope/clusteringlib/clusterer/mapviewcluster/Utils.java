package net.blackenvelope.clusteringlib.clusterer.mapviewcluster;

import android.graphics.Color;
import android.graphics.Point;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class Utils {

	/**
	 * Private contructor: utility class should not be instantiated
	 */
	private Utils(){
	}
	
	public static float eucledianDistance(float x1, float y1, float x2,
			float y2) {
		return android.util.FloatMath.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
	}

	public static float getEucledianDistance(GeoPoint a, GeoPoint b) {
		return eucledianDistance(a.getLatitudeE6(), a.getLongitudeE6(),
				b.getLatitudeE6(), b.getLongitudeE6());
	}

	public static float getEucledianDistance(Point a, Point b) {
		return eucledianDistance(a.x, a.y, b.x, b.y);
	}

	public static int fromPixels(MapView mapView, int lengthPx) {
		Projection proj = mapView.getProjection();
		GeoPoint gp = proj.fromPixels(lengthPx, 0);
		GeoPoint upperLeft = proj.fromPixels(0, 0);
		int radius = gp.getLongitudeE6() - upperLeft.getLongitudeE6();
		return radius;
	}

	public static int toPixels(MapView mapView, int length, Point point) {
		if (length != 0) {
			Projection proj = mapView.getProjection();
			GeoPoint upperLeft = proj.fromPixels(0, 0);
			GeoPoint gp = new GeoPoint(upperLeft.getLatitudeE6(),
					upperLeft.getLongitudeE6() + length);
			Point p = proj.toPixels(gp, point);

			return Math.abs(p.x);
		}
		return 0;
	}

	public static boolean isWithinView(Point p, View view) {
		return isWithinView(p, view, 0);
	}
	
	public static boolean isWithinView(Point p, View view, int margin) {
		return (p.x+margin > 0 & p.x-margin < view.getWidth() & p.y+margin > 0 & p.y-margin < view
				.getHeight());
	}
	public static class Colors extends Color{
		
		public static final int DARK_GREY = Color.rgb(105, 105, 105);
		public static final int FOREST_GREEN = Color.rgb(0, 135, 18);
		public static final int GOLD = Color.rgb(235, 176, 0);
		public static final int ORANGE = Color.rgb(255, 165, 0);
		public static final int DARK_ORANGE = Color.rgb(255, 140, 0);
		public static final int BROWN = Color.rgb(139,69, 19);
		public static final int PURPLE = Color.rgb(130,0, 130);
	}
}
