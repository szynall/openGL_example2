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
	public OpenGLEx()
	{
    	JLabel lonLabel = new JLabel("Longitude: ");
    	JLabel latLabel = new JLabel("Latitude: ");
    	JLabel lonmLabel = new JLabel("  '  ");
    	JLabel latmLabel = new JLabel("  '  ");
    	JLabel lonsLabel = new JLabel("  \"  ");
    	JLabel latsLabel = new JLabel("  \"  ");
    	final JTextComponent lon = new JTextArea("0");
    	final JTextComponent lat = new JTextArea("0");
    	final JTextComponent lonmin = new JTextArea("0");
    	final JTextComponent latmin = new JTextArea("0");
    	final JTextComponent lonsec = new JTextArea("0");
    	final JTextComponent latsec = new JTextArea("0");
        cameraSetButton = new JButton("Ustaw");
        
        cameraSetButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e)
            {
					try {
						r.cameraSet(Double.parseDouble(lat.getText()),Double.parseDouble(lon.getText()),
								Integer.parseInt(latmin.getText()),Integer.parseInt(lonmin.getText()),
								Integer.parseInt(latsec.getText()),Integer.parseInt(lonsec.getText()));
					} catch (NumberFormatException e1) {
						
					}
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
        GridLayout buttonLayout = new GridLayout(0,13);
        
        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(buttonLayout);
        buttonpanel.add(lonLabel);
        buttonpanel.add(lon);
        buttonpanel.add(lonmLabel);
        buttonpanel.add(lonmin);
        buttonpanel.add(lonsLabel);
        buttonpanel.add(lonsec);
        
        buttonpanel.add(latLabel);
        buttonpanel.add(lat);
        buttonpanel.add(latmLabel);
        buttonpanel.add(latmin);
        buttonpanel.add(latsLabel);
        buttonpanel.add(latsec);
        buttonpanel.add(cameraSetButton);
        panel.add("North",buttonpanel);
        panel.add("Center",canvas);
        
        
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
		r = new Renderer();
		canvas.addGLEventListener(r);
        frame.getContentPane().add(panel);
        frame.pack();
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
    }  
}
	   
    