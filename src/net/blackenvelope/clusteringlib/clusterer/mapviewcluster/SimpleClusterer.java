package net.blackenvelope.clusteringlib.clusterer.mapviewcluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import android.util.Log;

public class SimpleClusterer {
	private static final String TAG = "SimpleClusterer";

	/**
	 * @param clusterPoints
	 * @param clusterRadius
	 * @return
	 */
	//TODO synchronized?
	public synchronized static Collection<ClusterOverlayItem> clusterPoints(
			List<ClusterOverlayItem> clusterPoints, double clusterRadius) {
		List<ClusterOverlayItem> retVal = new ArrayList<ClusterOverlayItem>();
		// Make all items master
		for (ClusterOverlayItem item : clusterPoints) {
			item.clearCluster();
		}

		Log.v(TAG, "Clustering " + clusterPoints.size()
				+ " points with a geographical radius of " + clusterRadius
				+ "...");
		// Classify masters and slaves:

		// First iteration: half radius
		simpleCluster(clusterPoints, (clusterRadius) / 2, false);

		// Second iteration: full radius
		simpleCluster(clusterPoints, clusterRadius, false);

		// Check if the medians cross
		// for (int i = 0; i < clusterPoints.size(); i++) {
		// ClusterOverlayItem a = clusterPoints.get(i);
		// if (!a.isSlave()) {
		// for (int j = i + 1; j < clusterPoints.size(); j++) {
		// ClusterOverlayItem b = clusterPoints.get(j);
		// if (a != b & !b.isSlave()) {
		// if (Utils.getEucledianDistance(a.getMedian(),
		// b.getMedian()) < clusterRadius) {
		// // TODO gebruik addSlaves()?
		// a.addSlave(b);
		// break;
		// }
		// }
		// }
		// }
		// }

		// Copy masters to retVal
		for (ClusterOverlayItem item : clusterPoints) {
			if (!item.isSlave()) {
				retVal.add(item);
			}
		}
		return retVal;

		// return neverEndingAlgorithm(clusterPoints, clusterRadius, retVal, i);
	}

	public static void simpleCluster(List<ClusterOverlayItem> clusterPoints,
			double clusterRadius, boolean allowClustersToBeClustered) {
		for (int i = 0; i < clusterPoints.size(); i++) {
			ClusterOverlayItem a = clusterPoints.get(i);
			if (!a.isSlave()) {
				a.debugRadius = clusterRadius;
				Collection<ClusterOverlayItem> slavesToAdd = new HashSet<ClusterOverlayItem>();
				for (int j = i + 1; j < clusterPoints.size(); j++) {
					ClusterOverlayItem b = clusterPoints.get(j);
					if (a != b & !b.isSlave()) {
						if (allowClustersToBeClustered
								| (!allowClustersToBeClustered & b
										.getClusterSize() <= 1)) {
							// TODO median of point? kan allebei gigantische
							// cirkels maken... Misschien tweede iteratie alleen
							// guys die niet in een cluster zijn EN zelf geen
							// master zijn (is al gefixt?)
							if (Utils.getEucledianDistance(a.getMedian(),
									b.getMedian()) < clusterRadius) {
								slavesToAdd.add(b);
							}
						}
					}
				}
				a.addSlaves(slavesToAdd);
			}
		}
	}

	@SuppressWarnings("unused")
	private static void neverEndingAlgorithm(
			Collection<ClusterOverlayItem> clusterPoints, double clusterRadius,
			Collection<ClusterOverlayItem> retVal, int i) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (ClusterOverlayItem item : clusterPoints) {
				for (ClusterOverlayItem compare : clusterPoints) {
					i++;
					if (i % 100000000 == 0) {
						Log.w(TAG, i + "");
					}
					if (item != compare & !compare.isSlave()) {
						if (Utils.getEucledianDistance(item.getMedian(),
								compare.getMedian()) < clusterRadius) {
							// TODO gebruik addSlaves()?
							item.addSlave(compare);
							changed = true;
							break;
						}
					}
				}
				if (changed) {
					break;
				}
			}
		}
	}
}




