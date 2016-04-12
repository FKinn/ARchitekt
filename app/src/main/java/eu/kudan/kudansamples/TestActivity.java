package eu.kudan.kudansamples;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;
import com.jme3.math.Vector3f;
import eu.kudan.kudan.*;
import android.os.Vibrator;

public class TestActivity extends ARActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{


    //gestures
    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat gDetector;

    //additional variables
    Context context;

    //track state
    private ARBITRACK_STATE arbitrack_state;

    enum ARBITRACK_STATE {
        ARBI_POSITIONING,
        ARBI_SCALING,
        ARBI_PLACEMENT,
        ARBI_TRACKING,
        ARBI_LOCKED
    }

	ARImageNode targetImageNode;
	ARModelNode modelNode;
    ARArbiTrack arbiTrack;
	private float lastScale = 0;
	private float mScaleFactor = 1.f;


	public void onCreate(Bundle savedInstanceState) {
        this.context = getApplicationContext();
		// gesture
		this.gDetector = new GestureDetectorCompat(this,this);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

		// set api key for this package name.
		ARAPIKey key = ARAPIKey.getInstance();
		key.setAPIKey("GAWAE-FBVCC-XA8ST-GQVZV-93PQB-X7SBD-P6V4W-6RS9C-CQRLH-78YEU-385XP-T6MCG-2CNWB-YK8SR-8UUQ");
		super.onCreate(savedInstanceState);
		this.arbitrack_state = ARBITRACK_STATE.ARBI_PLACEMENT;

        //Log.i("KudanSamples", "Size of meshList: " + Integer.toString(importer.getMeshNodes().size()));

	}


	public void setup() {

		super.setup();
        this.modelNode = Models.setupModel("Neugereut");
		setupArbiTrack();

	}

	private void setupArbiTrack(){

		ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
		gyroPlaceManager.initialise();
		this.targetImageNode = new ARImageNode("target.png");
		gyroPlaceManager.getWorld().addChild(this.targetImageNode);
		arbiTrack = ARArbiTrack.getInstance();
		arbiTrack.initialise();
		arbiTrack.setTargetNode(this.targetImageNode);
		this.targetImageNode.scaleByUniform(0.3f);
		this.targetImageNode.setVisible(true);
		arbiTrack.getTargetNode().rotateByDegrees(90.0f, 1.0f, 0.0f, 0.0f);
		arbiTrack.getWorld().addChild(this.modelNode);
	}

	//region gesture methods
	private void tapGesture() {
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
                this.modelNode.rotateByDegrees(distanceX, 0.0f, 0.0f, 1.0f);
			}
		}
		else if (arbitrack_state == ARBITRACK_STATE.ARBI_POSITIONING) {
            Vector3f position = this.modelNode.getPosition();
            synchronized (ARRenderer.getInstance()) {
                this.modelNode.setPosition(position.getX(), position.getY() + distanceY, position.getZ());
                this.modelNode.setPosition(position.getX(), position.getY(), position.getZ() + distanceX);
            }
        }
	}
	//endregion

	//region gesture listener
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        tapGesture();

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);
        if(arbitrack_state == ARBITRACK_STATE.ARBI_TRACKING){
            arbitrack_state = ARBITRACK_STATE.ARBI_LOCKED;
        } else if (arbitrack_state == ARBITRACK_STATE.ARBI_LOCKED){
            arbitrack_state = ARBITRACK_STATE.ARBI_TRACKING;
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
    	return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		panGesture(distanceX,distanceY);
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(400);
        if(arbitrack_state == ARBITRACK_STATE.ARBI_TRACKING){
            arbitrack_state = ARBITRACK_STATE.ARBI_POSITIONING;
        } else if (arbitrack_state == ARBITRACK_STATE.ARBI_POSITIONING) {
            arbitrack_state = ARBITRACK_STATE.ARBI_TRACKING;
        }


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

			if(arbitrack_state == ARBITRACK_STATE.ARBI_SCALING){
				mScaleFactor *= detector.getScaleFactor();
				// Don't let the object get too small or too large.
				mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
				pinchGesture(mScaleFactor);
			}
			return true;

		}
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector){
			if(arbitrack_state == ARBITRACK_STATE.ARBI_TRACKING){
				lastScale = 1;
				arbitrack_state = ARBITRACK_STATE.ARBI_SCALING;
			}

			return true;
		}
		public void onScaleEnd(ScaleGestureDetector detector){
			if(arbitrack_state == ARBITRACK_STATE.ARBI_SCALING){
				arbitrack_state = ARBITRACK_STATE.ARBI_TRACKING;
			}

		}
	}
	//endregion

}
