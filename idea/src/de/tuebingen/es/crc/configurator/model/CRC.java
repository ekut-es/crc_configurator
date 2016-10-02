package de.tuebingen.es.crc.configurator.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class CRC {

    private final Model model;

    private int rows;
    private int columns;
    private int staticConfigLines;
    private int dynamicConfigLines;

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
     * @param model
     */
    public CRC(int rows, int columns, int staticConfigLines, int dynamicConfigLines, Model model) {
        this.model = model;
        this.setRows(rows);
        this.setColumns(columns);
        this.setStaticConfigLines(staticConfigLines);
        this.setDynamicConfigLines(dynamicConfigLines);

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

        this.generateFuMatrix();

        this.staticConfigs = new HashMap<>();
        this.dynamicConfigs = new HashMap<>();

        JSONArray pes = (JSONArray) jsonCrcDescription.get("PEs");

        if(pes == null) {
            throw new Exception("CRC description file does not contain 'PEs'!");
        }

        // set functions in all FUs
        //noinspection unchecked
        for (JSONObject pe : (Iterable<JSONObject>) pes) {
            FU fu = getFu(Integer.parseInt(pe.get("row").toString()), Integer.parseInt(pe.get("column").toString()));
            JSONArray fuFunctions = (JSONArray) pe.get("FUFunctions");

            //noinspection unchecked
            for (String fuFunction : (Iterable<String>) fuFunctions) {
                fu.setFunction(fuFunction, true);
            }
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
        JSONArray pes = (JSONArray) config.get("PEs");

        if(pes == null) {
            throw new Exception("CRC description file section staticConfigs does not contain 'PEs'!");
        }

        Configuration configuration = new Configuration(this, Integer.parseInt(config.get("configNumber").toString()));

        //noinspection unchecked
        for (JSONObject peJson : (Iterable<JSONObject>) pes) {
            PE pe = configuration.getPe(Integer.parseInt(peJson.get("row").toString()), Integer.parseInt(peJson.get("column").toString()));

            pe.setDataFlagOutN0(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutN0").toString()));
            pe.setDataFlagOutN1(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutN1").toString()));
            pe.setDataFlagOutE0(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutE0").toString()));
            pe.setDataFlagOutE1(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutE1").toString()));
            pe.setDataFlagOutS0(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutS0").toString()));
            pe.setDataFlagOutS1(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutS1").toString()));
            pe.setDataFlagInFu0(PE.DataFlagInFuDriver.valueOf(peJson.get("dataFlagInFU0").toString()));
            pe.setDataFlagInFu1(PE.DataFlagInFuDriver.valueOf(peJson.get("dataFlagInFU1").toString()));
            pe.setFlagInFuMux(PE.DataFlagInFuDriver.valueOf(peJson.get("flagInFUMux").toString()));
            pe.setFuFunction(PE.FUFunction.valueOf(peJson.get("FUFunction").toString()));
            pe.setSignedData(peJson.get("FUSignedness").toString().equals("signed"));
        }

        return configuration;
    }

    /**
     * edits CRC (old settings of PEs, FUs and configs will be copied to new objects)
     * @param rows
     * @param columns
     * @param staticConfigLines
     * @param dynamicConfigLines
     */
    public void edit(int rows, int columns, int staticConfigLines, int dynamicConfigLines) {

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

        this.notifyAllObservers();
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

    public void setFuFunctions(int row, int column, LinkedHashMap<String, Boolean> fuFunctions) {
        fuMatrix.get(row).get(column).setFunctions(fuFunctions);

        // check if function was removed which is used by a config and if so set function in config to none
        // static configs
        for(Configuration staticConfig : staticConfigs.values()) {
            this.checkSetFuFunctionInPe(staticConfig.getPe(row, column), fuFunctions);
        }

        // dynamic configs
        for(Configuration dynamicConfig : dynamicConfigs.values()) {
            this.checkSetFuFunctionInPe(dynamicConfig.getPe(row, column), fuFunctions);
        }

    }

    public void setFuSignedness(int row, int column, boolean fuSingedness) {

    }

    /**
     * if the hardware model of the CRC was edited (enable or disabling of a FU function) it will be checked if a
     * disabled FU functions is used in a PE and in set to NOP if necessary
     * @param pe
     * @param fuFunctions
     */
    private void checkSetFuFunctionInPe(PE pe, LinkedHashMap<String, Boolean> fuFunctions) {
        fuFunctions.entrySet().stream().filter(entry -> !entry.getValue()).forEachOrdered(entry -> {

            if (!Objects.equals(entry.getKey(), "compare") && !Objects.equals(entry.getKey(), "multiplex")) {

                if (pe.getFuFunction() == PE.FUFunction.valueOf(entry.getKey())) {
                    pe.setFuFunction(PE.FUFunction.none);
                }


            } else if (Objects.equals(entry.getKey(), "compare")) {

                if (
                        pe.getFuFunction() == PE.FUFunction.compare_eq ||
                                pe.getFuFunction() == PE.FUFunction.compare_neq ||
                                pe.getFuFunction() == PE.FUFunction.compare_lt ||
                                pe.getFuFunction() == PE.FUFunction.compare_gt ||
                                pe.getFuFunction() == PE.FUFunction.compare_leq ||
                                pe.getFuFunction() == PE.FUFunction.compare_geq) {
                    pe.setFuFunction(PE.FUFunction.none);
                }


            } else if (Objects.equals(entry.getKey(), "multiplex")) {

                if (pe.getFuFunction() == PE.FUFunction.mux_0 || pe.getFuFunction() == PE.FUFunction.mux_1) {
                    pe.setFuFunction(PE.FUFunction.none);
                }
            }
        });
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

        // process PEs
        JSONArray pes = new JSONArray();

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                JSONObject pe = new JSONObject();
                //noinspection unchecked
                pe.put("row", i);
                //noinspection unchecked
                pe.put("column", j);

                JSONArray fuFunctions = new JSONArray();

                LinkedHashMap<String, Boolean> fuFunctionsMap = this.getFu(i,j).getFunctions();

                //noinspection unchecked
                fuFunctions.addAll(fuFunctionsMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList()));

                //noinspection unchecked
                pe.put("FUFunctions", fuFunctions);
                //noinspection unchecked
                pes.add(pe);
            }
        }

        //noinspection unchecked
        jsonCRCDescription.put("PEs", pes);


        // process static configurations
        JSONArray staticConfigsJSON = new JSONArray();

        for(Configuration staticConfig : staticConfigs.values()) {

            JSONObject staticConfigJSON = new JSONObject();

            //noinspection unchecked
            staticConfigJSON.put("configNumber", staticConfig.getNumber());
            //noinspection unchecked
            staticConfigJSON.put("PEs", this.configPesToJSON(staticConfig));

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
            dynamicConfigJSON.put("PEs", this.configPesToJSON(dynamicConfig));

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
                configPe.put("dataFlagInFU0", pe.getDataFlagInFu0().toString());
                //noinspection unchecked
                configPe.put("dataFlagInFU1", pe.getDataFlagInFu1().toString());
                //noinspection unchecked
                configPe.put("flagInFUMux", pe.getFlagInFuMux().toString());
                //noinspection unchecked
                configPe.put("FUFunction", pe.getFuFunction().toString());
                //noinspection unchecked
                configPe.put("FUSignedness", pe.isSignedData() ? "signed" : "unsigned");

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

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                LinkedHashMap<String, Boolean> fuFunctions =  fuMatrix.get(i).get(j).getFunctions();

                bits += (fuFunctions.get("add") ? "1" : "0");
                bits += (fuFunctions.get("sub") ? "1" : "0");
                bits += (fuFunctions.get("mul") ? "1" : "0");
                bits += (fuFunctions.get("div") ? "1" : "0");
                bits += (fuFunctions.get("and") ? "1" : "0");
                bits += (fuFunctions.get("or") ? "1" : "0");
                bits += (fuFunctions.get("xor") ? "1" : "0");
                bits += (fuFunctions.get("not") ? "1" : "0");
                bits += (fuFunctions.get("shift_left") ? "1" : "0");
                bits += (fuFunctions.get("shift_right") ? "1" : "0");
                bits += (fuFunctions.get("compare") ? "1" : "0");
                bits += (fuFunctions.get("multiplex") ? "1" : "0");
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
                bits += this.getPeStaticConfigParameterBits(i,j);
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
            bits += this.getPeStaticConfigParameterBits(row,column,entry.getKey());
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
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutS1());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutS0());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutE0());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutE1());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutN1());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutN0());

        bits += PeDataFlagInFuDriverBitsMap.getBits(pe.getFlagInFuMux());
        bits += PeDataFlagInFuDriverBitsMap.getBits(pe.getDataFlagInFu1());
        bits += PeDataFlagInFuDriverBitsMap.getBits(pe.getDataFlagInFu0());

        bits += PeFuFunctionBitsMap.getBits(pe.getFuFunction());

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
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutS1());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutS0());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutE0());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutE1());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutN1());
        bits += PeDataFlagOutDriverBitsMap.getBits(pe.getDataFlagOutN0());

        bits += PeDataFlagInFuDriverBitsMap.getBits(pe.getFlagInFuMux());
        bits += PeDataFlagInFuDriverBitsMap.getBits(pe.getDataFlagInFu1());
        bits += PeDataFlagInFuDriverBitsMap.getBits(pe.getDataFlagInFu0());

        bits += PeFuFunctionBitsMap.getBits(pe.getFuFunction());

        return bits;
    }
}
