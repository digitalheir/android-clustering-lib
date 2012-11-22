package net.blackenvelope.clusteringlib.clusterer.mapviewcluster;

import java.util.Collection;
import java.util.HashSet;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


public class ClusterOverlayItem extends OverlayItem {
	private float radius;
	public Collection<ClusterOverlayItem> slaves = new HashSet<ClusterOverlayItem>();
	private GeoPoint median;
	private ClusterOverlayItem master = null;
	public double debugRadius;

	public ClusterOverlayItem(GeoPoint point, String type, String address) {
		this(point, type, address, null);
	}

	public ClusterOverlayItem(GeoPoint point, String type, String address,
			Collection<ClusterOverlayItem> slaves) {
		super(point, type, address);
		if (slaves != null) {
			this.slaves.addAll(slaves);
		}
		updateMedian();
	}

	public float getRadius() {
		return radius;
	}

	public static GeoPoint GetPointFromDouble(double lat, double lng) {
		return new GeoPoint((int) (lat * 1e6), (int) (lng * 1e6));
	}

	public Collection<ClusterOverlayItem> getSlaves() {
		return slaves;
	}

	private synchronized void updateMedian() {
		if (slaves == null || slaves.size() == 0) {
			median = getPoint();
			radius = 0;
		} else {
			int n = 1;
			n += slaves.size();
			int meanLat = (int) (getPoint().getLatitudeE6() / n);
			int meanLong = (int) (getPoint().getLongitudeE6() / n);

			for (ClusterOverlayItem slave : slaves) {
				GeoPoint p = slave.getPoint();
				meanLat += (int) Math.round(p.getLatitudeE6() / n);
				meanLong += (int) Math.round(p.getLongitudeE6() / n);
			}

			float maxDist = 0;
			for (ClusterOverlayItem slave : slaves) {
				GeoPoint p = slave.getPoint();

				double dist = Utils.getEucledianDistance(p, median);
				if (dist > maxDist) {
					maxDist = Math.round(dist);
				}
			}
			radius = maxDist;

			median = new GeoPoint(meanLat, meanLong);
		}
	}

	public GeoPoint getMedian() {
		return median;
	}

	private void addChildSlaves(ClusterOverlayItem getChildrenFrom) {
		this.slaves.addAll(getChildrenFrom.getSlaves());
		for (ClusterOverlayItem subSlaves : getChildrenFrom.getSlaves()) {
			// Not recursive
			subSlaves.setMaster(this);
		}
		getChildrenFrom.clearSlaves();
	}

	private void clearSlaves() {
		slaves.clear();
	}

	public synchronized void addSlaves(Collection<ClusterOverlayItem> slaves2) {
		this.master = null;// should be redundant
		slaves.addAll(slaves2);
		for (ClusterOverlayItem s : slaves2) {
			s.setMaster(this);
			addChildSlaves(s);
		}
		updateMedian();
	}

	public synchronized void addSlave(ClusterOverlayItem slave) {
		this.master = null;// should be redundant
		slaves.add(slave);
		slave.setMaster(this);
		addChildSlaves(slave);
		// Log.w("EUH", "now has "+slaves.size()+" slaves");
		updateMedian();
	}

	private void setMaster(ClusterOverlayItem master) {
		this.master = master;
	}

	public boolean isSlave() {
		return (master != null);
	}

	public synchronized void clearCluster() {
		slaves.clear();
		setMaster(null);
		updateMedian();
	}

	public String getAddress() {
		return getSnippet();
	}

	public int getClusterSize() {
		return (1 + slaves.size());
	}

	@Override
	public String toString() {
		return getTitle() + "\n" + getAddress() + "\n" + getSnippet();
	}

}