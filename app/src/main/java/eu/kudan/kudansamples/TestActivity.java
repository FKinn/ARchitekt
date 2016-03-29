package eu.kudan.kudansamples;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.jme3.math.Vector3f;
import eu.kudan.kudan.*;

public class TestActivity extends ARActivity implements GestureDetector.OnGestureListener{

	ARImageNode targetImageNode;
	ARModelNode modelNode;
	private GestureDetectorCompat gDetector;

	private ARBITRACK_STATE arbitrack_state;

	enum ARBITRACK_STATE {
		ARBI_STOPPED,
		ARBI_PLACEMENT,
		ARBI_TRACKING
	}


	public void onCreate(Bundle savedInstanceState) {
		// gesture
		this.gDetector = new GestureDetectorCompat(this,this);

		// set api key for this package name.
		ARAPIKey key = ARAPIKey.getInstance();
		key.setAPIKey("GAWAE-FBVCC-XA8ST-GQVZV-93PQB-X7SBD-P6V4W-6RS9C-CQRLH-78YEU-385XP-T6MCG-2CNWB-YK8SR-8UUQ");
		super.onCreate(savedInstanceState);
		this.arbitrack_state = ARBITRACK_STATE.ARBI_PLACEMENT;


	}

	public void setup() {


		//TEST
		super.setup();
		setupModel();
		setupArbiTrack();


	}


	private void setupModel(){

		ARModelImporter importer = new ARModelImporter();
		importer.loadFromAsset("bloodhound.jet");
		this.modelNode = (ARModelNode) importer.getNode();
		ARTexture2D texture2D = new ARTexture2D();
		texture2D.loadFromAsset("bloodhound.png");
		ARLightMaterial material = new ARLightMaterial();
		material.setTexture(texture2D);
		material.setDiffuse(0.2f, 0.2f, 0.2f);
		material.setAmbient(0.8f, 0.8f, 0.8f);
		material.setSpecular(0.3f, 0.3f, 0.3f);
		material.setShininess(20.0f);
		material.setReflectivity(0.15f);
		//Vector3f lightDirection = new Vector3f(0.0f, -1.0f, 0.0f);
		for (ARMeshNode meshNode : importer.getMeshNodes()) {
			meshNode.setMaterial(material);
			//meshNode.setLightDirection(lightDirection);
		}

		this.modelNode.scaleByUniform(6.0f);
		this.modelNode.setVisible(true);

	}

	private void setupArbiTrack(){

		ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
		gyroPlaceManager.initialise();
		this.targetImageNode = new ARImageNode("target.png");
		gyroPlaceManager.getWorld().addChild(this.targetImageNode);
		ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
		arbiTrack.initialise();
		arbiTrack.setTargetNode(this.targetImageNode);
		this.targetImageNode.scaleByUniform(0.3f);
		this.targetImageNode.setVisible(true);
		arbiTrack.getTargetNode().rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
		arbiTrack.getWorld().addChild(this.modelNode);
	}


	private void tapGesture() {

		ARArbiTrack arbiTrack = ARArbiTrack.getInstance();

		if(arbitrack_state == ARBITRACK_STATE.ARBI_PLACEMENT){
			arbiTrack.getTargetNode().setVisible(false);
			this.modelNode.setPosition(arbiTrack.getWorld().getFullTransform().invert().mult(this.modelNode.getFullTransform().mult(new Vector3f(0.0f, 0.0f, 0.0f))));
			arbiTrack.getWorld().setVisible(true);
			arbiTrack.start();
			//b.setText("Stop Tracking");
			this.arbitrack_state = ARBITRACK_STATE.ARBI_TRACKING;

		} else if(arbitrack_state == ARBITRACK_STATE.ARBI_TRACKING){
			arbiTrack.getTargetNode().setPosition(arbiTrack.getWorld().getFullTransform().invert().mult(this.modelNode.getFullTransform().mult(new Vector3f(0.0f, 0.0f, 0.0f))));
			arbiTrack.getTargetNode().setVisible(true);
			arbiTrack.stop();
			//b.setText("Start Tracking");
			this.arbitrack_state = ARBITRACK_STATE.ARBI_PLACEMENT;
		}
	}

	private void pinchGesture(){


	}

	private void panGesture(float distanceX){

		this.modelNode.rotateByDegrees(distanceX, 0.0f, -1.0f, 0.0f);


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
		tapGesture();
		Log.i("KudanSamples", "SingleTap");
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		panGesture(distanceX);
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

		this.gDetector.onTouchEvent(event);
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
