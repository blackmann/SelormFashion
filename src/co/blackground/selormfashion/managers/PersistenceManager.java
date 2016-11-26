package co.blackground.selormfashion.managers;

import co.blackground.selormfashion.Constants;
import co.blackground.selormfashion.models.Job;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;

/**
 * This singleton is responsible for managing the persistence
 * of jobs taken
 */
@XmlRootElement(name = "jobs")
public class PersistenceManager {

    private static PersistenceManager instance;

    private static ArrayList<Job> jobs;
    private static File file;

    private PersistenceManager() {

    }

    /**
     * Initializes the persistence manager to load saved jobs
     */
    public static void init() {
        instance = new PersistenceManager();
        file = new File(Constants.DATA_FILE);

        loadSavedJobs();
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }

        return instance;
    }

    /**
     * Fetches all jobs from the file
     */
    private static void loadSavedJobs() {
        jobs = new ArrayList<>();
        try {
            JAXBContext context = JAXBContext.newInstance(PersistenceManager.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.unmarshal(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a job to persistence
     */
    public void save(Job job) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersistenceManager.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // replace if already existing
            for (Job j : jobs) {
                if (j.getId().equals(job.getId())) {
                    jobs.remove(j);
                    break;
                }
            }

            jobs.add(job);

            marshaller.marshal(this, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all jobs saved to the persistence
     *
     * @return returns an ArrayList
     */
    @XmlElement(name = "job")
    public ArrayList<Job> getAllJobs() {
        return jobs;
    }
}
