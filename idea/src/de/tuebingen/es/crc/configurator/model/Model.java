package de.tuebingen.es.crc.configurator.model;

import de.tuebingen.es.crc.configurator.model.verilog.CRCVerilogGenerator;
import de.tuebingen.es.crc.configurator.model.verilog.CRCVerilogPreprocessorGenerator;
import de.tuebingen.es.crc.configurator.model.verilog.CRCVerilogQuestaSimScriptGenerator;
import de.tuebingen.es.crc.configurator.model.verilog.CRCVerilogTestBenchGenerator;
import de.tuebingen.es.crc.configurator.view.ConfigurationTab;
import de.tuebingen.es.crc.configurator.view.Observer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class Model implements Observable {

    private CRC crc;
    private boolean saved;
    private String crcDescriptionFilePath;

    private final List<Observer> observers;

    private boolean crcWasResized;

    private Thread fileWatcherThread;

    public Model() {
        saved = true;
        observers = new ArrayList<>();
        crcDescriptionFilePath = "";
        crcWasResized = false;
    }

    public CRC getCrc() {
        return crc;
    }

    @SuppressWarnings("SameParameterValue")
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
     * @param dataWidth
     */
    public void createCrcDescriptionFile(int rows, int columns, int staticConfigLines, int dynamicConfigLines, boolean inputsNorth, boolean inputsSouth, int dataWidth) {
        crc = new CRC(rows, columns, staticConfigLines, dynamicConfigLines, inputsNorth, inputsSouth, dataWidth, this);
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

        crcDescriptionFilePath = crcDescriptionFile.getPath();

        this.startFileWatcher(crcDescriptionFile);
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

        this.startFileWatcher(crcDescriptionFile);

    }

    /**
     * call copy on CRC and sets saved to true
     * @param rows
     * @param columns
     * @param staticConfigLines
     * @param dynamicConfigLines
     * @param dataWidth
     */
    public void editCrc(int rows, int columns, int staticConfigLines, int dynamicConfigLines, boolean inputsNorth, boolean inputsSouth, int dataWidth) {
        saved = false;
        crcWasResized = true;
        crc.edit(rows, columns, staticConfigLines, dynamicConfigLines, inputsNorth, inputsSouth, dataWidth);
        crcWasResized = false;
    }

    public void resetConfig(ConfigurationTab.ConfigurationTabType configurationTabType, int num) {
        saved = false;
        if(configurationTabType == ConfigurationTab.ConfigurationTabType.STATIC) {
            crc.resetStaticConfig(num);
        } else {
            crc.resetDynamicConfig(num);
        }
    }

    private void checkFile(File file) throws Exception {
        // check is a directory
        if(file.isDirectory()) {
            throw new Exception("Can't overwrite '" + file.getPath() + "' because it is a directory!");
        }

        // check if file exists and is writable
        if(file.exists() && !file.canWrite()) {
            throw new Exception("Can't write '" + file.getPath() + "'!");
        }

    }

    public void exportVerilogCode(File verilogFile, boolean fifosBetweenPes, int interPeFifoLength, int inputFifoLength, int outputFifoLength, boolean generateTestbenchAndQuestaSimScript, File testBenchFile, File questaSimScript, boolean generatePreprocessor, File preprocessorFile, int clockCycle) throws Exception {

        // check verilog file
        this.checkFile(verilogFile);

        // generate verilog file
        CRCVerilogGenerator crcVerilogGenerator = new CRCVerilogGenerator(
                this.crc,
                fifosBetweenPes,
                interPeFifoLength,
                inputFifoLength,
                outputFifoLength
        );

        // create files if they not exist
        if(!verilogFile.exists()) {
            verilogFile.createNewFile();
        }

        FileWriter verilogFileWriter = new FileWriter(verilogFile);

        // write verilog file
        try {
            verilogFileWriter.write(crcVerilogGenerator.generate());
        } catch (Exception e) {
            throw new Exception("Can't write to file '" + verilogFile.getAbsolutePath() + "'!");
        } finally {
            //noinspection ThrowFromFinallyBlock
            verilogFileWriter.flush();
            //noinspection ThrowFromFinallyBlock
            verilogFileWriter.close();
        }


        if(generateTestbenchAndQuestaSimScript) {
            // check test bench file
            this.checkFile(testBenchFile);

            // generate test bench file
            CRCVerilogTestBenchGenerator crcVerilogTestBenchGenerator = new CRCVerilogTestBenchGenerator(this.crc);

            if(!testBenchFile.exists()) {
                testBenchFile.createNewFile();
            }

            FileWriter testBenchFileWriter = new FileWriter(testBenchFile);

            // write test bench file
            try {
                testBenchFileWriter.write(crcVerilogTestBenchGenerator.generate());
            } catch (Exception e) {
                throw new Exception("Can't write to file '" + testBenchFile.getAbsolutePath() + "'!");
            } finally {
                //noinspection ThrowFromFinallyBlock
                testBenchFileWriter.flush();
                //noinspection ThrowFromFinallyBlock
                testBenchFileWriter.close();
            }


            // check QuestaSim script
            this.checkFile(questaSimScript);

            // generate QuestaSim script
            CRCVerilogQuestaSimScriptGenerator crcVerilogQuestaSimScriptGenerator = new CRCVerilogQuestaSimScriptGenerator(verilogFile.getName(), testBenchFile.getName());

            if(!questaSimScript.exists()) {
                questaSimScript.createNewFile();
            }

            FileWriter questaSimScriptFileWriter = new FileWriter(questaSimScript);

            // write QuestaSim script
            try {
                questaSimScriptFileWriter.write(crcVerilogQuestaSimScriptGenerator.generate());
            } catch (Exception e) {
                throw new Exception("Can't write to file '" + questaSimScript.getAbsolutePath() + "'!");
            } finally {
                //noinspection ThrowFromFinallyBlock
                questaSimScriptFileWriter.flush();
                //noinspection ThrowFromFinallyBlock
                questaSimScriptFileWriter.close();
            }
        }

        if(generatePreprocessor) {
            // check test bench file
            this.checkFile(preprocessorFile);

            // generate test bench file
            CRCVerilogPreprocessorGenerator crcVerilogPreprocessorGenerator = new CRCVerilogPreprocessorGenerator(this.crc, clockCycle);

            if(!preprocessorFile.exists()) {
                preprocessorFile.createNewFile();
            }

            FileWriter preprocessorFileWriter = new FileWriter(preprocessorFile);

            // write test bench file
            try {
                preprocessorFileWriter.write(crcVerilogPreprocessorGenerator.generate());
            } catch (Exception e) {
                throw new Exception("Can't write to file '" + preprocessorFile.getAbsolutePath() + "'!");
            } finally {
                //noinspection ThrowFromFinallyBlock
                preprocessorFileWriter.flush();
                //noinspection ThrowFromFinallyBlock
                preprocessorFileWriter.close();
            }
        }
    }

    private void startFileWatcher(File file) {
        /*
        if(fileWatcherThread != null) {
            if(fileWatcherThread.isAlive()) {
                fileWatcherThread.stop();
            }

        }

        fileWatcherThread = new Thread(new FileWatcher(file));
        fileWatcherThread.start();
        */
    }
}
