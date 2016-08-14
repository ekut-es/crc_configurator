package de.tuebingen.es.crc.configurator.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class CRC {

    private Model model;

    private int rows;
    private int columns;
    private int staticConfigLines;
    private int dynamicConfigLines;

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

        // TODO: read static and dynamic configurations
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

    /**
     * @return JSONObject containing a description of the CRC
     */
    public JSONObject toJSON() {
        JSONObject jsonCRCDescription = new JSONObject();
        jsonCRCDescription.put("rows", rows);
        jsonCRCDescription.put("columns", columns);
        jsonCRCDescription.put("staticConfigLines", staticConfigLines);
        jsonCRCDescription.put("dynamicConfigLines", dynamicConfigLines);

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

        JSONArray staticConfigs = new JSONArray();
        JSONArray dynamicConfigs = new JSONArray();

        jsonCRCDescription.put("staticConfigs", staticConfigs);
        jsonCRCDescription.put("dynamicConfigs", dynamicConfigs);

        return jsonCRCDescription;
    }
}
