package geocoder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Locations {

    private String address;

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress.toLowerCase().replace("ul.", "").replace("al.", "").replace(' ', '+');
    }

    private String urlAddress;
    private double latitude;
    private double longitude;

    public Locations(String address) throws ParserConfigurationException {
        setAddress(address);
        setUrlAddress(address);
        geocode();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private URL createUrl(String address) throws MalformedURLException {
        return new URL("https://nominatim.openstreetmap.org/search?q=" + address + "&format=xml");
    }

    private void geocode() throws ParserConfigurationException {
        double lat, lon;

        lat = lon = 99.999999;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        try {
            Document doc = db.parse(createUrl(getUrlAddress()).openStream());
            if (doc != null) {
                lat = Double.parseDouble(doc.getElementsByTagName("place").item(0).getAttributes().getNamedItem("lat").toString().replace('"', ' ').replace("lat=", ""));
                lon = Double.parseDouble(doc.getElementsByTagName("place").item(0).getAttributes().getNamedItem("lon").toString().replace('"', ' ').replace("lon=", ""));
            }
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }

        if (lat != 99.999999 && lon != 99.999999) {
            setLatitude(lat);
            setLongitude(lon);
        }
    }
}
