package Geometry;

public class Vector3
{
	float xyz[] = new float[3];
	
	public Vector3()
	{
		xyz[0] = 0;
		xyz[1] = 0;
		xyz[2] = 0;
	}

	public Vector3(float x, float y, float z)
	{
		xyz[0] = x;
		xyz[1] = y;
		xyz[2] = z;
	}
	
	public Vector3(float x, float y, float z, int _id)
	{
		xyz[0] = x;
		xyz[1] = y;
		xyz[2] = z;
	}

	
	public Vector3(float[] array)
	{
		if(array.length != 3)
			throw new RuntimeException("Must create vector with 3 element array");

		xyz[0] = array[0];
		xyz[1] = array[1];
		xyz[2] = array[2];
	}
	
	public float[] array()
	{
		return (float[])xyz.clone();
	}

	public void add(Vector3 rhs)
	{
			xyz[0] += rhs.xyz[0];
			xyz[1] += rhs.xyz[1];
			xyz[2] += rhs.xyz[2];
	}

	public Vector3 sub(Vector3 rhs)
	{
		return new Vector3(
			xyz[0] - rhs.xyz[0],
			xyz[1] - rhs.xyz[1],
			xyz[2] - rhs.xyz[2] );
	}
	
	public Vector3 neg()
	{
		return new Vector3(-xyz[0], -xyz[1], -xyz[2]);
	}

	public Vector3 mul(float c)
	{
		return new Vector3(c*xyz[0], c*xyz[1], c*xyz[2]);
	}

	public Vector3 div(float c)
	{
		return new Vector3(xyz[0]/c, xyz[1]/c, xyz[2]/c);
	}

	public float dot(Vector3 rhs)
	{
		return xyz[0]*rhs.xyz[0] +
			xyz[1]*rhs.xyz[1] +
			xyz[2]*rhs.xyz[2];
	}

	public Vector3 cross(Vector3 rhs)
	{
		return new Vector3(
			xyz[1]*rhs.xyz[2] - xyz[2]*rhs.xyz[1],
			xyz[0]*rhs.xyz[2] - xyz[2]*rhs.xyz[0],
			xyz[0]*rhs.xyz[1] - xyz[1]*rhs.xyz[0]
		);
	}

	public boolean equals(Object obj)
	{
		if( obj instanceof Vector3 )
		{
			Vector3 rhs = (Vector3)obj;

			return xyz[0]==rhs.xyz[0] &&
			       xyz[1]==rhs.xyz[1] &&
			       xyz[2]==rhs.xyz[2];
		}
		else
		{
			return false;
		}
		
	}

	public float norm()
	{
		return (float) Math.sqrt(this.dot(this));	
	}
	
	public Vector3 normalize()
	{
		return this.div(norm());
	}

	public float x()
	{
		return xyz[0];
	}
	
	public float y()
	{
		return xyz[1];
	}

	public float z()
	{
		return xyz[2];
	}
	
	public void setX(float x)
	{
		this.xyz[0] = x;
	}
	
	public void set(Vector3 v)
	{
		this.xyz[0] = v.x();
		this.xyz[1] = v.y();
		this.xyz[2] = v.z();
	}
	public void setY(float y)
	{
		this.xyz[1] = y;
	}
	public void setZ(float z)
	{
		this.xyz[2] = z;
	}
	public String toString()
	{
		return xyz[0] + " " + xyz[1] + " " + xyz[2]; 
	}
	
	/*package*/
}