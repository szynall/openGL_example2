package test;
 
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import Geometry.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

class Renderer implements GLEventListener, KeyListener, MouseListener, 
MouseMotionListener
{
	private OpenGLEx openGL;
	//private SerialTest serial = new SerialTest();
    private GLU glu;
    private boolean shift=false,forward=false, backward=false, left=false, right = false, up = false, down = false,
    				button1 = false,button2 =false,button3=false;
    private float lastPosX = 0, lastPosY, lastTime=0;
    public List<Mesh> meshes = new ArrayList<Mesh>();
    public Camera camera;
    private float[] LightPosition =  {6683328, 16000, 5464137, 1};
    private WGSToZone2000 wgs = new WGSToZone2000();
    private Zone2000ToWGS zone2000 = new Zone2000ToWGS();
    File[] files;
    boolean initReady = false;
    String name = null;
    
    
    public Renderer(OpenGLEx o)
    {
    	openGL = o;
    }
    
    //call every frame
    public void display(GLAutoDrawable drawable) {
    	 GL2 gl = drawable.getGL().getGL2();     
    	 if(initReady)
    	 {
    		 initVBO(gl, name);
    	 }
    	
    	//float time=drawable.getAnimator().getLastFPS();
    	//System.out.println(time);
        Input();
        
       // openGL.posLabel.setText("Pozycja: "+df.format() +"N");
        UpdatePositionLabel();
		render(drawable);
    }
    
    private void UpdatePositionLabel()
    {
    	DecimalFormat df = new DecimalFormat("0");
    	double lat = zone2000.ConvertLatitudeToWGS84(camera.eyePos.z(), camera.eyePos.x());
    	double lon = zone2000.ConvertLongitudeToWGS84(camera.eyePos.z(), camera.eyePos.x());
    	int deglat,hourlat,deglon,hourlon;
    	deglat = (int)lat;
    	hourlat = 59-(int)((lat - deglat)*60);
    	deglon = (int)lon;
    	hourlon = (int)((lon - deglon)*60);
    	openGL.posLabel.setText("Pozycja: "+deglat+"�"+hourlat+"'"+(deglat>0?"N ":"S ")+deglon+"�"+hourlon+"'"+(deglon>0?"E":"W"));
    }
    
    public void initVBO(GL2 gl, String name)
    {
    	try {
			meshes.add(new Mesh(gl,name));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	initReady = false;
    }
    
    //render scene
	private void render(GLAutoDrawable gLDrawable)
	{
        GL2 gl = gLDrawable.getGL().getGL2();     
        //usu� poprzedni� klatk�   
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);   
        gl.glEnable(GL2.GL_DEPTH_TEST);  //Z-Buffer Algorith
        
        //przejd� na macierz modelowania
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glPushMatrix();

		// initialze ModelView matrix
		gl.glLoadIdentity();
		
		//obr�� i przesu� kamere
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
		
		//przesu� �wiat�o razem z kamer�
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, LightPosition,0); 
		
		ListIterator<Mesh> it = meshes.listIterator();
		Mesh mesh;
		while (it.hasNext())
		{
			mesh = it.next();
			if(mesh.ready = true)
				mesh.render(gl);
		}
		
        gl.glPopMatrix();
        gl.glFlush();
    }
    
	//get input
    private void Input()
    {	
    	float currentTime = (float)(System.nanoTime() / 100000000.0);
    	float elapsedTime = currentTime - lastTime;
    	lastTime = currentTime;
    		
    	Vector3 forwardDir = camera.getForwardDir().mul(elapsedTime*(button1?5:1));
    	Vector3 sideDir = camera.getLeftDirection(forwardDir).mul(elapsedTime*(button1?5:1));

	     if(forward)
	   		 camera.moveCamera(forwardDir);
	   	 if(backward)
	   		 camera.moveCamera(forwardDir.mul(-1));
	   	 if(left)
	   		 camera.moveCamera(sideDir);
	   	 if(right)
	   		 camera.moveCamera(sideDir.mul(-1));
	   	 if(up)
	   		 camera.moveCamera(new Vector3(0,50*elapsedTime,0));		
	   	 if(down)
	   		 camera.moveCamera(new Vector3(0,-50*elapsedTime,0));	
	   	 
	   	//obs�uga gyro
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
    	GL2 gl = gLDrawable.getGL().getGL2();
    	
    	Thread t = new Thread(new Loader(this));
		t.start();
		
    	System.out.println("init() called");
        glu = GLU.createGLU(gl);
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glCullFace(GL2.GL_FRONT);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST); // perspective correction
        gl.glShadeModel(GL2.GL_SMOOTH); // blends colors nicely, and smoothes out lighting
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); 
    	gl.glEnable( GL2.GL_BLEND ); 
    	
    	//ustawienia materia�u
    	gl.glEnable(GL2.GL_COLOR_MATERIAL);
    	float ambient[] = {0.4725f,	0.595f,	0.745f}; 
    	float diffuse[] = {0.05164f,	0.0648f,	0.07648f};
    	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient,0);
    	gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse,0);
    	
    	//init mg�y i �wiate�
        initFog(gl);
        initLighting(gl);
        
        //ustaw kamere
        camera = new Camera(6646228,400,5431137,6646328,400,5431137);

        //utw�rz listenery do klawiatury i myszki
        ((Component) gLDrawable).addMouseListener(this);
        ((Component) gLDrawable).addMouseMotionListener(this);
        ((Component) gLDrawable).addKeyListener(this);
        
        //serial init
    	//serial.initialize();
    }
    
    //create fog
    private void initFog( GL2 gl )
    {
    	gl.glEnable(GL2.GL_FOG);
		gl.glFogf(GL2.GL_FOG_START,60000.0f); 	 //pocz�tek
		gl.glFogf(GL2.GL_FOG_END,100000.0f);   	 //koniec
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
	
		if (keyCode == KeyEvent.VK_ESCAPE)
			System.exit(0);	
		if (keyCode == KeyEvent.VK_UP)
			forward = true;
		if (keyCode == KeyEvent.VK_DOWN)
			backward = true;
		if (keyCode == KeyEvent.VK_LEFT) 
			left = true;
		if (keyCode == KeyEvent.VK_RIGHT)
			right = true;	
		if (keyCode == KeyEvent.VK_PAGE_UP)
			up = true;	
		if (keyCode == KeyEvent.VK_PAGE_DOWN)
			down = true;	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_ESCAPE)
			System.exit(0);
		if (keyCode == KeyEvent.VK_UP)
			forward = false;
		if (keyCode == KeyEvent.VK_DOWN)
			backward = false;
		if (keyCode == KeyEvent.VK_LEFT)
			left = false;
		if (keyCode == KeyEvent.VK_RIGHT)
			right = false;
		if (keyCode == KeyEvent.VK_PAGE_UP)
			up = false;
		if (keyCode == KeyEvent.VK_PAGE_DOWN)
			down = false;
	}
	
	public void keyTyped(KeyEvent e) {}
	
	//set camera from cordinates
    public void cameraSet(String cordinates)
    {	
    	Pattern pattern = Pattern.compile("(\\-?[0-9]+[\\.]{0,1}[0-9]*),(\\-?[0-9]+[\\.]{0,1}[0-9]*)");
    	Matcher matcher = pattern.matcher(cordinates);
    
    	if (matcher.find())
    	{
    	double lat = Double.parseDouble(matcher.group(1));

    	double lon = Double.parseDouble(matcher.group(2));
    	camera.eyePos = new Vector3(wgs.ConvertLongitude(lat,lon),
    								1000,
    								wgs.ConvertLatitude(lat,lon));
    	}
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