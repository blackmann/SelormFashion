package co.blackground.selormfashion.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

/**
 * Class representing the customer instance.
 */
@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {

    private String name;
    private String mobile;

    @XmlAttribute(name = "id")
    private UUID id;

    public Customer() {
    }

    public Customer(String name) {
        id = UUID.randomUUID();
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }
}
