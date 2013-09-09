package test;

import Geometry.*;

public class Camera {

	public Vector3 eyePos;
	public Vector3 targetPos;
	public Matrix4x4f matrixView = new Matrix4x4f();;
	public float cameraAngleX, cameraAngleY;
	public float cameraAngle[] = new float[3];

	public Camera(float x, float y, float z, float tx, float ty, float tz)
	{
		eyePos = new Vector3(x,y,z);
		targetPos = new Vector3(tx,ty,tz);
	}
	
	public void moveCamera(Vector3 moveVector)
	{
		Vector3 _eye = eyePos;	
		_eye.add(moveVector);
		setCamera( _eye, new Vector3(0,0,0));
	}
	
	void getForward()
	{
	
	}
	
	void setCamera(Vector3 _eyePos, Vector3 _targetPos)
	{
	    float forward[] = new float[4];
	    float up[] = new float[4];
	    float left[] = new float[4];
	    float position[] = new float[4];
	    float invLength;

	    // determine forward vector (direction reversed because it is camera)
	    forward[0] = _eyePos.x() - _targetPos.x();    // x
	    forward[1] = _eyePos.y() - _targetPos.y();    // y
	    forward[2] = _eyePos.z() - _targetPos.z();    // z
	    forward[3] = 0.0f;              // w
	    // normalize it without w-component
	    invLength = (float) (1.0f / Math.sqrt(forward[0]*forward[0] + forward[1]*forward[1] + forward[2]*forward[2]));
	    forward[0] *= invLength;
	    forward[1] *= invLength;
	    forward[2] *= invLength;

	    // assume up direction is straight up
	    up[0] = 0.0f;   // x
	    up[1] = 1.0f;   // y
	    up[2] = 0.0f;   // z
	    up[3] = 0.0f;   // w

	    // compute left vector with cross product
	    left[0] = up[1]*forward[2] - up[2]*forward[1];  // x
	    left[1] = up[2]*forward[0] - up[0]*forward[2];  // y
	    left[2] = up[0]*forward[1] - up[1]*forward[0];  // z
	    left[3] = 1.0f;                                 // w

	    // re-compute orthogonal up vector
	    up[0] = forward[1]*left[2] - forward[2]*left[1];    // x
	    up[1] = forward[2]*left[0] - forward[0]*left[2];    // y
	    up[2] = forward[0]*left[1] - forward[1]*left[0];    // z
	    up[3] = 0.0f;                                       // w

	    // camera position
	    position[0] = -eyePos.x();
	    position[1] = -eyePos.y();
	    position[2] = -eyePos.z();
	    position[3] = 1.0f;

	    // copy axis vectors to matrix
	    matrixView.identity();
	    matrixView.setColumn(0, left);
	    matrixView.setColumn(1, up);
	    matrixView.setColumn(2, forward);
	    matrixView.setColumn(3, position);
	}
	
	

}
