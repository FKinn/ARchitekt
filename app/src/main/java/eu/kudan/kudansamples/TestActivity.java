package eu.kudan.kudansamples;

import android.os.Bundle;
import android.util.Log;
import eu.kudan.kudan.*;

public class TestActivity extends ARActivity{

	public ARModelNode model;


	public void onCreate(Bundle savedInstanceState) {
		// set api key for this package name.
		ARAPIKey key = ARAPIKey.getInstance();
		key.setAPIKey("GAWAE-FBVCC-XA8ST-GQVZV-93PQB-X7SBD-P6V4W-6RS9C-CQRLH-78YEU-385XP-T6MCG-2CNWB-YK8SR-8UUQ");
		super.onCreate(savedInstanceState);
	}

	public void setup() {


		//TEST

		//setupModel();
		//setupArbiTrack();
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
		*/

		// load a set of trackables from a bundled file.
		ARTrackableSet trackableSet = new ARTrackableSet();
		trackableSet.loadFromAsset("demo.KARMarker");


		ARImageTracker tracker = ARImageTracker.getInstance();
		
		// add our trackables to the tracker.
		tracker.addTrackableSet(trackableSet);
		//tracker.addTrackable(wavesTrackable);
		
		// create an image node.
		ARImageTrackable legoTrackable = tracker.findTrackable("lego");
		ARImageNode imageNode = new ARImageNode("BatmanLegoMovie.png");
		
		// make it smaller.
		imageNode.scaleBy(0.5f, 0.5f, 0.5f);
		
		// add it to the lego trackable.
		legoTrackable.getWorld().addChild(imageNode);



	}



	private void setupModel(){

		// Import model from file.
		ARModelImporter modelImport = new ARModelImporter();
		modelImport.loadFromAsset("NeugereutModellTreppeRampe.jet");
		model = (ARModelNode) modelImport.getNode();


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
		ARArbiTrack slamTracker = new ARArbiTrack();
		slamTracker.initialise();

		// Set the arbiTracker target node to the node moved by the user.
		slamTracker.setTargetNode(targetNode);



		slamTracker.getWorld().addChild(model);


	}

	private void setupLabel() {

	}

	private void tapGesture() {
		synchronized (ARRenderer.getInstance()){
			//this.model.scaleByUniform();
		}
	}
	/*
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
	*/

}
