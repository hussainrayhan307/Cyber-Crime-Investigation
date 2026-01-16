package investigation;

import java.util.ArrayList;

//import org.junit.Test; 

/*  
 * This class represents a cyber crime investigation.  It contains a directory of hackers, which is a resizing
 * hash table. The hash table is an array of HNode objects, which are linked lists of Hacker objects.  
 * 
 * The class contains methods to add a hacker to the directory, remove a hacker from the directory.
 * You will implement these methods, to create and use the HashTable, as well as analyze the data in the directory.
 * 
 * @author Colin Sullivan
 */
public class CyberCrimeInvestigation {
       
    private HNode[] hackerDirectory;
    private int numHackers = 0; 

    public CyberCrimeInvestigation() {
        hackerDirectory = new HNode[10];
    }

    /**
     * Initializes the hacker directory from a file input.
     * @param inputFile
     */
    public void initializeTable(String inputFile) { 
        // DO NOT EDIT
        StdIn.setFile(inputFile);  
        while(!StdIn.isEmpty()){
            addHacker(readSingleHacker());
        }
    }

    /**
     * Reads a single hackers data from the already set file,
     * Then returns a Hacker object with the data, including 
     * the incident data.
     * 
     * StdIn.setFile() has already been called for you.
     * 
     * @param inputFile The name of the file to read hacker data from.
     */
    public Hacker readSingleHacker(){ 
        // WRITE YOUR CODE HERE

        String hackerName = StdIn.readLine();
        String ipAddressHash = StdIn.readLine();
        String location = StdIn.readLine();
        String operatingSystem = StdIn.readLine();
        String webServer = StdIn.readLine();
        String date = StdIn.readLine();
        String urlHash = StdIn.readLine();

        Incident incidentInformation = new Incident(operatingSystem, webServer, date, location, ipAddressHash, urlHash);
        Hacker hackerInformation = new Hacker(hackerName);
        hackerInformation.addIncident(incidentInformation);

        return hackerInformation;

        //return null; // Replace this line
    }

    /**
     * Adds a hacker to the directory.  If the hacker already exists in the directory,
     * instead adds the given Hacker's incidents to the existing Hacker's incidents.
     * 
     * After a new insertion (NOT if a hacker already exists), checks if the number of 
     * hackers in the table is >= table length divided by 2. If so, calls resize()
     * 
     * @param toAdd
     */
    public void addHacker(Hacker toAdd) {
        // WRITE YOUR CODE HERE

        //int tableLength = hackerDirectory.length;
        int index = toAdd.hashCode() % hackerDirectory.length;

        HNode hackerToAdd = new HNode(toAdd);
        
        if (hackerDirectory[index] == null) {
            hackerDirectory[index] = hackerToAdd;
            numHackers++;
            if (numHackers >= hackerDirectory.length/2) {
                resize();
            }
            return;
        }

        HNode ptr = hackerDirectory[index];

        while (ptr != null) {

            if (ptr.getHacker().equals(toAdd)) {
                ptr.getHacker().getIncidents().addAll(toAdd.getIncidents());
                return;
            }
            if (ptr.getNext() == null) { // unique item
                ptr.setNext(hackerToAdd);
                numHackers++;
                break;
            }

            ptr = ptr.getNext();

        }

        if (numHackers >= hackerDirectory.length/2) {
            resize();
        }


    }

    /**
     * Resizes the hacker directory to double its current size. Rehashes all hackers
     * into the new doubled directory.
     */
    private void resize() {
        // WRITE YOUR CODE HERE

        HNode[] temp = hackerDirectory;
        numHackers = 0;
        hackerDirectory = new HNode[hackerDirectory.length * 2];

        for (int i = 0; i < temp.length; i++) {
            HNode ptr = temp[i];
            while (ptr != null) {
                addHacker(ptr.getHacker());
                ptr = ptr.getNext();
            }
        }
        
        
    }

    /**
     * Searches the hacker directory for a hacker with the given name.
     * Returns null if the Hacker is not found
     * 
     * @param toSearch
     * @return The hacker object if found, null otherwise.
     */
    public Hacker search(String toSearch) {
        // WRITE YOUR CODE HERE 

        int indexToSearch = Math.abs(toSearch.hashCode() % hackerDirectory.length);
        
        HNode hackerToFind = hackerDirectory[indexToSearch];

        while (hackerToFind != null) {
            if (hackerToFind.getHacker().getName().equals(toSearch)) {
                return hackerToFind.getHacker();
            }
            hackerToFind = hackerToFind.getNext();
        }

        return null;


    }

