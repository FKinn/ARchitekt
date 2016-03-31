package eu.kudan.kudansamples;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


import com.jme3.math.Vector3f;
import eu.kudan.kudan.*;

public class TestActivity extends ARActivity implements GestureDetector.OnGestureListener{

	ARImageNode targetImageNode;
	ARModelNode modelNode;
	private GestureDetectorCompat gDetector;
	private float lastScale = 0;
	private ARBITRACK_STATE arbitrack_state;
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;

	enum ARBITRACK_STATE {
        ARBI_POSITIONING,
		ARBI_SCALING,
		ARBI_PLACEMENT,
		ARBI_TRACKING
	}

	public void onCreate(Bundle savedInstanceState) {
		// gesture
		this.gDetector = new GestureDetectorCompat(this,this);

		mScaleDetector = new ScaleGestureDetector(this.getApplicationContext(), new ScaleListener());

		// set api key for this package name.
		ARAPIKey key = ARAPIKey.getInstance();
		key.setAPIKey("GAWAE-FBVCC-XA8ST-GQVZV-93PQB-X7SBD-P6V4W-6RS9C-CQRLH-78YEU-385XP-T6MCG-2CNWB-YK8SR-8UUQ");
		super.onCreate(savedInstanceState);
		this.arbitrack_state = ARBITRACK_STATE.ARBI_PLACEMENT;


	}


	public void setup() {

		super.setup();
		setupModel();
		setupArbiTrack();

	}


	private void setupModel(){
		//setupBloodhound();
		//setupWall();
		setupNeugereut();
        //setupFence();
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

	//region gesture methods
	private void tapGesture() {

		ARArbiTrack arbiTrack = ARArbiTrack.getInstance();

		if(arbitrack_state == ARBITRACK_STATE.ARBI_PLACEMENT){
			arbiTrack.getTargetNode().setVisible(false);
			this.modelNode.setPosition(arbiTrack.getWorld().getFullTransform().invert().mult(this.modelNode.getFullTransform().mult(new Vector3f(0.0f, 0.0f, 0.0f))));
			arbiTrack.getWorld().setVisible(true);
			arbiTrack.start();
			this.arbitrack_state = ARBITRACK_STATE.ARBI_TRACKING;

		} else if(arbitrack_state == ARBITRACK_STATE.ARBI_TRACKING){
			arbiTrack.getTargetNode().setPosition(arbiTrack.getWorld().getFullTransform().invert().mult(this.modelNode.getFullTransform().mult(new Vector3f(0.0f, 0.0f, 0.0f))));
			arbiTrack.getTargetNode().setVisible(true);
			arbiTrack.stop();
			this.arbitrack_state = ARBITRACK_STATE.ARBI_PLACEMENT;

		}
	}

	private void pinchGesture(float mScaleFactor){
		if(arbitrack_state == ARBITRACK_STATE.ARBI_SCALING) {

			float scaleFactor = mScaleFactor;

			scaleFactor = 1 - (this.lastScale - scaleFactor);

			lastScale = mScaleFactor;

			synchronized (ARRenderer.getInstance()){
				this.modelNode.scaleByUniform(scaleFactor);
			}
		}


	}

	private void panGesture(float distanceX,float distanceY){

		if(arbitrack_state == ARBITRACK_STATE.ARBI_TRACKING){
			synchronized (ARRenderer.getInstance()){
				this.modelNode.rotateByDegrees(distanceY, 0.0f, 1.0f, 0.0f);
                //this.modelNode.rotateByDegrees(distanceX, 1.0f, 0.0f, 0.0f);
			}
		}
		else if (arbitrack_state == ARBITRACK_STATE.ARBI_POSITIONING) {
            Vector3f position = this.modelNode.getPosition();
            synchronized (ARRenderer.getInstance()) {
                this.modelNode.setPosition(position.getX(), position.getY() + distanceY, position.getZ());
                this.modelNode.setPosition(position.getX() - distanceX, position.getY(), position.getZ());
            }
        }



	}
	//endregion

	//region gesture listener
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
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		panGesture(distanceX,distanceY);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
        if(arbitrack_state == ARBITRACK_STATE.ARBI_TRACKING){
            arbitrack_state = ARBITRACK_STATE.ARBI_POSITIONING;
        } else if (arbitrack_state == ARBITRACK_STATE.ARBI_POSITIONING) {
            arbitrack_state = ARBITRACK_STATE.ARBI_TRACKING;
        }
		//Log.i("KudanSamples", "LongPress");

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.mScaleDetector.onTouchEvent(event);
		this.gDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}


