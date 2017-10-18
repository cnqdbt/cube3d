package com.tebi.wallpaper3dmilan;

import rajawali.animation.Animation3D;

public class CubeRotateAnimation extends Animation3D {

	private float mXToRot;
	private float mYToRot;
	private float mZToRot;

	private float lastInterpolatedTime = -1;

	public CubeRotateAnimation(float rotX,
			float rotY, float rotZ) {
		super();

		mXToRot = rotX;
		mYToRot = rotY;
		mZToRot = rotZ;

	}

	@Override
	protected void applyTransformation(float interpolatedTime) {
		System.out.println("zzzzz " + interpolatedTime);

		// the UpdateTimeTask is not perfect, and may pass a very small time at
		// last
		if (lastInterpolatedTime > interpolatedTime) {
			return;
		}

		lastInterpolatedTime = interpolatedTime;


		
//		float k = 0.2f * interpolatedTime * (1.0f - Math.min(3.5f * interpolatedTime, 0.995f));
		float k =  0.2f * interpolatedTime * (1.0f - Math.min(4.0f * interpolatedTime, 0.997f));
		float deltaX = k * mXToRot;
		float deltaY = k * mYToRot;
		float deltaZ = k * mZToRot;


		mTransformable3D.setRotation(mTransformable3D.getRotX() + deltaX,
				mTransformable3D.getRotY() + deltaY, mTransformable3D.getRotZ() + deltaZ);

	}

}