package de.tuebingen.es.crc.configurator.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class CRC {

    private final Model model;

    private int rows;
    private int columns;
    private int staticConfigLines;
    private int dynamicConfigLines;
    private boolean inputsNorth;
    private boolean inputsSouth;
    private int dataWidth;

    private String comment;

    private HashMap<Integer, Configuration> staticConfigs;
    private HashMap<Integer, Configuration> dynamicConfigs;

    private ArrayList<ArrayList<FU>> fuMatrix;

// --Commented out by Inspection START (01/09/16 13:47):
//    public CRC(Model model) {
//        this.model = model;
//        rows = 2;
//        columns = 2;
//        staticConfigLines = 0;
//        dynamicConfigLines = 0;
//    }
// --Commented out by Inspection STOP (01/09/16 13:47)

    /**
     * generates a CRC object with the given parameters
     * @param rows
     * @param columns
     * @param staticConfigLines
     * @param dynamicConfigLines
     * @param inputsNorth
     * @param inputsSouth
     * @param dataWidth
     * @param model
     */
    public CRC(int rows, int columns, int staticConfigLines, int dynamicConfigLines, boolean inputsNorth, boolean inputsSouth, int dataWidth, Model model) {
        this.model = model;
        this.setRows(rows);
        this.setColumns(columns);
        this.setStaticConfigLines(staticConfigLines);
        this.setDynamicConfigLines(dynamicConfigLines);
        this.setInputsNorth(inputsNorth);
        this.setInputsSouth(inputsSouth);
        this.setDataWidth(dataWidth);
        this.setComment("");

        this.generateFuMatrix();

        this.staticConfigs = new HashMap<>();

        for(int i = 0; i < this.staticConfigLines; i++) {
            Configuration staticConfig = new Configuration(this, i);
            staticConfigs.put(i, staticConfig);
        }

        this.dynamicConfigs = new HashMap<>();

        for(int i = 0; i < this.dynamicConfigLines; i++) {
            Configuration dynamicConfig = new Configuration(this, i);
            dynamicConfigs.put(i, dynamicConfig);
        }
    }

    /**
     * generates a CRC object with properties from a CRC description file
     * @param jsonCrcDescription
     * @throws Exception
     */
    public CRC(JSONObject jsonCrcDescription, Model model) throws Exception {

        this.model = model;

        // read rows, columns, static config lines, and dynamic config lines
        this.setRows(Integer.parseInt(jsonCrcDescription.get("rows").toString()));
        this.setColumns(Integer.parseInt(jsonCrcDescription.get("columns").toString()));
        this.setStaticConfigLines(Integer.parseInt(jsonCrcDescription.get("staticConfigLines").toString()));
        this.setDynamicConfigLines(Integer.parseInt(jsonCrcDescription.get("dynamicConfigLines").toString()));

        if(jsonCrcDescription.containsKey("inputsNorth")) {
            this.setInputsNorth(Boolean.parseBoolean(jsonCrcDescription.get("inputsNorth").toString()));
        } else {
            this.setInputsNorth(false);
        }

        if(jsonCrcDescription.containsKey("inputsSouth")) {
            this.setInputsSouth(Boolean.parseBoolean(jsonCrcDescription.get("inputsSouth").toString()));
        } else {
            this.setInputsSouth(false);
        }

        this.setDataWidth(Integer.parseInt(jsonCrcDescription.get("dataWidth").toString()));

        this.setComment(jsonCrcDescription.get("comment").toString());

        this.generateFuMatrix();

        this.staticConfigs = new HashMap<>();
        this.dynamicConfigs = new HashMap<>();

        JSONArray pes = (JSONArray) jsonCrcDescription.get("pes");

        if(pes == null) {
            throw new Exception("CRC description file does not contain 'pes'!");
        }

        // set modes in all FUs
        //noinspection unchecked
        for (JSONObject pe : (Iterable<JSONObject>) pes) {
            FU fu = getFu(Integer.parseInt(pe.get("row").toString()), Integer.parseInt(pe.get("column").toString()));
            JSONArray fuModes = (JSONArray) pe.get("availableFuModes");

            //noinspection unchecked
            for (String fuMode : (Iterable<String>) fuModes) {
                fu.setMode(FU.FuMode.valueOf(fuMode), true);
            }

            fu.setLut8BitContentHexString(pe.get("lut8BitContentHexString").toString());
        }

        // generate static configs
        JSONArray staticConfigs = (JSONArray) jsonCrcDescription.get("staticConfigs");

        if(staticConfigs == null) {
            throw new Exception("CRC description file does not contain 'staticConfigs'!");
        }

        //noinspection unchecked
        for (JSONObject staticConfig : (Iterable<JSONObject>) staticConfigs) {
            Configuration configuration = this.readConfigFromJSON(staticConfig);

            this.staticConfigs.put(configuration.getNumber(), configuration);
        }

        // generate dynamic configs
        JSONArray dynamicConfigs = (JSONArray) jsonCrcDescription.get("dynamicConfigs");

        if(dynamicConfigs == null) {
            throw new Exception("CRC description file does not contain 'dynamicConfigs'!");
        }

        //noinspection unchecked
        for (JSONObject dynamicConfig : (Iterable<JSONObject>) dynamicConfigs) {
            Configuration configuration = this.readConfigFromJSON(dynamicConfig);

            this.dynamicConfigs.put(configuration.getNumber(), configuration);
        }

    }

    /**
     * reads a config from JSON
     * @param config
     * @return Configuration config
     * @throws Exception
     */
    private Configuration readConfigFromJSON(JSONObject config) throws Exception {

        // read PEs
        JSONArray pes = (JSONArray) config.get("pes");

        if(pes == null) {
            throw new Exception("CRC description file section staticConfigs does not contain 'pes'!");
        }

        Configuration configuration = new Configuration(this, Integer.parseInt(config.get("configNumber").toString()));
        configuration.setComment(config.get("comment").toString());

        //noinspection unchecked
        for (JSONObject peJson : (Iterable<JSONObject>) pes) {
            PE pe = configuration.getPe(Integer.parseInt(peJson.get("row").toString()), Integer.parseInt(peJson.get("column").toString()));

            pe.setDataFlagOutN0(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutN0").toString()));
            pe.setDataFlagOutN1(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutN1").toString()));
            pe.setDataFlagOutE0(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutE0").toString()));
            pe.setDataFlagOutE1(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutE1").toString()));
            pe.setDataFlagOutS0(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutS0").toString()));
            pe.setDataFlagOutS1(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutS1").toString()));
            pe.setDataFlagInFu0(PE.DataFlagInFuDriver.valueOf(peJson.get("dataFlagInFu0").toString()));
            pe.setDataFlagInFu1(PE.DataFlagInFuDriver.valueOf(peJson.get("dataFlagInFu1").toString()));
            pe.setDataFlagInFuMux(PE.DataFlagInFuDriver.valueOf(peJson.get("dataFlagInFuMux").toString()));
            pe.setConstantRegisterDataContent(Long.parseLong(peJson.get("constRegDataContent").toString()));
            pe.setConstantRegisterFlagContent(Boolean.parseBoolean(peJson.get("constRegFlagContent").toString()));
            pe.setFuFunction(FU.FuFunction.valueOf(peJson.get("fuFunction").toString()));
            pe.setSignedData(peJson.get("fuSignedness").toString().equals("signed"));
        }

        return configuration;
    }

    /**
     * edits CRC (old settings of PEs, FUs and configs will be copied to new objects)
     * @param rows
     * @param columns
     * @param staticConfigLines
     * @param dynamicConfigLines
     * @param inputsNorth
     * @param inputsSouth
     * @param dataWidth
     */
    public void edit(int rows, int columns, int staticConfigLines, int dynamicConfigLines, boolean inputsNorth, boolean inputsSouth, int dataWidth) {


        // save current FU matrix in temp FU matrix
        ArrayList<ArrayList<FU>> tempFuMatrix = new ArrayList<>();

        for(int i = 0; i < this.rows; i++) {

            tempFuMatrix.add(new ArrayList<>());

            for(int j = 0; j < this.columns; j++) {
                tempFuMatrix.get(i).add(this.getFu(i,j));
            }
        }

        // generate new FU matrix with saved values
        fuMatrix = new ArrayList<>();

        for(int i = 0; i < rows; i++) {

            fuMatrix.add(new ArrayList<>());

            for(int j = 0; j < columns; j++) {

                if(i < this.rows && j < this.columns) {
                    fuMatrix.get(i).add(new FU(this, tempFuMatrix.get(i).get(j)));
                } else {
                    fuMatrix.get(i).add(new FU(this));
                }
            }
        }

        this.rows = rows;
        this.columns = columns;

        int oldStaticConfigLines = this.staticConfigLines;
        this.staticConfigLines = staticConfigLines;

        // save current static configs in temp static configs
        HashMap<Integer,Configuration> tempStaticConfigs = new HashMap<>();

        for(Map.Entry<Integer,Configuration> entry : staticConfigs.entrySet()) {
            tempStaticConfigs.put(entry.getKey(), entry.getValue());
        }

        staticConfigs = new HashMap<>();

        // generate new static configs with saved static configs
        for(int i = 0; i < this.staticConfigLines; i++) {
            if(i < oldStaticConfigLines) {
                staticConfigs.put(i, new Configuration(this, i, tempStaticConfigs.get(i)));

                // set outputs driven by south to none in PEs of last row
                for(int j = 0; j < columns; j++) {
                    staticConfigs.get(i).getPe(rows - 1, j).setOutputsDrivenBySouthInputsToNone();
                }

            } else {
                staticConfigs.put(i, new Configuration(this, i));
            }
        }


        int oldDynamicConfigLines = this.dynamicConfigLines;
        this.dynamicConfigLines = dynamicConfigLines;

        // save current static configs in temp dynamic configs
        HashMap<Integer, Configuration> tempDynamicConfigs = new HashMap<>();

        for(Map.Entry<Integer, Configuration> entry : dynamicConfigs.entrySet()) {
            tempDynamicConfigs.put(entry.getKey(), entry.getValue());
        }

        dynamicConfigs = new HashMap<>();

        // generate new dynamic configs with saved dynamic configs
        for(int i = 0; i < this.dynamicConfigLines; i++) {
            if(i < oldDynamicConfigLines) {
                dynamicConfigs.put(i, new Configuration(this, i, tempDynamicConfigs.get(i)));

                // south inputs of PEs in last row to none
                for(int j = 0; j < columns; j++) {
                    dynamicConfigs.get(i).getPe(rows - 1, j).setOutputsDrivenBySouthInputsToNone();
                }

            } else {
                dynamicConfigs.put(i, new Configuration(this, i));
            }
        }

        // if inputs in the north were disabled remove all internal PE connections which are coming from a north input
        if(this.inputsNorth && !inputsNorth) {

            // loop through static configs
            for(int i = 0; i < this.staticConfigLines; i++) {

                Configuration staticConfig = staticConfigs.get(i);
                // loop through northmost row PEs
                for(int column = 0; column < this.columns; column++) {
                    disableDriversFromNorth(staticConfig.getPe(0,column));
                }
            }

            // loop through dynamic configs
            for(int i = 0; i < this.dynamicConfigLines; i++) {
                Configuration dynamicConfig = dynamicConfigs.get(i);
                // loop through northmost row PEs
                for(int column = 0; column < this.columns; column++) {
                    disableDriversFromNorth(dynamicConfig.getPe(0,column));
                }
            }
        }

        // if inputs in the south were disabled remove all internal PE connections which are coming from a south input
        if(this.inputsSouth && !inputsSouth) {

            // loop through static configs
            for(int i = 0; i < this.staticConfigLines; i++) {

                Configuration staticConfig = staticConfigs.get(i);
                // loop through northmost row PEs
                for(int column = 0; column < this.columns; column++) {
                    disableDriversFromSouth(staticConfig.getPe(this.rows-1,column));
                }
            }

            // loop through dynamic configs
            for(int i = 0; i < this.dynamicConfigLines; i++) {
                Configuration dynamicConfig = dynamicConfigs.get(i);
                // loop through northmost row PEs
                for(int column = 0; column < this.columns; column++) {
                    disableDriversFromSouth(dynamicConfig.getPe(this.rows-1,column));
                }
            }
        }

        this.inputsNorth = inputsNorth;
        this.inputsSouth = inputsSouth;

        // if data width reduced changed truncate all const reg contents
        if(dataWidth < this.dataWidth) {
            // loop through static configs
            for(int i = 0; i < this.staticConfigLines; i++) {

                Configuration staticConfig = staticConfigs.get(i);

                for(int row = 0; row < this.rows; row++) {
                    for(int column = 0; column < this.columns; column++) {
                        truncateConstantRegisterDataContent(staticConfig.getPe(row, column), dataWidth);
                    }
                }
            }

            // loop through dynamic configs
            for(int i = 0; i < this.dynamicConfigLines; i++) {

                Configuration dynamicConfig = dynamicConfigs.get(i);

                for(int row = 0; row < this.rows; row++) {
                    for(int column = 0; column < this.columns; column++) {
                        truncateConstantRegisterDataContent(dynamicConfig.getPe(row, column), dataWidth);
                    }
                }
            }
        }

        this.dataWidth = dataWidth;

        this.notifyAllObservers();
    }

    private void disableDriversFromNorth(PE pe) {
        if(pe.getDataFlagInFu0() == PE.DataFlagInFuDriver.data_flag_in_N_0 || pe.getDataFlagInFu0() == PE.DataFlagInFuDriver.data_flag_in_N_1) {
            pe.setDataFlagInFu0(PE.DataFlagInFuDriver.none);
        }

        if(pe.getDataFlagInFu1() == PE.DataFlagInFuDriver.data_flag_in_N_0 || pe.getDataFlagInFu1() == PE.DataFlagInFuDriver.data_flag_in_N_1) {
            pe.setDataFlagInFu1(PE.DataFlagInFuDriver.none);
        }

        if(pe.getDataFlagInFuMux() == PE.DataFlagInFuDriver.data_flag_in_N_0 || pe.getDataFlagInFuMux() == PE.DataFlagInFuDriver.data_flag_in_N_1) {
            pe.setDataFlagInFuMux(PE.DataFlagInFuDriver.none);
        }

        if(pe.getDataFlagOutE0() == PE.DataFlagOutDriver.data_flag_in_N_0 || pe.getDataFlagOutE0() == PE.DataFlagOutDriver.data_flag_in_N_1) {
            pe.setDataFlagOutE0(PE.DataFlagOutDriver.none);
        }

        if(pe.getDataFlagOutE1() == PE.DataFlagOutDriver.data_flag_in_N_0 || pe.getDataFlagOutE1() == PE.DataFlagOutDriver.data_flag_in_N_1) {
            pe.setDataFlagOutE1(PE.DataFlagOutDriver.none);
        }

        if(pe.getDataFlagOutS0() == PE.DataFlagOutDriver.data_flag_in_N_0 || pe.getDataFlagOutS0() == PE.DataFlagOutDriver.data_flag_in_N_1) {
            pe.setDataFlagOutS0(PE.DataFlagOutDriver.none);
        }

        if(pe.getDataFlagOutS1() == PE.DataFlagOutDriver.data_flag_in_N_0 || pe.getDataFlagOutS1() == PE.DataFlagOutDriver.data_flag_in_N_1) {
            pe.setDataFlagOutS1(PE.DataFlagOutDriver.none);
        }
    }

    private void disableDriversFromSouth(PE pe) {
        if(pe.getDataFlagInFu0() == PE.DataFlagInFuDriver.data_flag_in_S_0 || pe.getDataFlagInFu0() == PE.DataFlagInFuDriver.data_flag_in_S_1) {
            pe.setDataFlagInFu0(PE.DataFlagInFuDriver.none);
        }

        if(pe.getDataFlagInFu1() == PE.DataFlagInFuDriver.data_flag_in_S_0 || pe.getDataFlagInFu1() == PE.DataFlagInFuDriver.data_flag_in_S_1) {
            pe.setDataFlagInFu1(PE.DataFlagInFuDriver.none);
        }

        if(pe.getDataFlagInFu1() == PE.DataFlagInFuDriver.data_flag_in_S_0 || pe.getDataFlagInFu1() == PE.DataFlagInFuDriver.data_flag_in_S_1) {
            pe.setDataFlagInFu1(PE.DataFlagInFuDriver.none);
        }

        if(pe.getDataFlagOutN0() == PE.DataFlagOutDriver.data_flag_in_S_0 || pe.getDataFlagOutN0() == PE.DataFlagOutDriver.data_flag_in_S_1) {
            pe.setDataFlagOutN0(PE.DataFlagOutDriver.none);
        }

        if(pe.getDataFlagOutN1() == PE.DataFlagOutDriver.data_flag_in_S_0 || pe.getDataFlagOutN1() == PE.DataFlagOutDriver.data_flag_in_S_1) {
            pe.setDataFlagOutN1(PE.DataFlagOutDriver.none);
        }

        if(pe.getDataFlagOutE0() == PE.DataFlagOutDriver.data_flag_in_S_0 || pe.getDataFlagOutE0() == PE.DataFlagOutDriver.data_flag_in_S_1) {
            pe.setDataFlagOutE0(PE.DataFlagOutDriver.none);
        }

        if(pe.getDataFlagOutE1() == PE.DataFlagOutDriver.data_flag_in_S_0 || pe.getDataFlagOutE1() == PE.DataFlagOutDriver.data_flag_in_S_1) {
            pe.setDataFlagOutE1(PE.DataFlagOutDriver.none);
        }
    }

    private void truncateConstantRegisterDataContent(PE pe, int dataWidth) {
        pe.setConstantRegisterDataContent(Truncator.truncateNumber(pe.getConstantRegisterDataContent(), dataWidth));
    }

    public void resetStaticConfig(int num) {
        Configuration staticConfig = this.staticConfigs.get(num);

        for(int row = 0; row < this.rows; row++) {
            for(int column = 0; column < this.columns; column++) {
                this.resetPe(staticConfig.getPe(row, column));
            }
        }

        this.notifyAllObservers();
    }

    public void resetDynamicConfig(int num) {
        Configuration dynamicConfig = this.dynamicConfigs.get(num);

        for(int row = 0; row < this.rows; row++) {
            for(int column = 0; column < this.columns; column++) {
                this.resetPe(dynamicConfig.getPe(row, column));
            }
        }

        this.notifyAllObservers();
    }

    private void resetPe(PE pe) {
        pe.setDataFlagInFu0(PE.DataFlagInFuDriver.none);
        pe.setDataFlagInFu1(PE.DataFlagInFuDriver.none);
        pe.setDataFlagInFuMux(PE.DataFlagInFuDriver.none);
        pe.setDataFlagOutN0(PE.DataFlagOutDriver.none);
        pe.setDataFlagOutN1(PE.DataFlagOutDriver.none);
        pe.setDataFlagOutE0(PE.DataFlagOutDriver.none);
        pe.setDataFlagOutE1(PE.DataFlagOutDriver.none);
        pe.setDataFlagOutS0(PE.DataFlagOutDriver.none);
        pe.setDataFlagOutS1(PE.DataFlagOutDriver.none);
        pe.setConstantRegisterDataContent(0);
        pe.setConstantRegisterFlagContent(false);
        pe.setFuFunction(FU.FuFunction.none);
    }

    private void setRows(int rows) {
        this.rows = (rows < 2) ? 2 : rows;
    }

    public int getRows() {
        return rows;
    }

    private void setColumns(int columns) {
        this.columns = (columns < 2) ? 2 : columns;
    }

    public int getColumns() {
        return columns;
    }

    private void setStaticConfigLines(int staticConfigLines) {
        this.staticConfigLines = (staticConfigLines < 0 ) ? 0 : staticConfigLines;
    }


    public int getStaticConfigLines() {
        return staticConfigLines;
    }

    private void setDynamicConfigLines(int dynamicConfigLines) {
        this.dynamicConfigLines = (dynamicConfigLines < 0) ? 0 : dynamicConfigLines;
    }

    public int getDynamicConfigLines() {
        return dynamicConfigLines;
    }

    public boolean areInputsNorth() {
        return inputsNorth;
    }

    public void setInputsNorth(boolean inputsNorth) {
        this.inputsNorth = inputsNorth;
    }

    public boolean areInputsSouth() {
        return inputsSouth;
    }

    public void setInputsSouth(boolean inputsSouth) {
        this.inputsSouth = inputsSouth;
    }

    public int getDataWidth() {
        return dataWidth;
    }

    public void setDataWidth(int dataWidth) {
        this.dataWidth = (dataWidth < 8) ? 8 : (dataWidth > 64) ? 64 : dataWidth;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public HashMap<Integer, Configuration> getStaticConfigs(){
        return staticConfigs;
    }

    public Configuration getStaticConfig(int number) {
        return staticConfigs.get(number);
    }

    public HashMap<Integer, Configuration> getDynamicConfigs() {
        return dynamicConfigs;
    }

    public Configuration getDynamicConfig(int number) {
        return dynamicConfigs.get(number);
    }

    public void notifyAllObservers() {
        this.model.notifyAllObservers();
    }

    /**
     * generates a Matrix (2D ArrayList) containing rows x columns FUs
     */
    private void generateFuMatrix() {

        fuMatrix = new ArrayList<>();

        for(int i = 0; i < rows; i++) {

            fuMatrix.add(new ArrayList<>());

            for(int j = 0; j < columns; j++) {
                fuMatrix.get(i).add(new FU(this));
            }
        }
    }

    /**
     * @param row
     * @param column
     * @return FU at position row,column
     */
    public FU getFu(int row, int column) {
        return fuMatrix.get(row).get(column);
    }

    public void setFuModes(int row, int column, LinkedHashMap<FU.FuMode, Boolean> fuFunctions) {
        fuMatrix.get(row).get(column).setAvailableModes(fuFunctions);

        // check if function was removed which is used by a config and if so set function in config to none
        // static configs
        for(Configuration staticConfig : staticConfigs.values()) {
            this.checkSetFuModesInPe(staticConfig.getPe(row, column), fuFunctions);
        }

        // dynamic configs
        for(Configuration dynamicConfig : dynamicConfigs.values()) {
            this.checkSetFuModesInPe(dynamicConfig.getPe(row, column), fuFunctions);
        }

    }

    public void setAllFuModes(LinkedHashMap<FU.FuMode, Boolean> fuModes) {
        for(int row = 0; row < this.rows; row++) {
           for(int column = 0; column < this.columns; column++) {
               this.setFuModes(row, column, fuModes);
           }
        }
    }

    public void setFuLut8BitContentHexString(int row, int column, String lut8BitContentHexString) {
        fuMatrix.get(row).get(column).setLut8BitContentHexString(lut8BitContentHexString);
    }

    public void setAllFuLut8BitContentHexString(String lut8BitContentHexString) {
        for(int row = 0; row < this.rows; row++) {
           for(int column = 0; column < this.columns; column++) {
               this.setFuLut8BitContentHexString(row, column, lut8BitContentHexString);
           }
        }
    }

    /**
     * if the hardware model of the CRC was edited (enable or disabling of a FU function) it will be checked if a
     * disabled FU functions is used in a PE and in set to nop if necessary
     * @param pe
     * @param availableFuModes
     */
    private void checkSetFuModesInPe(PE pe, LinkedHashMap<FU.FuMode, Boolean> availableFuModes) {
        if(pe.getFuFunction() != FU.FuFunction.none && !availableFuModes.get(FU.fuModeOfFuFunction.get(pe.getFuFunction()))) {
            pe.setFuFunction(FU.FuFunction.none);
        }
    }

    /**
     * JSON description of CRC
     * @return JSONObject containing a description of the CRC
     */
    public JSONObject toJSON() {
        JSONObject jsonCRCDescription = new JSONObject();
        //noinspection unchecked
        jsonCRCDescription.put("rows", rows);
        //noinspection unchecked
        jsonCRCDescription.put("columns", columns);
        //noinspection unchecked
        jsonCRCDescription.put("staticConfigLines", staticConfigLines);
        //noinspection unchecked
        jsonCRCDescription.put("dynamicConfigLines", dynamicConfigLines);

        jsonCRCDescription.put("inputsNorth", inputsNorth);
        jsonCRCDescription.put("inputsSouth", inputsSouth);

        jsonCRCDescription.put("dataWidth", dataWidth);

        //noinspection unchecked
        jsonCRCDescription.put("comment", comment);

        // process PEs
        JSONArray pes = new JSONArray();

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                JSONObject pe = new JSONObject();
                //noinspection unchecked
                pe.put("row", i);
                //noinspection unchecked
                pe.put("column", j);

                JSONArray availableFuModesJSON = new JSONArray();

                LinkedHashMap<FU.FuMode, Boolean> availableFuModes = this.getFu(i,j).getAvailableModes();

                for(Map.Entry<FU.FuMode, Boolean> availableFuMode : availableFuModes.entrySet()) {
                    if(availableFuMode.getValue()) {
                        availableFuModesJSON.add(FU.fuModeToName.get(availableFuMode.getKey()));
                    }
                }

                pe.put("availableFuModes", availableFuModesJSON);
                pe.put("lut8BitContentHexString", this.getFu(i,j).getLut8BitContentHexString());

                //noinspection unchecked
                pes.add(pe);
            }
        }

        //noinspection unchecked
        jsonCRCDescription.put("pes", pes);


        // process static configurations
        JSONArray staticConfigsJSON = new JSONArray();

        for(Configuration staticConfig : staticConfigs.values()) {

            JSONObject staticConfigJSON = new JSONObject();

            //noinspection unchecked
            staticConfigJSON.put("configNumber", staticConfig.getNumber());
            //noinspection unchecked
            staticConfigJSON.put("comment", staticConfig.getComment());
            //noinspection unchecked
            staticConfigJSON.put("pes", this.configPesToJSON(staticConfig));

            //noinspection unchecked
            staticConfigsJSON.add(staticConfigJSON);
        }

        //noinspection unchecked
        jsonCRCDescription.put("staticConfigs", staticConfigsJSON);


        // process dynamic configurations
        JSONArray dynamicConfigsJSON = new JSONArray();

        for(Configuration dynamicConfig : dynamicConfigs.values()) {

            JSONObject dynamicConfigJSON = new JSONObject();

            //noinspection unchecked
            dynamicConfigJSON.put("configNumber", dynamicConfig.getNumber());
            //noinspection unchecked
            dynamicConfigJSON.put("comment", dynamicConfig.getComment());
            //noinspection unchecked
            dynamicConfigJSON.put("pes", this.configPesToJSON(dynamicConfig));

            //noinspection unchecked
            dynamicConfigsJSON.add(dynamicConfigJSON);
        }

        //noinspection unchecked
        jsonCRCDescription.put("dynamicConfigs", dynamicConfigsJSON);

        return jsonCRCDescription;
    }

    /**
     * JSON description of PEs of a config
     * @param config
     * @return JSONArray containing a JSON description of all PEs of a config
     */
    private JSONArray configPesToJSON(Configuration config) {
        JSONArray configPes = new JSONArray();

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                PE pe = config.getPe(i,j);
                JSONObject configPe = new JSONObject();

                //noinspection unchecked
                configPe.put("row", i);
                //noinspection unchecked
                configPe.put("column", j);
                //noinspection unchecked
                configPe.put("dataFlagOutN0", pe.getDataFlagOutN0().toString());
                //noinspection unchecked
                configPe.put("dataFlagOutN1", pe.getDataFlagOutN1().toString());
                //noinspection unchecked
                configPe.put("dataFlagOutE0", pe.getDataFlagOutE0().toString());
                //noinspection unchecked
                configPe.put("dataFlagOutE1", pe.getDataFlagOutE1().toString());
                //noinspection unchecked
                configPe.put("dataFlagOutS0", pe.getDataFlagOutS0().toString());
                //noinspection unchecked
                configPe.put("dataFlagOutS1", pe.getDataFlagOutS1().toString());
                //noinspection unchecked
                configPe.put("dataFlagInFu0", pe.getDataFlagInFu0().toString());
                //noinspection unchecked
                configPe.put("dataFlagInFu1", pe.getDataFlagInFu1().toString());
                //noinspection unchecked
                configPe.put("dataFlagInFuMux", pe.getDataFlagInFuMux().toString());
                configPe.put("constRegDataContent", pe.getConstantRegisterDataContent());
                configPe.put("constRegFlagContent", pe.getConstantRegisterFlagContent());
                //noinspection unchecked
                configPe.put("fuFunction", pe.getFuFunction().toString());
                //noinspection unchecked
                configPe.put("fuSignedness", pe.isSignedData() ? "signed" : "unsigned");

                //noinspection unchecked
                configPes.add(configPe);
            }
        }

        return configPes;
    }

    /**
     * bits for enabled PE operations for all PEs
     * @return String bit 0/1
     */
    public String getPeOpParametersBits() {

        String bits = "";
        String peBits = "";

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                LinkedHashMap<FU.FuMode, Boolean> availableFuModes =  fuMatrix.get(i).get(j).getAvailableModes();

                peBits = "";

                peBits += (availableFuModes.get(FU.FuMode.add) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.sub) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.mul) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.div) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.and) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.or) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.xor) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.not) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.shift_left) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.shift_right) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.compare) ? "1" : "0");
                peBits += (availableFuModes.get(FU.FuMode.multiplex) ? "1" : "0");

                bits = peBits + bits;
            }
        }

        return bits;
    }

    /**
     * bits for all static configs
     * @return String bits
     */
    public String getStaticConfigParameterBits() {

        String bits = "";

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                bits = this.getPeStaticConfigParameterBits(i,j) + bits;
            }
        }


        return bits;
    }

    /**
     * bits for all static configs of a PE
     * @param row
     * @param column
     * @return String bits
     */
    public String getPeStaticConfigParameterBits(int row, int column) {

        String bits = "";

        // TODO: sort by key
        for(Map.Entry<Integer, Configuration> entry : staticConfigs.entrySet()) {
            bits = this.getPeStaticConfigParameterBits(row,column,entry.getKey()) + bits;
        }

        return bits;
    }

    public String getPeStaticConstRegContentBits(int row, int column) {

        String bits = "";

        for(Map.Entry<Integer, Configuration> entry : staticConfigs.entrySet()) {
            bits = this.getPeStaticConstRegContentBits(row, column, entry.getKey()) + bits;
        }


        return bits;
    }

    /**
     * bits for a static config of a PE
     * @param row
     * @param column
     * @param configNumber
     * @return String bits
     */
    private String getPeStaticConfigParameterBits(int row, int column, int configNumber) {

        String bits = "";

        PE pe = staticConfigs.get(configNumber).getPe(row, column);

        bits += (pe.isActive() ? "1" : "0");
        bits += (pe.isSignedData() ? "1" : "0");
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutS1());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutS0());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutE1());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutE0());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutN1());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutN0());

        bits += PE.dataFlagInFuDriverToBits.get(pe.getDataFlagInFuMux());
        bits += PE.dataFlagInFuDriverToBits.get(pe.getDataFlagInFu1());
        bits += PE.dataFlagInFuDriverToBits.get(pe.getDataFlagInFu0());

        bits += FU.getFuFunctionToBits.get(pe.getFuFunction());

        return bits;
    }

    private String getPeStaticConstRegContentBits(int row, int column, int configNumber) {

        PE pe = staticConfigs.get(configNumber).getPe(row, column);

        String bits = Long.toBinaryString(pe.getConstantRegisterDataContent());

        // add leading zeros
        while(bits.length() < dataWidth) {
            bits = "0" + bits;
        }

        // add flag
        bits = (pe.getConstantRegisterFlagContent() ? "1" : "0") + bits;

        return bits;
    }

    /**
     * bits for a dynamic config of a PE
     * @param row
     * @param column
     * @param configNumber
     * @return String bits
     */
    public String getPeDynamicConfigParameterBits(int row, int column, int configNumber) {

        String bits = "";

        PE pe = dynamicConfigs.get(configNumber).getPe(row, column);

        bits += (pe.isActive() ? "1" : "0");
        bits += (pe.isSignedData() ? "1" : "0");
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutS1());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutS0());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutE1());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutE0());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutN1());
        bits += PE.dataFlagOutDriverToBits.get(pe.getDataFlagOutN0());

        bits += PE.dataFlagInFuDriverToBits.get(pe.getDataFlagInFuMux());
        bits += PE.dataFlagInFuDriverToBits.get(pe.getDataFlagInFu1());
        bits += PE.dataFlagInFuDriverToBits.get(pe.getDataFlagInFu0());

        bits += FU.getFuFunctionToBits.get(pe.getFuFunction());

        return bits;
    }

    public String getPeDynamicConstRegContentBits(int row, int column, int configNumber) {

        PE pe = dynamicConfigs.get(configNumber).getPe(row, column);

        String bits = Long.toBinaryString(pe.getConstantRegisterDataContent());

        // add leading zeros
        while(bits.length() < dataWidth) {
            bits = "0" + bits;
        }

        return bits;
    }
}
