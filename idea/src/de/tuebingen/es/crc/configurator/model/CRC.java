package de.tuebingen.es.crc.configurator.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class CRC {

    private Model model;

    private int rows;
    private int columns;
    private int staticConfigLines;
    private int dynamicConfigLines;

    private HashMap<Integer, Configuration> staticConfigs;
    private HashMap<Integer, Configuration> dynamicConfigs;

    private ArrayList<ArrayList<FU>> fuMatrix;

    public CRC(Model model) {
        this.model = model;
        rows = 2;
        columns = 2;
        staticConfigLines = 0;
        dynamicConfigLines = 0;
    }

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

        Iterator<JSONObject> peIterator = pes.iterator();

        // set functions in all FUs
        while(peIterator.hasNext()) {
            JSONObject pe = peIterator.next();
            FU fu = getFu(Integer.parseInt(pe.get("row").toString()), Integer.parseInt(pe.get("column").toString()));
            JSONArray fuFunctions = (JSONArray) pe.get("FUFunctions");

            Iterator<String> fuFunctionIterator = fuFunctions.iterator();

            while(fuFunctionIterator.hasNext()) {
                fu.setFunction(fuFunctionIterator.next(), true);
            }
        }

        // generate static configs
        JSONArray staticConfigs = (JSONArray) jsonCrcDescription.get("staticConfigs");

        if(staticConfigs == null) {
            throw new Exception("CRC description file does not contain 'staticConfigs'!");
        }

        Iterator<JSONObject> staticConfigsIterator = staticConfigs.iterator();

        while(staticConfigsIterator.hasNext()) {
            JSONObject staticConfig = staticConfigsIterator.next();

            Configuration configuration = this.readConfigFromJSON(staticConfig);

            this.staticConfigs.put(configuration.getNumber(), configuration);
        }

        // generate dynamic configs
        JSONArray dynamicConfigs = (JSONArray) jsonCrcDescription.get("dynamicConfigs");

        if(dynamicConfigs == null) {
            throw new Exception("CRC description file does not contain 'dynamicConfigs'!");
        }

        Iterator<JSONObject> dynamicConfigsIterator = dynamicConfigs.iterator();

        while(dynamicConfigsIterator.hasNext()) {
            JSONObject dynamicConfig = dynamicConfigsIterator.next();

            Configuration configuration = this.readConfigFromJSON(dynamicConfig);

            this.dynamicConfigs.put(configuration.getNumber(), configuration);
        }

    }

    public Configuration readConfigFromJSON(JSONObject config) throws Exception {

        // read PEs
        JSONArray pes = (JSONArray) config.get("PEs");

        if(pes == null) {
            throw new Exception("CRC description file section staticConfigs does not contain 'PEs'!");
        }

        Configuration configuration = new Configuration(this, Integer.parseInt(config.get("configNumber").toString()));
        Iterator<JSONObject> peIterator = pes.iterator();

        while(peIterator.hasNext()) {
            JSONObject peJson = peIterator.next();

            PE pe = configuration.getPE(Integer.parseInt(peJson.get("row").toString()), Integer.parseInt(peJson.get("column").toString()));

            pe.setDataFlagOutN0(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutN0").toString()));
            pe.setDataFlagOutN1(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutN1").toString()));
            pe.setDataFlagOutE0(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutE0").toString()));
            pe.setDataFlagOutE1(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutE1").toString()));
            pe.setDataFlagOutS0(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutS0").toString()));
            pe.setDataFlagOutS1(PE.DataFlagOutDriver.valueOf(peJson.get("dataFlagOutS1").toString()));
            pe.setDataFlagInFU0(PE.DataFlagInFuDriver.valueOf(peJson.get("dataFlagInFU0").toString()));
            pe.setDataFlagInFU1(PE.DataFlagInFuDriver.valueOf(peJson.get("dataFlagInFU1").toString()));
            pe.setFlagInFUMux(PE.DataFlagInFuDriver.valueOf(peJson.get("flagInFUMux").toString()));
            pe.setFUFunction(PE.FUFunction.valueOf(peJson.get("FUFunction").toString()));
        }

        return configuration;
    }

    public void editCrc(int rows, int columns, int staticConfigLines, int dynamicConfigLines) {

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
                    staticConfigs.get(i).getPE(rows - 1, j).setOutputsDrivenBySouthInputsToNone();
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
                    dynamicConfigs.get(i).getPE(rows - 1, j).setOutputsDrivenBySouthInputsToNone();
                }

            } else {
                dynamicConfigs.put(i, new Configuration(this, i));
            }
        }

        this.notifyAllObservers();
    }

    public void setRows(int rows) {
        this.rows = (rows < 2) ? 2 : rows;
    }

    public int getRows() {
        return rows;
    }

    public void setColumns(int columns) {
        this.columns = (columns < 2) ? 2 : columns;
    }

    public int getColumns() {
        return columns;
    }

    public void setStaticConfigLines(int staticConfigLines) {
        this.staticConfigLines = (staticConfigLines < 0 ) ? 0 : staticConfigLines;
    }


    public int getStaticConfigLines() {
        return staticConfigLines;
    }

    public void setDynamicConfigLines(int dynamicConfigLines) {
        this.dynamicConfigLines = (dynamicConfigLines < 0) ? 0 : dynamicConfigLines;
    }

    public int getDynamicConfigLines() {
        return dynamicConfigLines;
    }

    public HashMap<Integer, Configuration> getStaticConfigurations(){
        return staticConfigs;
    }

    public Configuration getStaticConfiguration(int number) {
        return staticConfigs.get(number);
    }

    public HashMap<Integer, Configuration> getDynamicConfigurations() {
        return dynamicConfigs;
    }

    public Configuration getDynamicConfiguration(int number) {
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
            this.checkSetFuFunctionInPe(staticConfig.getPE(row, column), fuFunctions);
        }

        // dynamic configs
        for(Configuration dynamicConfig : dynamicConfigs.values()) {
            this.checkSetFuFunctionInPe(dynamicConfig.getPE(row, column), fuFunctions);
        }

    }

    public void checkSetFuFunctionInPe(PE pe, LinkedHashMap<String, Boolean> fuFunctions) {
        for(Map.Entry<String, Boolean> entry : fuFunctions.entrySet()) {

            if(!entry.getValue()) {

                if (entry.getKey() != "compare" && entry.getKey() != "multiplex") {

                    if(pe.getFUFunction() == PE.FUFunction.valueOf(entry.getKey())) {
                        pe.setFUFunction(PE.FUFunction.none);
                    }


                } else if (entry.getKey() == "compare") {

                    if(
                            pe.getFUFunction() == PE.FUFunction.compare_eq ||
                            pe.getFUFunction() == PE.FUFunction.compare_neq ||
                            pe.getFUFunction() == PE.FUFunction.compare_lt ||
                            pe.getFUFunction() == PE.FUFunction.compare_gt ||
                            pe.getFUFunction() == PE.FUFunction.compare_leq ||
                            pe.getFUFunction() == PE.FUFunction.compare_geq) {
                        pe.setFUFunction(PE.FUFunction.none);
                    }


                } else if (entry.getKey() == "multiplex") {

                    if(pe.getFUFunction() == PE.FUFunction.mux_0 || pe.getFUFunction() == PE.FUFunction.mux_1) {
                        pe.setFUFunction(PE.FUFunction.none);
                    }
                }
            }
        }
    }

    /**
     * @return JSONObject containing a description of the CRC
     */
    public JSONObject toJSON() {
        JSONObject jsonCRCDescription = new JSONObject();
        jsonCRCDescription.put("rows", rows);
        jsonCRCDescription.put("columns", columns);
        jsonCRCDescription.put("staticConfigLines", staticConfigLines);
        jsonCRCDescription.put("dynamicConfigLines", dynamicConfigLines);

        // process PEs
        JSONArray pes = new JSONArray();

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                JSONObject pe = new JSONObject();
                pe.put("row", i);
                pe.put("column", j);

                JSONArray fuFunctions = new JSONArray();

                LinkedHashMap<String, Boolean> fuFunctionsMap = this.getFu(i,j).getFunctions();

                for(Map.Entry<String, Boolean> function : fuFunctionsMap.entrySet()) {
                    if(function.getValue()) {
                        fuFunctions.add(function.getKey());
                    }
                }

                pe.put("FUFunctions", fuFunctions);
                pes.add(pe);
            }
        }

        jsonCRCDescription.put("PEs", pes);


        // process static configurations
        JSONArray staticConfigsJSON = new JSONArray();

        for(Configuration staticConfig : staticConfigs.values()) {

            JSONObject staticConfigJSON = new JSONObject();

            staticConfigJSON.put("configNumber", staticConfig.getNumber());
            staticConfigJSON.put("PEs", this.configPesToJSON(staticConfig));

            staticConfigsJSON.add(staticConfigJSON);
        }

        jsonCRCDescription.put("staticConfigs", staticConfigsJSON);


        // process dynamic configurations
        JSONArray dynamicConfigsJSON = new JSONArray();

        for(Configuration dynamicConfig : dynamicConfigs.values()) {

            JSONObject dynamicConfigJSON = new JSONObject();

            dynamicConfigJSON.put("configNumber", dynamicConfig.getNumber());
            dynamicConfigJSON.put("PEs", this.configPesToJSON(dynamicConfig));

            dynamicConfigsJSON.add(dynamicConfigJSON);
        }

        jsonCRCDescription.put("dynamicConfigs", dynamicConfigsJSON);

        return jsonCRCDescription;
    }

    private JSONArray configPesToJSON(Configuration config) {
        JSONArray configPes = new JSONArray();

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                PE pe = config.getPE(i,j);
                JSONObject configPe = new JSONObject();

                configPe.put("row", i);
                configPe.put("column", j);
                configPe.put("dataFlagOutN0", pe.getDataFlagOutN0().toString());
                configPe.put("dataFlagOutN1", pe.getDataFlagOutN1().toString());
                configPe.put("dataFlagOutE0", pe.getDataFlagOutE0().toString());
                configPe.put("dataFlagOutE1", pe.getDataFlagOutE1().toString());
                configPe.put("dataFlagOutS0", pe.getDataFlagOutS0().toString());
                configPe.put("dataFlagOutS1", pe.getDataFlagOutS1().toString());
                configPe.put("dataFlagInFU0", pe.getDataFlagInFU0().toString());
                configPe.put("dataFlagInFU1", pe.getDataFlagInFU1().toString());
                configPe.put("flagInFUMux", pe.getFlagInFUMux().toString());
                configPe.put("FUFunction", pe.getFUFunction().toString());

                configPes.add(configPe);
            }
        }

        return configPes;
    }
}
