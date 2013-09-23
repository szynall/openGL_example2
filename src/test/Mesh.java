package test;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ListIterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import Geometry.Vector3;

public class Mesh {
    // Mesh Data
	
    private int vertexCount;    // Vertex Count
    private FloatBuffer vertices;    // Vertex Data
    private FloatBuffer normals;    // Texture Coordinates

    // Vertex Buffer Object Names
    public int[] VBO = null;  // Vertex VBO Name
    //IntBuffer VBO;// = IntBuffer.allocate(2);
    public boolean ready = false;
    public Vector3 position;
    
    public int getVertexCount() {
        return vertexCount;
    }

    public Mesh(GL2 gl, String szPath) throws IOException {
    	//load data
    	LoaderOBJ loader = new LoaderOBJ(szPath);
    	position = loader.position;
    	// Generate Vertex Field
    	
    	
        vertexCount = 30258;    //ustawienia 1:3
    	//vertexCount = 263538;	  //ustawienia 1:1
    	
    	
        // TODO loading vertex count
        
        // Allocate Vertex Data
        vertices = FloatBuffer.allocate(vertexCount*3);
        // Allocate Normals Data
        normals = FloatBuffer.allocate(vertexCount*3);  
        
        //load vertices into buffer
        ListIterator<Float> it = loader.faceVertices.listIterator();
        
        while(it.hasNext())
        {
        	vertices.put(it.next());
        }
        vertices.rewind();
        
        //load normals into buffer
        it = loader.n.listIterator();
        while(it.hasNext())
        	normals.put(it.next());
        
        normals.rewind();
        
            // Load Vertex Data Into The Graphics Card Memory
            buildVBOs(gl);  // Build The VBOs
    }

    public void render(GL2 gl) {
    	
    	
        // Enable Pointers
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);  // Enable Vertex Arrays
        // Enable Normlas Arrays
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);  
        
        // Use the vertices
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[0]);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
            
        // Use the normals
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBO[1]);
        gl.glNormalPointer(GL.GL_FLOAT, 0, 0);
        
        // Render
        // Draw All Of The Triangles At Once
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertexCount);  

        // Disable Pointers
        // Disable Vertex Arrays
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);  
        // Disable Texture Coord Arrays
        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);        
    }


    public void buildVBOs(GL2 gl) {
    	VBO = new int[2];
        // Generate And Bind The Vertex Buffer
        gl.glGenBuffers(2, VBO, 0);  // Get A Valid Name
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBO[0]);  // Bind The Buffer
        // Load The Data
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 3 * 4 , vertices, GL2.GL_STATIC_DRAW);

        //Generate And Bind The Normals Buffer
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBO[1]);  // Bind The Buffer
        // Load The Data
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 3 * 4 , normals, GL2.GL_STATIC_DRAW);

        // Our Copy Of The Data Is No Longer Necessary, It Is Safe In The Graphics Card
        vertices = null;
        normals = null;
        ready = true;

    }
}