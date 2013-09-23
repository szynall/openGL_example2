package test;

import Geometry.*;

public class Camera {

	public Vector3 eyePos;
	public Vector3 targetPos;
	public Matrix4x4f matrixView = new Matrix4x4f();;
	public float cameraAngle[] = new float[3];
	
	
	public Camera(float x, float y, float z, float tx, float ty, float tz)
	{
		eyePos = new Vector3(x,y,z);
		targetPos = new Vector3(tx,ty,tz);
	}
	
	public void moveCamera(Vector3 moveVector)
	{
		eyePos.add(moveVector);
		targetPos.add(moveVector);
	}
	
	Vector3 getForwardDir()
	{
	    return new Vector3(eyePos.z() - targetPos.z(),0, targetPos.x() - eyePos.x());
	}
	
	Vector3 getLeftDirection(Vector3 _forward)
	{
		return new Vector3(_forward.z(),0,-1*_forward.x());
	   
	}
	
	

}
