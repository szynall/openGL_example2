package test;

import Geometry.ComplexNumber;

public class WGSToZone2000{
    private double easting, northing;

        public int ConvertLongitude(double lat, double lng)
        {
            ConvertTo2000(lat, lng);
            return (int)(easting);
        }

        public int ConvertLatitude(double lat, double lng)
        {
            ConvertTo2000(lat, lng);
            return (int)(northing);
        }

        private void ConvertTo2000(double lat, double lng)
        {
            //pierwszy mimosrod elipsoidy
            double ee = 0.0818191910428;

            //promien sfery lagrange'a
            double r0 = 6367449.14577;

            //parametr normujacy
            double ss = 2.0E-6;
            //parametr centrujacy
            double x0 = 5760000.00000000;
            //wspolczynniki wielomianu
            double a0 = 5765181.11148097;
            double a1 = 499800.81713800;
            double a2 = -63.81145283;
            double a3 = 0.83537915;
            double a4 = 0.13046891;
            double a5 = -0.00111138;
            double a6 = -0.00010504;

            //zadane wspolrzedne
            double b, l, lp;

            //dlugosc geodezyjna poludnika srodkowego, konwertujemy na radiany
            lp = 18 * 2 * Math.PI / 360;

            //konwertujemy wartosci na radiany
            b = lat * 2 * Math.PI / 360;
            l = lng * 2 * Math.PI / 360;

            //obliczamy delta l
            l = l - lp;

            //przeksztalcenie lagrange'a
            double u = 1 - ee * Math.sin(b);
            double v = 1 + ee * Math.sin(b);

            double k = Math.pow((u / v), (ee / 2.0));
            double c = k * Math.tan(b / 2 + Math.PI / 4);

            double fi = 2 * Math.atan(c) - Math.PI / 2;
            double lam = l;

            //przekszta³cenie Mercatora
            double p = Math.sin(fi);
            double q = Math.cos(fi) * Math.cos(lam);
            double r = 1 + Math.cos(fi) * Math.sin(lam);
            double s = 1 - Math.cos(fi) * Math.sin(lam);

            double xm = r0 * Math.atan(p / q);

            double ym = 0.5 * r0 * Math.log(r / s);

            //czesc trzecia - zespolone
            //aktualna wspolrzedna
            ComplexNumber comN = new ComplexNumber();
            ComplexNumber.sComp z = comN.new sComp(); 
            z.g = (xm - x0) * ss;
            z.h = ym * ss;
            
            //tablica wspolczynnikow
            ComplexNumber.sComp[] arr;
            arr = new ComplexNumber.sComp[7];
            
            for(int i = 0; i < arr.length; i++){
                arr[i] = comN.new sComp();
            }
            
            arr[0].g = a0;
            arr[0].h = 0;

            arr[1].g = a1;
            arr[1].h = 0;
            arr[2].g = a2;
            arr[2].h = 0;

            arr[3].g = a3;
            arr[3].h = 0;

            arr[4].g = a4;
            arr[4].h = 0;

            arr[5].g = a5;
            arr[5].h = 0;

            arr[6].g = a6;
            arr[6].h = 0;

            //pomoc przy przekazywaniu wyniku
            ComplexNumber.sComp res = comN.new sComp();
            res.g = arr[6].g;
            res.h = arr[6].h;

            //obliczamy wielomian zespolony
            for (int i = 5; i >= 0; i--)
            {
                res = comN.cMult(z, res);
                res = comN.cAdd(arr[i], res);
            }

            //ostateczne przeksztalcenie z wgs84 na 2000/18
            double xout, yout;
            xout = 0.999923 * res.g - 0;
            yout = 0.999923 * res.h + 6500000;
           // yout = 0.999923 * res.h + 500000 + 21/3*1000000;

            easting = yout;
            northing = xout;
        }
    }