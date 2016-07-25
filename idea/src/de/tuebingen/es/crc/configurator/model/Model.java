package de.tuebingen.es.crc.configurator.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class Model {

    private CRC crc;

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
        CRC crc = new CRC(jsonCrcDescription);
    }
}
