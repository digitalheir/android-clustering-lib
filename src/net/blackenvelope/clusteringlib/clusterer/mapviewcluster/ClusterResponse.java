package net.blackenvelope.clusteringlib.clusterer.mapviewcluster;

import java.util.Collection;

import net.blackenvelope.clusteringlib.clusterer.mapviewcluster.ClusterTask.ClusterTaskCallback;

import com.google.android.maps.MapView;

public class ClusterResponse {
		private Collection<ClusterOverlayItem> items;
		private MapView mapView;
		private ClusterOverlay overlay;
		private ClusterTaskCallback callback;

		ClusterResponse(Collection<ClusterOverlayItem> i, ClusterOverlay ol,
				MapView mv, ClusterTaskCallback cb) {
			items = i;
			overlay = ol;
			mapView = mv;
			callback = cb;
		}

		public ClusterOverlay getOverlay() {
			return overlay;
		}

		public MapView getMapView() {
			return mapView;
		}

		public Collection<ClusterOverlayItem> getItems() {
			return items;
		}

		public ClusterTaskCallback getClusterCallBack() {
			return callback;
		}

}
