package test;
 
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.FloatBuffer;
import java.util.ListIterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import Geometry.*;
import java.io.File;
import java.io.FilenameFilter;

class Renderer implements GLEventListener, KeyListener, MouseListener, 
MouseMotionListener
{
	private SerialTest serial = new SerialTest();
    private GLU glu;
    private boolean forward=false, backward=false, left=false, right = false, up = false, down = false,
    				button1 = false,button2 =false,button3=false;
    private float lastPosX = 0, lastPosY, lastTime=0;
    private Mesh[] meshes;
    private FloatBuffer vertices, normals; 
    private Camera camera;
    private float[] LightPosition =  {6683328, 16000, 5464137, 1};
    private WGSToZone2000 wgs = new WGSToZone2000();
    private int VBO[] = null; 
    
    //call every frame
    public void display(GLAutoDrawable drawable) {
    	//float time=drawable.getAnimator().getLastFPS();
    	//System.out.println(time);
        Input();
		render(drawable);
    }
    
    //render scene
	private void render(GLAutoDrawable gLDrawable)
    {
		
        GL2 gl = gLDrawable.getGL().getGL2();     
        //usuñ poprzedni¹ klatkê   
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);   
        gl.glEnable(GL2.GL_DEPTH_TEST);  //Z-Buffer Algorith
        
        //przejdŸ na macierz modelowania
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glPushMatrix();

		// initialze ModelView matrix
		gl.glLoadIdentity();
		
		//obróæ i przesuñ kamere
		gl.glRotatef(camera.cameraAngle[0], 1.0f, 0.0f, 0.0f);
		gl.glRotatef(camera.cameraAngle[1], 0.0f, 1.0f, 0.0f);
		gl.glRotatef(camera.cameraAngle[2], 0.0f, 0.0f, 1.0f);
		gl.glTranslatef(-camera.eyePos.x(), -camera.eyePos.y(), -camera.eyePos.z());
	
	
	    // transform the object (model matrix)
	    // The result of GL_MODELVIEW matrix will be:
	    // ModelView_M = View_M * Model_M
	    //gl.glTranslatef(1, 0, 0);
	    //gl.glRotatef(1, 1, 0, 0);
	    //gl.glRotatef(1, 0, 1, 0);
	    //gl.glRotatef(1, 0, 0, 1);
		
		//przesuñ œwiat³o razem z kamer¹
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, LightPosition,0); 
		
		// Enable the client states
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);

        // Bind the VBOs (loop over the objects)
        for (int i=0; i<meshes.length; i++) {

            // Use the normals
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[2*i+1]);
            gl.glNormalPointer(GL.GL_FLOAT, 0, 0);
            
            // Use the vertices
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[2*i]);
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

            // Render the triangles
                gl.glDrawArrays(GL.GL_TRIANGLES, 0, meshes[i].faceVertices.size()/3);
        }

        // Disable the client states
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		
        gl.glPopMatrix();
        gl.glFlush();
    }
    
	//get input
    private void Input()
    {
    	
    	float currentTime = (float)(System.nanoTime() / 100000000.0);
    	float elapsedTime = currentTime - lastTime;
    	lastTime = currentTime;
    	
	     if(forward)
	   		 camera.moveCamera(new Vector3(0,0,400*elapsedTime));
	   	 if(backward)
	   		 camera.moveCamera(new Vector3(0,0,-400*elapsedTime));
	   	 if(left)
	   		 camera.moveCamera(new Vector3(400*elapsedTime,0,0));
	   	 if(right)
	   		 camera.moveCamera(new Vector3(-400*elapsedTime,0,0));
	   	 if(up)
	   		 camera.moveCamera(new Vector3(0,50*elapsedTime,0));		
	   	 if(down)
	   		 camera.moveCamera(new Vector3(0,-50*elapsedTime,0));	
	   	 
	   	//obs³uga gyro
	   	/* System.out.println(serial.inputLine);
	   	try {
			String[] s = serial.inputLine.split(" "); 
			camera.cameraAngle[0] = Math.max( - 50, Math.min(50, Float.parseFloat(s[1]) ) );
			camera.cameraAngle[1] = Float.parseFloat(s[5]);
			//camera.cameraAngle[2] = Float.parseFloat(s[5]);
			//System.out.println(camera.cameraAngle[0]);
		} catch (Exception e) {
		}*/
	}
    
    //call after window resize
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) 
    {
        final GL2 gl = gLDrawable.getGL().getGL2();
        if (height <= 0) // avoid a divide by zero error!
            height = 1;
 
        final float h = (float) width / (float) height; //aspect
        
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0f, h, 40.0f, 40000.0f);
        glu.gluLookAt(	0,	0,	0,
				0,	0,	10000, 0,1,0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
     
    //initialize GL
    public void init(GLAutoDrawable gLDrawable) 
    {
    	System.out.println("init() called");
        LoaderOBJ loader;
    	//znajdz wszystkie pliki w katalogu
    	File[] files = finder("C:\\Users\\Kutalisk\\workspace\\praktyki\\out\\");
    	meshes = new Mesh[files.length];
    	
    	//wczytaj wszystkie pliki przy pomocy LoaderOBJ i zapisz do tablicy meshy
    	for (int i=0; i<files.length;i++)
    	{
    		String name = files[i].getName();
    		//System.out.println(i+"   "+name);
    		loader = new LoaderOBJ(name);
        	meshes[i] = new Mesh(loader.faceVertices,loader.n,loader.h,loader.w);
    	}
    	
        GL2 gl = gLDrawable.getGL().getGL2();
        glu = GLU.createGLU(gl);
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST); // perspective correction
        gl.glShadeModel(GL2.GL_SMOOTH); // blends colors nicely, and smoothes out lighting
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); 
    	gl.glEnable( GL2.GL_BLEND ); 
    	
    	//ustawienia materia³u
    	gl.glEnable(GL2.GL_COLOR_MATERIAL);
    	float ambient[] = {0.4725f,	0.595f,	0.745f}; 
    	float diffuse[] = {0.05164f,	0.0648f,	0.07648f};
    	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient,0);
    	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse,0);
    	
    	//init mg³y i œwiate³
        initFog(gl);
        initLighting(gl);
        
        //ustaw kamere
        camera = new Camera(6646328,400,5431137,0,0,0);
        
        //deklaracja wielkoœci buffora
        //2 - vertex+normals
        VBO = new int[meshes.length*2];
        gl.glGenBuffers(meshes.length*2, VBO, 0);
        
        // Loop through all of the objects we have
        for (int i=0; i<meshes.length; i++) {
            // Get the current object from which we'll retrieve the vertices and normals
            Mesh tempObj = meshes[i];

            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[2*i]);

            // Allocate the float buffer for the vertices.
            int totalBufferSize = tempObj.faceVertices.size();
            vertices = FloatBuffer.allocate(totalBufferSize);

            ListIterator<Float> it = tempObj.faceVertices.listIterator();
            while(it.hasNext())
            	vertices.put(it.next());
            
            vertices.rewind();

            // Write out vertex buffer to the currently bound VBO. *4 because float
            gl.glBufferData(GL.GL_ARRAY_BUFFER, totalBufferSize *4, vertices, GL.GL_STATIC_DRAW);

            // Free the vertices buffer since we are done with them
            vertices = null;

            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[2*i+1]);

            totalBufferSize = tempObj.normals.size();
            normals = FloatBuffer.allocate(totalBufferSize);
            it = tempObj.normals.listIterator();
            while(it.hasNext())
            	normals.put(it.next());

            normals.rewind();

            // Write out the normals buffer to the currently bound VBO.
            gl.glBufferData(GL.GL_ARRAY_BUFFER, totalBufferSize *4, normals, GL.GL_STATIC_DRAW);

            // Free the normals
            normals = null;
        }

        //utwórz listenery do klawiatury i myszki
        ((Component) gLDrawable).addMouseListener(this);
        ((Component) gLDrawable).addMouseMotionListener(this);
        ((Component) gLDrawable).addKeyListener(this);
        
        //serial init
    	serial.initialize();
    }
    
    //create fog
    private void initFog( GL2 gl )
    {
    	gl.glEnable(GL2.GL_FOG);
		gl.glFogf(GL2.GL_FOG_START,20000.0f); 	 //pocz¹tek
		gl.glFogf(GL2.GL_FOG_END,50000.0f);   	 //koniec
		gl.glFogi(GL2.GL_FOG_MODE,GL.GL_LINEAR);
		float fogColor[]={0.0f, 0.78f, 0.94f,1};
		gl.glFogfv(GL2.GL_FOG_COLOR,fogColor,0);
		gl.glClearColor(fogColor[0],fogColor[1],fogColor[2],fogColor[3]);
    }
    
    //create lights static
    private void initLighting( GL2 gl )
    {
    	float[] LightDiffuse= { 0.50f, 0.73f, 0.42f, 1.0f };
    	float[] lightSpecular = {0.99f, 0.99f, 0.99f, 1f};
 
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, LightDiffuse,0);
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION,LightPosition,0);
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightSpecular, 0);
    	gl.glEnable(GL2.GL_LIGHT1);  
    	gl.glEnable(GL2.GL_LIGHTING); 
    }

    
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_1)
			button1 = button1?false:true;
		if (keyCode == KeyEvent.VK_2)
			button2 = button2?false:true;
		if (keyCode == KeyEvent.VK_3)
			button3 = button3?false:true;
		
		if (keyCode == KeyEvent.VK_ESCAPE)
			System.exit(0);	
		if (keyCode == KeyEvent.VK_W)
			forward = true;
		if (keyCode == KeyEvent.VK_S)
			backward = true;
		if (keyCode == KeyEvent.VK_A) 
			left = true;
		if (keyCode == KeyEvent.VK_D)
			right = true;	
		if (keyCode == KeyEvent.VK_Q)
			up = true;	
		if (keyCode == KeyEvent.VK_E)
			down = true;	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_ESCAPE)
			System.exit(0);
		if (keyCode == KeyEvent.VK_W)
			forward = false;
		if (keyCode == KeyEvent.VK_S)
			backward = false;
		if (keyCode == KeyEvent.VK_A)
			left = false;
		if (keyCode == KeyEvent.VK_D)
			right = false;
		if (keyCode == KeyEvent.VK_Q)
			up = false;
		if (keyCode == KeyEvent.VK_E)
			down = false;
	}
	
	public void keyTyped(KeyEvent e) {}
	
	//set camera from cordinates
    public void cameraSet(double lonh, double lath, int lonmin, int latmin, int lonsec, int latsec)
    {
    	double lat = lath+(double)latmin/60 + (double)latsec/3600;

    	double lon = lonh+(double)lonmin/60 + (double)lonsec/3600;
    	camera.eyePos = new Vector3(wgs.ConvertLongitude(lat,lon),
    								500,
    								wgs.ConvertLatitude(lat,lon));
    }

    public File[] finder( String dirName){
    	File dir = new File(dirName);

    	return dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".dat"); }
    	} );
    }
	
	public void mousePressed(MouseEvent e) {
		lastPosX = e.getX();
		lastPosY = e.getY();
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Dimension size = e.getComponent().getSize();
		 
		float thetaY = 360.0f * ( (float)(x-lastPosX)/(float)size.width);
		float thetaX = 360.0f * ( (float)(lastPosY-y)/(float)size.height);
		 
		lastPosX = x;
		lastPosY = y;
		 
		camera.cameraAngle[0] += thetaX;
		camera.cameraAngle[1] += thetaY;
		camera.cameraAngle[0] = Math.max( - 85, Math.min( 85, camera.cameraAngle[0] ) );
		float phi = (float) Math.toRadians( 90 - camera.cameraAngle[0] );
		float theta = (float) Math.toRadians( camera.cameraAngle[1] );

		camera.targetPos.set(new Vector3(
				(float)(camera.eyePos.x() + 100 * Math.sin( phi ) * Math.cos( theta )),
				(float)(camera.eyePos.y() + 100 * Math.cos( phi )),
				(float)(camera.eyePos.z() + 100 * Math.sin( phi ) * Math.sin( theta ))
				));
	}
	
	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {}
	public void dispose(GLAutoDrawable arg0) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent arg0){}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}

	    
}