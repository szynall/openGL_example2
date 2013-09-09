package Geometry;



public class Triangle {
   private Vector3 p1;
   private Vector3 p2;
   private Vector3 p3;
   public Triangle() { }

   public Triangle(Vector3 p1, Vector3 p2, Vector3 p3) 
   {
          this.setP1(p1);
          this.setP2(p2);
          this.setP3(p3);
   }
   

	public Vector3 getP1() {
		return p1;
	}

	public void setP1(Vector3 p1) {
		this.p1 = p1;
	}

	public Vector3 getP2() {
		return p2;
	}

	public void setP2(Vector3 p2) {
		this.p2 = p2;
	}

	public Vector3 getP3() {
		return p3;
	}

	public void setP3(Vector3 p3) {
		this.p3 = p3;
	}
       
      
}