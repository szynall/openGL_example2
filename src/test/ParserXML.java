package test;


import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ParserXML extends DefaultHandler {
		
	   private WGSToZone2000 wgs = new WGSToZone2000();
       private String temp;
       private Peak peak;
       public ArrayList<Peak> peaksList = new ArrayList<Peak>();
       
       public ArrayList<Peak> parseXML() throws IOException, SAXException,
       ParserConfigurationException {
           //pobieramy katalog w którym się znajdujemy
           String cwd = System.getProperty("user.dir");
			    File file = new File(cwd+"\\Szczyty\\Szczyty.XML");
           		try {       			
           			SAXParserFactory spfac = SAXParserFactory.newInstance();
           			
           			//tworzymy obiekt SAXParser
					SAXParser sp = spfac.newSAXParser();
					//sparsuj plik
					sp.parse(file.toString(), this);
			         
				} catch (ParserConfigurationException e1) {
					e1.printStackTrace();
				} catch (SAXException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
           		return peaksList;
       }
       



       public void characters(char[] buffer, int start, int length) {
              temp = new String(buffer, start, length);
       }
       
       //kiedy parser znajdzie nowy element uruchamiana jest ta metoda
       public void startElement(String uri, String localName, 
                     String qName, Attributes attributes) throws SAXException {
              temp = "";
              //jest znaleźliśmy znacznik <P> utwórz nowy obiekt Point i zwiększ licznik
              if (qName.equalsIgnoreCase("Szczyt")) {
                     peak = new Peak();
              }
       }

       //kiedy parser zakonczy przegladanie elementu, uruchamiana jest ta metoda
       public void endElement(String uri, String localName, String qName) throws SAXException
       {
    	   //jak znajdziemy koniec </P> ładujemy dane x,y,z
		  if (qName.equalsIgnoreCase("Szczyt")) 
			  peaksList.add(peak); 
		  else if (qName.equalsIgnoreCase("Nazwa")) 
		        peak.setName(temp);
		  else if (qName.equalsIgnoreCase("Wysokosci")) 
		  {
			  //
			Pattern pattern = Pattern.compile("([\\d]+)[[\\d]+]?");
			Matcher matcher = pattern.matcher(temp);
			if (matcher.find())
			{	
				peak.setHeight(Integer.parseInt((matcher.group(1))));
			}
		  }
		  else if (qName.equalsIgnoreCase("Wspolrzedne")) 
		  {
			  	String error = "Brak danych";
			  	Pattern pattern = Pattern.compile(error);
				Matcher matcher = pattern.matcher(temp);
				if(matcher.find())
				{
					peak.setLatitude(-9999);
					peak.setLongitude(-9999);
				}
				//([\d]+)°([\d]+)′([\d]+[\.[\d]+]?)″[N|S] ([\d]+)°([\d]+)′([\d]+[\.[\d]+]?)″[E|W]
			  	pattern = Pattern.compile("([\\d]+)\\u00B0([\\d]+)\\u2032([\\d]+\\.[\\d]+|[\\d]+)\\u2033[N|S] ([\\d]+)\\u00B0([\\d]+)\\u2032([\\d]+\\.[\\d]+|[\\d]+)\\u2033[E|W]");
				matcher = pattern.matcher(temp);
				if (matcher.find())
				{	
					double lat = Double.parseDouble(matcher.group(1));
					lat	+= Double.parseDouble(matcher.group(2))/60;
					lat += Double.parseDouble(matcher.group(3))/3600;
					lat = 49.5+(49.5-lat);
					double lon = Double.parseDouble(matcher.group(4));
					lon += Double.parseDouble(matcher.group(5))/60;
					lon += Double.parseDouble(matcher.group(6))/3600;
					peak.setLongitude(wgs.ConvertLongitude(lat,lon));
					peak.setLatitude(wgs.ConvertLatitude(lat,lon));
				}
		  }
       }       
}
