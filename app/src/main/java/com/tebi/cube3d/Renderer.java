package com.tebi.cube3d;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.BaseObject3D;
import rajawali.animation.Animation3D;
import rajawali.animation.Animation3DListener;
import rajawali.animation.RotateAnimation3D;
import rajawali.lights.ALight;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.math.Number3D;
import rajawali.math.Quaternion;
import rajawali.parser.ObjParser;
import rajawali.renderer.RajawaliRenderer;
import rajawali.wallpaper.Wallpaper;

public class Renderer extends RajawaliRenderer implements OnSharedPreferenceChangeListener{
	private Animation3D mAnim;
	private BaseObject3D mCube;
	private BaseObject3D mScene;
	private float mStartX = 0, mStartY = 0;
	private final float TORLERANCE = 80.0f;

	Number3D mXAxis = new Number3D(1, 0, 0);
	Number3D mYAxis = new Number3D(0, 1, 0);

	private int mCheckedPos;

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	private Quaternion mOrientation = null;
	private boolean mAutoRotate;
	private boolean mChangeToAutoRotate;
	private boolean mFixCube;
	private GestureDetector mGestureDetector;
	private CubeRotateAnimation flingRotation;
	private int mScreenX;
	private int mCurrentClub;

	public Renderer(Context context) {
		super(context);
		setFrameRate(50);
		preferences = mContext.getSharedPreferences(Wallpaper.SHARED_PREFS_NAME,
				Context.MODE_PRIVATE);
		preferences.registerOnSharedPreferenceChangeListener(this);
		mGestureDetector = new GestureDetector(context, new MyGestureListener());

		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(size);
		} else {
			size.x = display.getWidth();
			size.y = display.getHeight();
		}
		mScreenX = Math.min(size.x, size.y);
		mCurrentClub = ((Cube3dApplication)context.getApplicationContext()).getClub();
	}

	public void initScene () {
		ALight light = new DirectionalLight(1.1f, -0.5f, -0.8f);
		light.setPower(0.85f);

		ALight light2 = new DirectionalLight(-1.1f, -0.5f, -0.8f);
		light2.setPower(0.85f);

		ALight lightScene = new DirectionalLight();
		lightScene.setPower(6.5f);

		mCamera.setPosition(0, 0, 7);
		mCamera.setLookAt(0, 0, 0);
		mCamera.setFarPlane(1000);



		ObjParser sceneParser;
		mCheckedPos = this.preferences.getInt("checked", 0);

		sceneParser = new ObjParser(mContext.getResources(),
				mTextureManager, R.raw.scenefc_obj);
		sceneParser.parse();
		mScene = sceneParser.getParsedObject();
		if (mCheckedPos == 0) {
			mScene.addTexture(mTextureManager.addTexture(getBitmapByName("scene")));
		} else {
			mScene.addTexture(mTextureManager.addTexture(getBitmapByName("scene2")));
		}


		ObjParser objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.cube_obj);
		objParser.parse();
		mCube = objParser.getParsedObject();
		mCube.addTexture(mTextureManager.addTexture(getBitmapByName("cube_texture")));

		mCube.addLight(light);
		mCube.addLight(light2);
		mScene.addLight(lightScene);

		addChild(mCube);
		addChild(mScene);

		mCube.setScale(preferences.getInt("scale", 80) / 80.0f);
		mCube.setMaterial(new DiffuseMaterial());

		int speed = preferences.getInt("speed", 50);
		speed = Math.max (speed, 20);
		long duration = 10000 / speed * 50;

		// 从不自动转更改到自动转时， 不进行复位
		if ((!mChangeToAutoRotate) && (mOrientation != null))  {
			mCube.setOrientation(mOrientation);
		}

		if (!mEngine.isPreview()) {
			mChangeToAutoRotate = false;
		}

		mAutoRotate = preferences.getBoolean("auto_rotate", true);
		mFixCube = preferences.getBoolean("fix_cube", false);
		if (mAutoRotate) {
			Number3D axis = new Number3D(1, 8, 4);
			axis.normalize();
			mAnim = new RotateAnimation3D(axis, 360);
			mAnim.setDuration(duration);
			mAnim.setRepeatCount(Animation3D.INFINITE);
			mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
			mAnim.setTransformable3D(mCube);
			mAnim.start();
		} else {
			if (mAnim != null) {
				mAnim.cancel();
			}
		}

	}

	private int getDrawableIdByName(String name) {
		return  getContext().getResources().getIdentifier(name, "drawable", getContext().getPackageName());
	}

	private Bitmap getBitmapByName (String name) {
		return BitmapFactory.decodeResource(getContext().getResources(), getDrawableIdByName(name));
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
	}

	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		if (!mFixCube) {
			if ((!mAutoRotate) && (touchTurn != 0)) {
				mCube.rotateAround(mYAxis, touchTurn * 15);
				touchTurn = 0;
			}

			if ((!mAutoRotate) && (touchTurnUp != 0)) {
				mCube.rotateAround(mXAxis, touchTurnUp * 15);
				touchTurnUp = 0;
			}
		}
		mOrientation = mCube.getOrientation();
	}


	@Override
	public void onTouchEvent(MotionEvent me) {
		if (!mFixCube) {
			if (!mAutoRotate) {
				mGestureDetector.onTouchEvent(me);
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					xpos = me.getX();
					ypos = me.getY();
					return;
				}

				if (me.getAction() == MotionEvent.ACTION_UP) {
					xpos = -1;
					ypos = -1;
					touchTurn = 0;
					touchTurnUp = 0;
					return;
				}

				if (me.getAction() == MotionEvent.ACTION_MOVE) {
					float xd = me.getX() - xpos;
					float yd = me.getY() - ypos;

					xpos = me.getX();
					ypos = me.getY();

					touchTurn = xd / -100f;
					touchTurnUp = yd / -100f;
					return;
				}

				try {
					Thread.sleep(15);
				} catch (Exception e) {
					// No need for this...
				}
			} else {

				switch (me.getAction() & MotionEvent.ACTION_MASK) {

					case MotionEvent.ACTION_DOWN:
						mStartX = me.getX();
						mStartY = me.getY();
						break;
					case MotionEvent.ACTION_UP:
						if ((me.getX() - mStartX) > TORLERANCE) {
							if (mAnim != null) mAnim.cancel();
							Number3D axis = new Number3D(-1, -8, -4);
							axis.normalize();
							mAnim = new RotateAnimation3D(axis, 360);
							mAnim.setDuration(10000);
							mAnim.setRepeatCount(Animation3D.INFINITE);
							mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
							mAnim.setTransformable3D(mCube);
							mAnim.start();

						} else if ((mStartX - me.getX()) > TORLERANCE) {
							if (mAnim != null) mAnim.cancel();
							Number3D axis = new Number3D(1, 8, 4);
							axis.normalize();
							mAnim = new RotateAnimation3D(axis, 360);
							mAnim.setDuration(10000);
							mAnim.setRepeatCount(Animation3D.INFINITE);
							mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
							mAnim.setTransformable3D(mCube);
							mAnim.start();
						}

						break;

				}
			}
		}

	}

	public void startRendering() {
		super.startRendering();
		destroyScene();
		initScene();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
										  String key) {

		if (key.equals("auto_rotate") && preferences.getBoolean("auto_rotate", true)) {
			mChangeToAutoRotate = true;
		}

	}

	private class MyGestureListener implements GestureDetector.OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							   float velocityY) {

			float xd = e2.getX() - e1.getX();

			if (Math.abs(xd) > 0.8 * mScreenX && Math.abs(velocityX) > 1500) {

				long duration =  Math.round(25000 * Math.abs(xd / velocityX));

				if (flingRotation != null) flingRotation.cancel();
				flingRotation = new CubeRotateAnimation(0, -1.5f * xd, 0);
				flingRotation.setDuration(duration);

				flingRotation.setInterpolator(new LinearInterpolator());

				flingRotation.setRepeatCount(0);
				flingRotation.setTransformable3D(mCube);
				flingRotation.setAnimationListener(new Animation3DListener() {
					public void onAnimationEnd(Animation3D animation) {
						if (null != flingRotation) {
							flingRotation.cancel();
							flingRotation.setPaused(true);
							flingRotation.setHasEnded(true);
							flingRotation = null;
						}
					}
					public void onAnimationRepeat(Animation3D animation) { }
					public void onAnimationStart(Animation3D animation) { }
				});

				flingRotation.start();
			}

			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
								float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

	}
}