	private class ScaleListener
			extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {

			mScaleFactor *= detector.getScaleFactor();
			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
			pinchGesture(mScaleFactor);
			return true;

		}
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector){
			lastScale = 1;
			arbitrack_state = ARBITRACK_STATE.ARBI_SCALING;
			return true;
		}
		public void onScaleEnd(ScaleGestureDetector detector){
			arbitrack_state = ARBITRACK_STATE.ARBI_TRACKING;
		}
	}
	//endregion

	//region Models
	private void setupBloodhound() {
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


	private void setupWall(){


		ARModelImporter importer = new ARModelImporter();
		importer.loadFromAsset("wall.jet");
		this.modelNode = (ARModelNode) importer.getNode();
		ARTexture2D texture2D = new ARTexture2D();
		texture2D.loadFromAsset("wall.png");
		ARTextureMaterial material = new ARTextureMaterial();
		material.setTexture(texture2D);
		for (ARMeshNode meshNode : importer.getMeshNodes()) {
			meshNode.setMaterial(material);
		}

		/* alternative to give every meshNode a different texture
		for (int i = 0; i < importer.getMeshNodes().size(); i++){
			ARTexture2D texture2D = new ARTexture2D();
			texture2D.loadFromAsset("army"+i+".png");
			ARTextureMaterial material = new ARTextureMaterial();
			material.setTexture(texture2D);
			importer.getMeshNodes().get(i).setMaterial(material);
		}
		*/
		this.modelNode.scaleByUniform(6.0f);
		this.modelNode.setVisible(true);

	}


	private void setupNeugereut() {
		ARModelImporter importer = new ARModelImporter();
		importer.loadFromAsset("neugereut.jet");
		this.modelNode = (ARModelNode) importer.getNode();

        /*
		ARTexture2D texture2D = new ARTexture2D();
		texture2D.loadFromAsset("beton1.png");
		ARTextureMaterial material = new ARTextureMaterial();
		material.setTexture(texture2D);

		for (ARMeshNode meshNode : importer.getMeshNodes()) {
			meshNode.setMaterial(material);
		}
        */

        /* alternative to give every meshNode a different texture */
        int u = 0;
		for (int i = 0; i < importer.getMeshNodes().size(); i++){
            if(u > 3 ){
                u = 0;
            }
			ARTexture2D texture2D = new ARTexture2D();
			texture2D.loadFromAsset("beton"+u+".png");
			ARTextureMaterial material = new ARTextureMaterial();
			material.setTexture(texture2D);
            importer.getMeshNodes().get(i).setMaterial(material);
            u++;
		}


        this.modelNode.rotateByDegrees(-90, 1.0f, 0.0f, 0.0f);
		this.modelNode.scaleByUniform(3.0f);
		this.modelNode.setVisible(true);

	}

    private void setupFence(){
        ARModelImporter importer = new ARModelImporter();
        importer.loadFromAsset("fence.jet");
        this.modelNode = (ARModelNode) importer.getNode();

        String size = Integer.toString(importer.getMeshNodes().size());
        Log.i("KudanSamples", "Meshnodes =" + size);
		for (int i = 0; i < importer.getMeshNodes().size(); i++){
			ARTexture2D texture2D = new ARTexture2D();
            texture2D.loadFromAsset("fence" + i + ".png");
			ARTextureMaterial material = new ARTextureMaterial();
			material.setTexture(texture2D);
            material.setTransparent(true);
            importer.getMeshNodes().get(i).setMaterial(material);
		}
        //importer.getMeshNodes().get(0).getMesh()
        this.modelNode.scaleByUniform(6.0f);
        this.modelNode.setVisible(true);
    }

	//endregion


}
