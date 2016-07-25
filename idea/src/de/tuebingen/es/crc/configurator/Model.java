package de.tuebingen.es.crc.configurator;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class Model {

    /**
     * reads a CRC description file an checks if it is correct and builds CRC object representation
     * @param crcDescriptionFile
     */
    public void parseCrcDescriptionFile(File crcDescriptionFile) throws Exception {

        // check if file exists
        if(!crcDescriptionFile.exists()) {
            throw new FileNotFoundException("CRC description file does not exist.");
        }

        // check if file is readable
        if(!crcDescriptionFile.canRead()) {
            throw new IOException("CRC description file is not readable.");
        }

        // parse
        throw new Exception("CRC description file is malformed.");
    }
}
