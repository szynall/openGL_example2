package Geometry;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zaku
 */
public class ComplexNumber {
        //liczba urojona
        public class sComp
        {
            public double g,  //rzeczywista
                          h;  //urojona
        }

        //mnozenie urojone
        public sComp cMult(sComp a, sComp b)
        {
            sComp res = new sComp();
            res.g = a.g * b.g - a.h * b.h;
            res.h = a.g * b.h + a.h * b.g;
            return res;
        }

        //dodawanie urojone
        public sComp cAdd(sComp a, sComp b)
        {
            sComp res = new sComp();
            res.g = a.g + b.g;
            res.h = a.h + b.h;
            return res;
        }
}
