package test;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
 
public class HelloWorld
{
    public static void main(String[] args) 
    {
    	
    	GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);
        final FPSAnimator animator = new FPSAnimator(canvas,60);
        //final Animator animator = new Animator(canvas);
        
        JFrame frame = new JFrame("AWT Window Test");
        frame.setSize(800, 600);
        frame.add(canvas);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	new Thread( new Runnable() { 
            		 public void run() { 
            		 animator.stop(); 
            		 System.exit(0); 
            		 } 
            		 }).start();
            }
        });
        canvas.setFocusable(true);
        canvas.requestFocus();
        canvas.addGLEventListener(new Renderer());
        animator.start();
    }
    
}