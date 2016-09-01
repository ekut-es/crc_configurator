package de.tuebingen.es.crc.configurator.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import de.tuebingen.es.crc.configurator.view.Observer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class Model implements Observable {

    private CRC crc;
    private boolean saved;
    private String crcDescriptionFilePath;

    private final List<Observer> observers;

    private boolean crcWasResized;

    public Model() {
        saved = true;
        observers = new ArrayList<>();
        crcDescriptionFilePath = "";
        crcWasResized = false;
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

    public String getCrcDescriptionFilePath() {
        return this.crcDescriptionFilePath;
    }

    public void setCrcDescriptionFilePath(String crcDescriptionFilePath) {
        this.crcDescriptionFilePath = crcDescriptionFilePath;
    }

    public boolean wasCrcResized() {
        return crcWasResized;
    }

    @Override
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyAllObservers() {
        observers.forEach(Observer::update);
    }

    /**
     * creates a new CRC description file
     * @param rows
     * @param columns
     * @param staticConfigLines
     * @param dynamicConfigLines
     */
    public void createCrcDescriptionFile(int rows, int columns, int staticConfigLines, int dynamicConfigLines) {
        crc = new CRC(rows, columns, staticConfigLines, dynamicConfigLines, this);
        saved = false;
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

        crcDescriptionFilePath = crcDescriptionFile.getAbsolutePath();
    }

    /**
     * wrapper which inserts file path of CRC description file
     * @throws Exception
     */
    public void saveCrcDescriptionFile() throws Exception {
        this.saveCrcDescriptionFile(crcDescriptionFilePath);
    }

    /**
     * saves CRC description file as JSON to the given file path
     * @param filePath
     * @throws Exception
     */
    public void saveCrcDescriptionFile(String filePath) throws Exception {

        File crcDescriptionFile = new File(filePath);
        FileWriter fw = new FileWriter(crcDescriptionFile);

        if(!crcDescriptionFile.exists()) {
            crcDescriptionFile.createNewFile();
        }

        try {
            fw.write(crc.toJSON().toString());
            saved = true;
        } catch (Exception e) {
            throw new Exception("Can't write to file '" + filePath + "'!");
        } finally {
            //noinspection ThrowFromFinallyBlock
            fw.flush();
            //noinspection ThrowFromFinallyBlock
            fw.close();
        }
    }

    /**
     * call copy on CRC and sets saved to true
     * @param rows
     * @param columns
     * @param staticConfigLines
     * @param dynamicConfigLines
     */
    public void editCrc(int rows, int columns, int staticConfigLines, int dynamicConfigLines) {
        saved = false;
        crcWasResized = true;
        crc.edit(rows, columns, staticConfigLines, dynamicConfigLines);
        crcWasResized = false;
    }
}