    /**
     * Removes a hacker from the directory.  Returns the removed hacker object.
     * If the hacker is not found, returns null.
     * 
     * @param toRemove
     * @return The removed hacker object, or null if not found.
     */
    public Hacker remove(String toRemove) {
        // WRITE YOUR CODE HERE
        
        int indexToRemove = Math.abs(toRemove.hashCode() % hackerDirectory.length);

        HNode ptr = hackerDirectory[indexToRemove];
        HNode prev = null;

        
        while (ptr != null) {
            if (ptr.getHacker().getName().equals(toRemove)) {
                if (prev == null) {
                    hackerDirectory[indexToRemove] = ptr.getNext();
                    numHackers--;
                    return ptr.getHacker();
                }
                else {
                    prev.setNext(ptr.getNext());
                    numHackers--;
                    return ptr.getHacker();
                }
            }
            prev = ptr;
            ptr = ptr.getNext();
        }
        

        return null;


    } 

    /**
     * Merges two hackers into one based on number of incidents.
     * 
     * @param hacker1 One hacker
     * @param hacker2 Another hacker to attempt merging with
     * @return True if the merge was successful, false otherwise.
     */
    public boolean mergeHackers(String hacker1, String hacker2) {  
        // WRITE YOUR CODE HERE 

        if (search(hacker1).equals(null) || search(hacker2).equals(null)) {
            return false;
        }
        
        Hacker hackerA = search(hacker1);
        ArrayList<Incident> hackerAincidents = hackerA.getIncidents();

        Hacker hackerB = search(hacker2);
        ArrayList<Incident> hackerBincidents = hackerB.getIncidents();


        if (hackerBincidents.size() > hackerAincidents.size()) {
            hackerBincidents.addAll(hackerA.getIncidents());
            hackerB.addAlias(hacker1);
            remove(hacker1);
            return true;
        }

        else if (hackerAincidents.size() > hackerBincidents.size()) {
            hackerAincidents.addAll(hackerB.getIncidents());
            hackerA.addAlias(hacker2);
            remove(hacker2);
            return true;
        }

        else if (hackerAincidents.size() == hackerBincidents.size()) {
            hackerAincidents.addAll(hackerB.getIncidents());
            hackerA.addAlias(hacker2);
            remove(hacker2);
            return true;
        }

        return false;
        
        //return false; // Replace this line
    }

    /**
     * Gets the top n most wanted Hackers from the directory, and
     * returns them in an arraylist. 
     * 
     * You should use the provided MaxPQ class to do this. You can
     * add all hackers, then delMax() n times, to get the top n hackers.
     * 
     * @param n
     * @return Arraylist containing top n hackers
     */
    public ArrayList<Hacker> getNMostWanted(int n) {
        // WRITE YOUR CODE HERE

        MaxPQ<Hacker> maxPQ = new MaxPQ<>();
        ArrayList<Hacker> mostWanted = new ArrayList<>();

        for (int i = 0; i < hackerDirectory.length; i++) {
            HNode ptr = hackerDirectory[i];
            while (ptr != null) {
                maxPQ.insert(hackerDirectory[i].getHacker());
                hackerDirectory[i] = hackerDirectory[i].getNext();
                ptr = ptr.getNext();
            }
        }

        for (int i = 0; i < n; i++) {
            mostWanted.add(maxPQ.delMax());
        }

        return mostWanted;

        //return null; // Replace this line
    }

    /**
     * Gets all hackers that have been involved in incidents at the given location.
     * 
     * You should check all hackers, and ALL of each hackers incidents.
     * You should not add a single hacker more than once.
     * 
     * @param location
     * @return Arraylist containing all hackers who have been involved in incidents at the given location.
     */
    public ArrayList<Hacker> getHackersByLocation(String location) {
        // WRITE YOUR CODE HERE 

        ArrayList<Hacker> matchingHackers = new ArrayList<>();

        for (int i = 0; i < hackerDirectory.length; i++) {
            HNode ptr = hackerDirectory[i];
            while (ptr != null) {
                ArrayList<Incident> allIncidents = hackerDirectory[i].getHacker().getIncidents();

                for (int j = 0; j < allIncidents.size(); j++) {
                    Incident specificIncident = allIncidents.get(j);
                    if (specificIncident.getLocation().equals(location)) {
                        matchingHackers.add(hackerDirectory[i].getHacker());
                        break;
                    }
                }

                hackerDirectory[i] = hackerDirectory[i].getNext();
                ptr = ptr.getNext();
            }
        }

        return matchingHackers;

        //return null; // Replace this line
    }

    /**
     * PROVIDED--DO NOT MODIFY!
     * Outputs the entire hacker directory to the terminal. 
     */
     public void printHackerDirectory() { 
        System.out.println(toString());
    } 

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.hackerDirectory.length; i++) {
            HNode headHackerNode = hackerDirectory[i];
            while (headHackerNode != null) {
                if (headHackerNode.getHacker() != null) {
                    sb.append(headHackerNode.getHacker().toString()).append("\n");
                    ArrayList<Incident> incidents = headHackerNode.getHacker().getIncidents();
                    for (Incident incident : incidents) {
                        sb.append("\t" +incident.toString()).append("\n");
                    }
                }
                headHackerNode = headHackerNode.getNext();
            } 
        }
        return sb.toString();
    }

    public HNode[] getHackerDirectory() {
        return hackerDirectory;
    }
}
