package net.blackenvelope.clusteringlib.clusterer.mapviewcluster;

import net.blackenvelope.clusteringlib.clusterer.mapviewcluster.ClusterTask.ClusterTaskCallback;

import com.google.android.maps.MapView;

	public class ClusterInput {
		private MapView mapView;
		private ClusterOverlay overlay;
		private double factor;
		private int clusterRadius;
		private ClusterTaskCallback clusterCallback;

		public ClusterInput(MapView m, ClusterOverlay o, int clusterRadius, ClusterTaskCallback cb) {
			this(m, o, 1, clusterRadius, cb);
		}

		/**
		 * 
		 * @return desired cluster radius, in pixels
		 */
		public int getClusterRadius() {
			return clusterRadius;
		}

		public ClusterInput(MapView m, ClusterOverlay o, double factor,
				int clusterRadius, ClusterTaskCallback cb) {
			this.mapView = m;
			this.overlay = o;
			this.factor = factor;
			this.clusterRadius = clusterRadius;
			clusterCallback = cb;
		}

		public double getFactor() {
			return factor;
		}

		public ClusterOverlay getOverlay() {
			return overlay;
		}

		public MapView getMapView() {
			return mapView;
		}

		public ClusterTaskCallback getClusterCallback() {
			return clusterCallback;
		}
}
