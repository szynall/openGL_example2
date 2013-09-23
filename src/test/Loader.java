package test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;

import javax.media.opengl.GL2;

import Geometry.Vector3;

public class Loader	implements Runnable {
	Renderer renderer;
	GL2 gl;
	public Loader(Renderer r)
	{
		renderer = r;
	}
	public void CheckChunks()
	{
		Vector3 camPos = renderer.camera.eyePos;
		synchronized (renderer.meshes) 
		{
			Iterator<Mesh> it = renderer.meshes.iterator(); // Must be in synchronized block  
		    while (it.hasNext())  
		    {
				Mesh mesh = (Mesh) it.next();
				if( Math.sqrt(Math.pow( mesh.position.x()-camPos.x(),2) + Math.pow( mesh.position.z()-camPos.z(),2)) < 5000)
				{
					renderer.meshes.remove(mesh);
				}
			}
		}
	}
	
	public void Load()
	{
		File[] files = finder("C:\\Users\\Kutalisk\\workspace\\praktyki\\out\\");
    	
    	//wczytaj wszystkie pliki przy pomocy LoaderOBJ i zapisz do tablicy meshy
    	for (int i=0; i<files.length;i++)
    	{
    		String name = files[i].getAbsolutePath();

    		renderer.name = name;
    		renderer.initReady = true;
    		while(renderer.initReady == true)
    		{
    			try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		System.out.println("Doda³em mesh!");
    	}
	}
	
    public void run() {
    	Load();
    	//while(true)
    	//{
    		//CheckChunks();
    	//}
    	
    }
    
    public File[] finder( String dirName){
    	File dir = new File(dirName);

    	return dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".dat"); }
    	} );
    }
}
