package co.blackground.selormfashion.models;

import co.blackground.selormfashion.managers.PersistenceManager;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.HashMap;
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
    @XmlElementWrapper(name = "measures")
    private HashMap<String, Double> measures;
    private Customer customer;
    private Date dateReceived;
    private Date dateArrived;
    private String userStyle;
    private String userPhoto;
    private double jobCost;
    private double deposit;
    private boolean done;
    private String jobType;


    public Job() {
        this.id = UUID.randomUUID();
        persistenceManager = PersistenceManager.getInstance();
        measures = new HashMap<>();
        jobType = Type.TOPS;
        customer = new Customer();
    }

    public Job(Customer customer) {
        this();
        this.customer = customer;
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

    public void setId(UUID id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public double getJobCost() {
        return jobCost;
    }

    public void setJobCost(double jobCost) {
        this.jobCost = jobCost;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getDateArrived() {
        return dateArrived;
    }

    public void setDateArrived(Date dateArrived) {
        this.dateArrived = dateArrived;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public void addMeasure(String what, double value) {
        measures.put(what, value);
    }

    public HashMap<String, Double> getMeasures() {
        return measures;
    }

    public double getMeasure(String what) {
        if (!measures.containsKey(what)) return 0;
        return measures.get(what);
    }

    public String getUserStyle() {
        return userStyle;
    }

    public void setUserStyle(String userStyle) {
        this.userStyle = userStyle;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    /**
     * Helps in search filters
     */
    public enum Filter {
        ALL, TODAY, NOT_DONE, DONE, TOPS, TROUSERS
    }

    /**
     * Specifies if the job is meant to be a top (shirt, coat, etc)
     * or a pant (trouser)
     */
    public class Type {
        public static final String TOPS = "TOPS";
        public static final String TROUSER = "TROUSER";
    }
}
