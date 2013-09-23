package test;

import Geometry.ComplexNumber;
import Geometry.ComplexNumber.sComp;

public class Zone2000ToWGS
{

    private double latitude, longitude;

    /// <summary>
    /// konwertuje wspolrzedne z ukladu 2000/18 na WGS84 i zwraca wartosc wysokosci geograficznej
    /// </summary>
    /// <param name="northing"></param>
    /// <param name="easting"></param>
    /// <returns></returns>
    public double ConvertLatitudeToWGS84(double northing, double easting)
    {
        ToWGS84(northing, easting);
        return latitude;
    }
    /// <summary>
    /// konwertuje wspolrzedne z ukladu 2000/18 na WGS84 i zwraca wartosc szerokosci geograficznej
    /// </summary>
    /// <param name="northing"></param>
    /// <param name="easting"></param>
    /// <returns></returns>
    public double ConvertLongitudeToWGS84(double northing, double easting)
    {
        ToWGS84(northing, easting);
        return longitude;
    }
    /// <summary>
    /// konwertuje wspolrzedne z ukladu 2000/18 na WGS84
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    private void ToWGS84(double x, double y)
    {
        double xg, yg;

        //przesuwamy i przeskalowujemy uklad do xg yg
        xg = (x) / 0.999923;
        yg = (y - 6500000) / 0.999923;

        //parametr normalizujacy
        double ss = 2.0e-6;

        //wspolczynnik wielomianu
        double a0 = 5765181.11148097;

        //przekszta³cenie p³aszczyzny Gaussa-Krugera na p³aszczyznê Mercatora
        ComplexNumber comN = new ComplexNumber();
        ComplexNumber.sComp z = comN.new sComp();
        z.g = (xg - a0) * ss;
        z.h = yg * ss;

        //wspolczynniki wielomianu
        double b0 = 5760000;
        double b1 = 500199.26224125;
        double b2 = 63.88777449;
        double b3 = -0.82039170;
        double b4 = -0.13125817;
        double b5 = 0.00101782;
        double b6 = 0.00010778;

        //tablica wspolczynnikow wielomianu
        ComplexNumber.sComp[] arr;
        arr = new ComplexNumber.sComp[7];
        
        for(int i = 0; i < arr.length; i++){
            arr[i] = comN.new sComp();
        }
        
        arr[0].g = b0;
        arr[0].h = 0;

        arr[1].g = b1;
        arr[1].h = 0;

        arr[2].g = b2;
        arr[2].h = 0;

        arr[3].g = b3;
        arr[3].h = 0;

        arr[4].g = b4;
        arr[4].h = 0;

        arr[5].g = b5;
        arr[5].h = 0;

        arr[6].g = b6;
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

        double xmerc, ymerc;
        xmerc = res.g;
        ymerc = res.h;

        //walcowe - poprzeczne odwzorowanie p³aszczyzny na strefê
        //promien sfery lagrange'a
        double r0 = 6367449.14577;

        double alfa, beta;
        alfa = xmerc / r0;
        beta = ymerc / r0;

        double w, fi, dlam;

        w = 2 * Math.atan(Math.exp(beta)) - Math.PI / 2;

        fi = Math.asin(Math.cos(w) * Math.sin(alfa));

        dlam = Math.atan(Math.tan(w) / Math.cos(alfa));

        //odwzorowanie ca³ej strefy na powierzchniê elipsoidy 
        double l, b;

        //wspolczynniki szeregu trygonometrycznego
        double c2 = 0.0033565514856;
        double c4 = 0.0000065718731;
        double c6 = 0.0000000176466;
        double c8 = 0.0000000000540;

        //oblicamy latitude
        b = fi + c2 * Math.sin(2 * fi) + c4 * Math.sin(4 * fi) + c6 * Math.sin(6 * fi) + c8 * Math.sin(8 * fi);

        b = b * 360.0 / (2 * Math.PI);

        //obliczamy longitude
        dlam = dlam * 360.0 / (2 * Math.PI);

        latitude = b;

        l = dlam + 18;

        longitude = l;
    }
}