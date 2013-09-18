package test;

import java.util.ArrayList;
import java.util.List;

//klasa przechowuj¹ca fragmenty terenu
public class Mesh {
	public List<Float> faceVertices = new ArrayList<Float>(); //lista wierzcholkow triangli
    public List<Float> normals = new ArrayList<Float>();//Lista normalnych
    int width,length;
    public int id;

	public Mesh(List<Float> _f, List<Float> _n , int _width, int _length)
	{
		faceVertices = _f;
		normals = _n;
		width = _width;
		length = _length;
	}
}