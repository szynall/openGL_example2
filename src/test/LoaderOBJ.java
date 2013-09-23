package test;


import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import Geometry.Vector3;


public class LoaderOBJ 
{
    public List<Float> faceVertices = new ArrayList<Float>(); //lista wierzcholkow triangli
    public List<Float>   n = new ArrayList<Float>();//Lista normalnych
    
    public int w=42,h=42;      //ustawienia w skali 1:3
   // public int w=122,h=122;	 //ustawienia w skali 1:1
    public Vector3 position;

    
	public LoaderOBJ( String name)
	{
		Vector3[][] normals = new Vector3[w][h];
		Vector3 p1,p2,p3,v1,v2,n1;
		int vertexCount;
		
    	try {
            File file = new File(name);
            
            @SuppressWarnings("resource")
			FileInputStream fin = new FileInputStream(file);
            FileChannel fc = fin.getChannel();
            ByteBuffer buf = ByteBuffer.allocateDirect((int)fc.size());
            fc.read(buf);
            
            //ustaw kolejnoœæ bajtów
            buf.order(ByteOrder.BIG_ENDIAN);
            buf.rewind();
            
			vertexCount = buf.getInt();
			long currentTime = System.currentTimeMillis();
			
			//wype³nij tablice normalnych
			for (int i=0;i<w;i+=1)
      	  	{
			  for(int j=0;j<h;j+=1)
			  {
				  normals[i][j] = new Vector3(0,0,0);
			  }
      	  	}
			
			for (int i=0;i<w-1;i+=1)
      	  	{
				for(int j=0;j<h-1;j+=1)
				{
					//1st quad triangle
					p1 = new Vector3(buf.getFloat(i*w*12+(j*12)+4),buf.getFloat(i*w*12+(j*12)+12),buf.getFloat(i*w*12+(j*12)+8));
					faceVertices.add(p1.x());
					faceVertices.add(p1.y());
					faceVertices.add(p1.z());
					p2 = new Vector3(buf.getFloat((i+1)*w*12+(j*12)+4),buf.getFloat((i+1)*w*12+(j*12)+12),buf.getFloat((i+1)*w*12+(j*12)+8));
					faceVertices.add(p2.x());
					faceVertices.add(p2.y());
					faceVertices.add(p2.z());
					p3 = new Vector3(buf.getFloat(i*w*12+((j+1)*12)+4),buf.getFloat(i*w*12+((j+1)*12)+12),buf.getFloat(i*w*12+((j+1)*12)+8));
					faceVertices.add(p3.x());
					faceVertices.add(p3.y());
					faceVertices.add(p3.z());
		  
					//wylicz wektory
					v1 = p2.sub(p1);
					v2 = p3.sub(p1);
					// wylicz normalna - iloczyn wektorowy
					n1 = v1.cross(v2);
					//dodaj normalne
					normals[i][j].add(n1);
					normals[i+1][j].add(n1);
					normals[i][j+1].add(n1);
					
					//2nd quad triangle
					p1 = new Vector3(buf.getFloat(i*w*12+((j+1)*12)+4),buf.getFloat(i*w*12+((j+1)*12)+12),buf.getFloat(i*w*12+((j+1)*12)+8));
					faceVertices.add(p1.x());
					faceVertices.add(p1.y());
					faceVertices.add(p1.z());
					p2 = new Vector3(buf.getFloat((i+1)*w*12+(j*12)+4),buf.getFloat((i+1)*w*12+(j*12)+12),buf.getFloat((i+1)*w*12+(j*12)+8));
					faceVertices.add(p2.x());
					faceVertices.add(p2.y());
					faceVertices.add(p2.z());
					p3 = new Vector3(buf.getFloat((i+1)*w*12+((j+1)*12)+4),buf.getFloat((i+1)*w*12+((j+1)*12)+12),buf.getFloat((i+1)*w*12+((j+1)*12)+8));
					faceVertices.add(p3.x());
					faceVertices.add(p3.y());
					faceVertices.add(p3.z());

				  	v2 = p2.sub(p1);
					v1 = p3.sub(p1);
					n1 = v2.cross(v1);	
					
					normals[i][j+1].add(n1);
					normals[i+1][j].add(n1);
					normals[i+1][j+1].add(n1);
				}
      	  	}
	    	
			//srednia pozycja
			position = new Vector3(0,0,0);
			for (int i=0;i<w-1;i++)
			{
				position.setX(position.x()+buf.getFloat(4+i*12));
				position.setZ(position.z()+buf.getFloat(i*w*12+8));
			}
			position = new Vector3(position.x()/(w-1),0, position.z()/(w-1));
			
			
			//znormalizuj normalne
	    	for (int i=0;i<w;i+=1)
	  	  	{
				for(int j=0;j<h;j+=1)
				{
					normals[i][j] = normals[i][j].normalize();
				}
	  	  	}
	    	
	    	//dodaj do listy
	    	for (int i=0;i<w-1;i+=1)
	  	  	{
				for(int j=0;j<h-1;j+=1)
				{
					  n.add(normals[i][j].x());
					  n.add(normals[i][j].y());
					  n.add(normals[i][j].z());
					  
					  n.add(normals[i+1][j].x());
					  n.add(normals[i+1][j].y());
					  n.add(normals[i+1][j].z());
					  
					  n.add(normals[i][j+1].x());
					  n.add(normals[i][j+1].y());
					  n.add(normals[i][j+1].z());

					  n.add(normals[i][j+1].x());
					  n.add(normals[i][j+1].y());
					  n.add(normals[i][j+1].z());
					  
					  n.add(normals[i+1][j].x());
					  n.add(normals[i+1][j].y());
					  n.add(normals[i+1][j].z());
					  
					  n.add(normals[i+1][j+1].x());
					  n.add(normals[i+1][j+1].y());
					  n.add(normals[i+1][j+1].z());
				}
	  	  	}
	    	long elapsedTime = System.currentTimeMillis() - currentTime;
	    	//System.out.print(elapsedTime);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
}
