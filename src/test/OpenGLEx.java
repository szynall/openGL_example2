package test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;


import com.jogamp.opengl.util.Animator;
//import com.jogamp.opengl.util.FPSAnimator;
 
public class OpenGLEx
{
	static JPanel panel;
	//static FPSAnimator animator;
    static Animator animator;
    static GLCanvas canvas;
    JButton cameraSetButton;
    static Renderer r;
    JLabel posLabel;
	public OpenGLEx()
	{
    	posLabel = new JLabel("Pozycja: ");
    	JLabel cordinatesLabel = new JLabel("Wspó³rzêdne: ");
    	final JTextComponent cordinates = new JTextArea("0");
        cameraSetButton = new JButton("Ustaw");
        
        cameraSetButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e)
            {
					//try {
						//r.cameraSet(Double.parseDouble(lat.getText()),Double.parseDouble(lon.getText()),
						//		Integer.parseInt(latmin.getText()),Integer.parseInt(lonmin.getText()),
						//		Integer.parseInt(latsec.getText()),Integer.parseInt(lonsec.getText()));
						r.cameraSet(cordinates.getText());
					//} catch (NumberFormatException e1) {
					//}
            }
        });      
        
        GLProfile glp =  GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(glp);
        canvas = new GLCanvas(caps);
        canvas.setPreferredSize(new Dimension(800,600));
        
        //gui
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setSize(800, 800);
       // GridLayout buttonLayout = new GridLayout(0,3);
        
        JPanel buttonpanel = new JPanel();
        cordinates.setPreferredSize(new Dimension(200,20));
        buttonpanel.add(cordinatesLabel);
        buttonpanel.add(cordinates);
        buttonpanel.add(cameraSetButton);
        panel.add("North",buttonpanel);
        panel.add("Center",canvas);
        panel.add("South",posLabel);
        
        
        animator = new Animator(canvas);
        //animator = new FPSAnimator(canvas,60);
        canvas.setFocusable(true);
        canvas.requestFocus();
        animator.start();
	}
	
	public static void main(String[] args) 
    {
		
		JFrame frame = new JFrame();
		OpenGLEx openGL = new OpenGLEx();
		r = new Renderer(openGL);
		canvas.addGLEventListener(r);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
       // Thread t = new Thread(new Loader(r));
		//t.start();
       
        
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
    }  
}
	   
    