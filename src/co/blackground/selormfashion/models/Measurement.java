package co.blackground.selormfashion.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing the measurement instance of a customer
 */
@XmlRootElement(name = "measure")
@XmlAccessorType(XmlAccessType.FIELD)
public class Measurement {
    private double sleeveLength;

    public void setSleeveLength(double sleeveLength) {
        this.sleeveLength = sleeveLength;
    }

    public double getSleeveLength() {
        return sleeveLength;
    }

    public double sleeveLengthProperty() {
        return sleeveLength;
    }
}
