package de.tuebingen.es.crc.configurator.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tuebingen.es.crc.configurator.view.ConfiguratorTab;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class Model {

    private CRC crc;
    private boolean saved;

    private List<ConfiguratorTab> observers;

    public Model() {
        saved = true;
        observers = new ArrayList<ConfiguratorTab>();
    }

    public CRC getCrc() {
        return crc;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isSaved() {
        return saved;
    }

    public void attachObserver(ConfiguratorTab observer) {
        observers.add(observer);
    }

    public void removeObserver(ConfiguratorTab observer) {
        observers.remove(observer);
    }

    public void notifyAllObservers() {
        for(ConfiguratorTab observer : observers) {
           observer.update();
        }
    }

    /**
     * reads a CRC description file an checks if it is correct and builds CRC object representation
     * @param crcDescriptionFile
     */
    public void parseCrcDescriptionFile(File crcDescriptionFile) throws Exception {

        // check if file exists
        if(!crcDescriptionFile.exists()) {
            throw new FileNotFoundException("CRC description file does not exist!");
        }

        // check if file is readable
        if(!crcDescriptionFile.canRead()) {
            throw new IOException("CRC description file is not readable!");
        }


        // parse
        JSONObject jsonCrcDescription;

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(crcDescriptionFile));

            jsonCrcDescription = (JSONObject) obj;
        } catch (Exception e) {
            throw new Exception("JSON parser error!");
        }

        // generate object representation CRC
        crc = new CRC(jsonCrcDescription, this);
    }


}
