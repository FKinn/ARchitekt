package eu.kudan.kudansamples;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


import eu.kudan.kudan.*;

public class TestActivity extends ARActivity implements ARImageTrackableListener, GestureDetector.OnGestureListener{

	public ARModelNode modelNode;

	private GestureDetectorCompat gDetector;



	public void onCreate(Bundle savedInstanceState) {
		// gesture
		this.gDetector = new GestureDetectorCompat(this,this);




		// set api key for this package name.
		ARAPIKey key = ARAPIKey.getInstance();
		key.setAPIKey("GAWAE-FBVCC-XA8ST-GQVZV-93PQB-X7SBD-P6V4W-6RS9C-CQRLH-78YEU-385XP-T6MCG-2CNWB-YK8SR-8UUQ");
		super.onCreate(savedInstanceState);



	}

	public void setup() {


		//TEST

		//setupModel();
		setupArbiTrack();
		//setupLabel();



		/*
		// create a trackable from a bundled image.
		ARImageTrackable wavesTrackable = new ARImageTrackable("waves");
		wavesTrackable.loadFromAsset("waves.png");
		
		// create video texture.
		ARVideoTexture videoTexture = new ARVideoTexture();
		videoTexture.loadFromAsset("waves.mp4");
		ARVideoNode videoNode = new ARVideoNode(videoTexture);
		
		// add video to the waves trackable.
		wavesTrackable.getWorld().addChild(videoNode);


		// load a set of trackables from a bundled file.
		ARTrackableSet trackableSet = new ARTrackableSet();
		trackableSet.loadFromAsset("demo.KARMarker");


		ARImageTracker tracker = ARImageTracker.getInstance();
		
		// add our trackables to the tracker.
		tracker.addTrackableSet(trackableSet);
		tracker.addTrackable(wavesTrackable);
		
		// create an image node.
		ARImageTrackable legoTrackable = tracker.findTrackable("lego");
		ARImageNode imageNode = new ARImageNode("BatmanLegoMovie.png");
		
		// make it smaller.
		imageNode.scaleBy(0.5f, 0.5f, 0.5f);
		
		// add it to the lego trackable.
		legoTrackable.getWorld().addChild(imageNode);
		*/


	}



	private void setupModel(){

		// Import model from file.
		ARModelImporter modelImport = new ARModelImporter();
		modelImport.loadFromAsset("NeugereutModellTreppeRampe.jet");
		modelNode = (ARModelNode) modelImport.getNode();


	}

	private void setupArbiTrack(){

		// Initialise gyro placement. Gyro placement positions content on a virtual floor plane where the device is aiming.
		ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
		gyroPlaceManager.initialise();

		// Set up the target node on which the model is placed.
		ARNode targetNode = new ARNode();
		gyroPlaceManager.getWorld().addChild(targetNode);

		// Add a visual reticule to the target node for the user.
		ARImageNode targetImageNode = new ARImageNode("target.png");
		targetNode.addChild(targetImageNode);

		// Scale and rotate the image to the correct transformation.
		targetImageNode.scaleByUniform(0.1f);
		targetImageNode.rotateByDegrees(90, 1, 0, 0);

		// Initialise the arbiTracker, do not start until user placement.
		ARArbiTrack slamTracker = ARArbiTrack.getInstance();
		slamTracker.initialise();

		// Set the arbiTracker target node to the node moved by the user.
		slamTracker.setTargetNode(targetNode);



		//slamTracker.getWorld().addChild(modelNode);


	}

	private void setupLabel() {

	}

	private void tapGesture() {

		ARArbiTrack slamTracker = ARArbiTrack.getInstance();

		if(!slamTracker.getIsTracking()){
			slamTracker.start();
			slamTracker.getTargetNode().setVisible(false);
			this.modelNode.setScale(1,1,1);

		} else if(slamTracker.getIsTracking()){
			slamTracker.stop();
			slamTracker.getTargetNode().setVisible(true);

		}
	}

	private void pinchGesture(){


	}

	private void panGesture(){

		/*
		float x = 0; //float x = [gesture translationInView:self.cameraView].x; Objective C


		float diff = x - lastPanX;
		float deg = diff *0.5;
		synchronized (ARRenderer.getInstance()){
			this.modelNode.rotateByDegrees(deg,0,1,0);
		}
		*/
	}




	@Override
	public void didDetect(ARImageTrackable trackable) {
		Log.i("KudanSamples", "detected " + trackable.getName());
	}


	@Override
	public void didTrack(ARImageTrackable trackable) {
//		Log.i("KudanSamples", "tracked");
	}

	@Override
	public void didLose(ARImageTrackable trackable) {
		Log.i("KudanSamples", "lost " + trackable.getName());
	}


	//region Gesture listener
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.i("KudanSamples", "SingleTap");
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		//boolean handled = gestureScale.onTouchEvent(event);

		//if(!handled){
			this.gDetector.onTouchEvent(event);
		//}
		// Be sure to call the superclass implementation
		return super.onTouchEvent(event);
	}

	/*/Scale gesture
	@Override
	public boolean onScale(ScaleGestureDetector detector) {

		return false;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return false;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}
	*/
	//endregion

}
