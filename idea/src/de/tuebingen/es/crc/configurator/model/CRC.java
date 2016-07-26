package de.tuebingen.es.crc.configurator.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by Konstantin (Konze) Lübeck on 25/07/16.
 */
public class CRC {
    private int rows;
    private int columns;
    private int staticConfigLines;
    private int dynamicConfigLines;

    private ArrayList<ArrayList<FU>> fuMatrix;

    public CRC() {
        this.rows = 2;
        this.columns = 2;
        this.staticConfigLines = 0;
        this.dynamicConfigLines = 0;
    }

    /*
    public CRC(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.staticConfigLines = 0;
        this.dynamicConfigLines = 0;
    }

    public CRC(int rows, int columns, int staticConfigLines, int dynamicConfigLines) {
        this.rows = rows;
        this.columns = columns;
        this.staticConfigLines = staticConfigLines;
        this.dynamicConfigLines = dynamicConfigLines;
    }
    */

    /**
     * generates a CRC object with properties from a CRC description file
     * @param jsonCrcDescription
     * @throws Exception
     */
    public CRC(JSONObject jsonCrcDescription) throws Exception {

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

    private void generateFuMatrix() {

        fuMatrix = new ArrayList<>();

        for(int i = 0; i < this.rows; i++) {

            fuMatrix.add(new ArrayList<>());

            for(int j = 0; j < this.columns; j++) {
                fuMatrix.get(i).add(new FU());
            }
        }
    }

    public FU getFu(int row, int column) {
        return this.fuMatrix.get(row).get(column);
    }
}
