package co.blackground.selormfashion.models;

import co.blackground.selormfashion.managers.PersistenceManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.UUID;

/**
 * Class representing a job to be done.
 */
@XmlRootElement(name = "job")
@XmlAccessorType(XmlAccessType.FIELD)
public class Job {

    private transient PersistenceManager persistenceManager;

    @XmlAttribute(name = "id")
    private UUID id;
    private Customer customer;

    private Measurement measurement;

    private Date dateReceived;

    private double jobCost;
    private double deposit;
    private boolean done;

    public Job() {

    }

    public Job(Customer customer, Measurement measurement) {
        this.customer = customer;
        this.measurement = measurement;
        persistenceManager = PersistenceManager.getInstance();
        id = UUID.randomUUID();
    }

    /**
     * Saves this job to persistence
     */
    public void save() {
        persistenceManager.save(this);
    }

    public UUID getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public double getJobCost() {
        return jobCost;
    }

    public double getDeposit() {
        return deposit;
    }

    public boolean isDone() {
        return done;
    }
}
