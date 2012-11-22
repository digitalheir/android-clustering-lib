package net.blackenvelope.clusteringlib.clusterer.mapviewcluster;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.MapView;

public class ClusterTask extends AsyncTask<ClusterInput, Void, ClusterResponse> {

	private static final String TAG = "ClusterTask";
	private ClusterTaskCallback onCancel;

	public ClusterTask(ClusterTaskCallback onCancel) {
		this.onCancel =onCancel;
	}
	
	@Override
	protected ClusterResponse doInBackground(ClusterInput... inputs) {
		
		for (ClusterInput input : inputs) {
			ClusterOverlay overlayItem = input.getOverlay();
			MapView mapView = input.getMapView();
			int clusterRadiusPix = input.getClusterRadius();
			// Setup radius
			// inout.getRadius()
			int radius = Utils.fromPixels(mapView,
					(int) Math.round(clusterRadiusPix * input.getFactor()));
			// Log.v(TAG,
			// "Radius: "+RADIUS_PIXELS+", after conversion:"+Utils.toPixels(mapView,
			// radius, new Point()));
			// Log.v("gp","("+gp.getLatitudeE6()+", "+gp.getLongitudeE6()+")");
			// Log.v("upperLeft","("+upperLeft.getLatitudeE6()+", "+upperLeft.getLongitudeE6()+")");
			if (isCancelled()) {
				return null;
			}
			// Cluster points with clusterer
			return new ClusterResponse(SimpleClusterer.clusterPoints(
					overlayItem.getCopies(), radius), overlayItem, mapView, input.getClusterCallback());
			
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Void... unused) {

	}

	@Override
	protected void onPostExecute(ClusterResponse sResponse) {
		// if(mapView.isShown()){
		// Toast.makeText(mapView.getContext(), "Done",
		// Toast.LENGTH_SHORT).show();
		// }

		ClusterOverlay overlay = sResponse.getOverlay();
		 overlay.switchCopies();
		 sResponse.getMapView().postInvalidate();
		 
		 sResponse.getClusterCallBack().onClusterDone(sResponse);
		 
	}
	
	@Override
	protected void onCancelled(ClusterResponse result ) {
		Log.v(TAG,"Clustering cancelled");
		onCancel.onClusterDone(result);
	}
	
	
	public interface ClusterTaskCallback{
		public void onClusterDone(ClusterResponse response);
	}
}
