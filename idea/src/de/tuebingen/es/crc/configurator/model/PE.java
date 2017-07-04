package de.tuebingen.es.crc.configurator.model;

import java.util.HashMap;

/**
 * Created by Konstantin (Konze) Lübeck on 22/08/16.
 */
public class PE {

    private final Configuration configuration;

    public enum DataFlagOutDriver {
        none, data_flag_in_N_0, data_flag_in_N_1, data_flag_in_S_0, data_flag_in_S_1, data_flag_in_W_0, data_flag_in_W_1, data_flag_out_FU
    }

    public enum DataFlagInFuDriver {
        none, data_flag_in_N_0, data_flag_in_N_1, data_flag_in_S_0, data_flag_in_S_1, data_flag_in_W_0, data_flag_in_W_1, const_reg
    }

    public static final HashMap<DataFlagInFuDriver, String> dataFlagInFuDriverToBits = new HashMap<DataFlagInFuDriver, String>() {{
        put(DataFlagInFuDriver.none, "111");
        put(DataFlagInFuDriver.data_flag_in_N_0, "000");
        put(DataFlagInFuDriver.data_flag_in_N_1, "001");
        put(DataFlagInFuDriver.data_flag_in_S_0, "010");
        put(DataFlagInFuDriver.data_flag_in_S_1, "011");
        put(DataFlagInFuDriver.data_flag_in_W_0, "100");
        put(DataFlagInFuDriver.data_flag_in_W_1, "101");
        put(DataFlagInFuDriver.const_reg, "110");
    }};

    public static final HashMap<DataFlagOutDriver, String> dataFlagOutDriverToBits = new HashMap<DataFlagOutDriver, String>() {{
        put(DataFlagOutDriver.none, "111");
        put(DataFlagOutDriver.data_flag_in_N_0, "000");
        put(DataFlagOutDriver.data_flag_in_N_1, "001");
        put(DataFlagOutDriver.data_flag_in_S_0, "010");
        put(DataFlagOutDriver.data_flag_in_S_1, "011");
        put(DataFlagOutDriver.data_flag_in_W_0, "100");
        put(DataFlagOutDriver.data_flag_in_W_1, "101");
        put(DataFlagOutDriver.data_flag_out_FU, "110");
    }};

    private boolean active;

    private boolean signedData;

    private DataFlagOutDriver dataFlagOutN0;
    private DataFlagOutDriver dataFlagOutN1;
    private DataFlagOutDriver dataFlagOutE0;
    private DataFlagOutDriver dataFlagOutE1;
    private DataFlagOutDriver dataFlagOutS0;
    private DataFlagOutDriver dataFlagOutS1;
    private DataFlagInFuDriver dataFlagInFU0;
    private DataFlagInFuDriver dataFlagInFU1;
    private DataFlagInFuDriver dataFlagInFUMux;
    private FU.FuFunction fuFunction;

    private long constantRegContent;

    public PE(Configuration configuration) {
        this.configuration = configuration;
        active = false;
        signedData = false;
        dataFlagOutN0 = DataFlagOutDriver.none;
        dataFlagOutN1 = DataFlagOutDriver.none;
        dataFlagOutE0 = DataFlagOutDriver.none;
        dataFlagOutE1 = DataFlagOutDriver.none;
        dataFlagOutS0 = DataFlagOutDriver.none;
        dataFlagOutS1 = DataFlagOutDriver.none;
        dataFlagInFU0 = DataFlagInFuDriver.none;
        dataFlagInFU1 = DataFlagInFuDriver.none;
        dataFlagInFUMux = DataFlagInFuDriver.none;
        constantRegContent = 0;
        fuFunction = FU.FuFunction.none;
    }

    /**
     * copies content of another PE
     * @param pe
     */
    public void copy(PE pe) {
        this.signedData = pe.isSignedData();
        this.dataFlagOutN0 = pe.getDataFlagOutN0();
        this.dataFlagOutN1 = pe.getDataFlagOutN1();
        this.dataFlagOutE0 = pe.getDataFlagOutE0();
        this.dataFlagOutE1 = pe.getDataFlagOutE1();
        this.dataFlagOutS0 = pe.getDataFlagOutS1();
        this.dataFlagInFU0 = pe.getDataFlagInFu0();
        this.dataFlagInFU1 = pe.getDataFlagInFu1();
        this.dataFlagInFUMux = pe.getDataFlagInFuMux();
        this.constantRegContent = pe.getConstantRegContent();
        this.fuFunction = pe.getFuFunction();

        this.checkSetActive();
    }

    public boolean isActive() {
        return active;
    }

    public boolean isSignedData() {
        return signedData;
    }

    public void setSignedData(boolean signedData) {
        this.signedData = signedData;
        configuration.notifyAllObservers();
    }

    public DataFlagOutDriver getDataFlagOutN0() {
        return dataFlagOutN0;
    }

    public void setDataFlagOutN0(DataFlagOutDriver dataFlagOutN0) {
        this.dataFlagOutN0 = dataFlagOutN0;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public DataFlagOutDriver getDataFlagOutN1() {
        return dataFlagOutN1;
    }

    public void setDataFlagOutN1(DataFlagOutDriver dataFlagOutN1) {
        this.dataFlagOutN1 = dataFlagOutN1;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public DataFlagOutDriver getDataFlagOutE0() {
        return dataFlagOutE0;
    }

    public void setDataFlagOutE0(DataFlagOutDriver dataFlagOutE0) {
        this.dataFlagOutE0 = dataFlagOutE0;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public DataFlagOutDriver getDataFlagOutE1() {
        return dataFlagOutE1;
    }

    public void setDataFlagOutE1(DataFlagOutDriver dataFlagOutE1) {
        this.dataFlagOutE1 = dataFlagOutE1;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public DataFlagOutDriver getDataFlagOutS0() {
        return dataFlagOutS0;
    }

    public void setDataFlagOutS0(DataFlagOutDriver dataFlagOutS0) {
        this.dataFlagOutS0 = dataFlagOutS0;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public DataFlagOutDriver getDataFlagOutS1() {
        return dataFlagOutS1;
    }

    public void setDataFlagOutS1(DataFlagOutDriver dataFlagOutS1) {
        this.dataFlagOutS1 = dataFlagOutS1;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public DataFlagInFuDriver getDataFlagInFu0() {
        return dataFlagInFU0;
    }

    public void setDataFlagInFu0(DataFlagInFuDriver dataFlagInFU0) {
        this.dataFlagInFU0 = dataFlagInFU0;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public DataFlagInFuDriver getDataFlagInFu1() {
        return dataFlagInFU1;
    }

    public void setDataFlagInFu1(DataFlagInFuDriver dataFlagInFU1) {
        this.dataFlagInFU1 = dataFlagInFU1;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public DataFlagInFuDriver getDataFlagInFuMux() {
        return dataFlagInFUMux;
    }

    public void setDataFlagInFuMux(DataFlagInFuDriver dataFlagInFUMux) {
        this.dataFlagInFUMux = dataFlagInFUMux;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public long getConstantRegContent() {
        return constantRegContent;
    }

    public void setConstantRegContent(long constantRegContent) {
        this.constantRegContent = constantRegContent;
        configuration.notifyAllObservers();
    }

    public FU.FuFunction getFuFunction() {
        return fuFunction;
    }

    public void setFuFunction(FU.FuFunction fuFunction) {
        this.fuFunction = fuFunction;
        configuration.notifyAllObservers();
    }



    /**
     * checks if the PE is active
     */
    private void checkSetActive()  {
        active = !(dataFlagOutN0 == DataFlagOutDriver.none &&
                dataFlagOutN1 == DataFlagOutDriver.none &&
                dataFlagOutE0 == DataFlagOutDriver.none &&
                dataFlagOutE1 == DataFlagOutDriver.none &&
                dataFlagOutS0 == DataFlagOutDriver.none &&
                dataFlagOutS1 == DataFlagOutDriver.none &&
                dataFlagInFU0 == DataFlagInFuDriver.none &&
                dataFlagInFU1 == DataFlagInFuDriver.none &&
                dataFlagInFUMux == DataFlagInFuDriver.none);
    }

    /**
     * set all outputs driven by south inputs to none
     */
    public void setOutputsDrivenBySouthInputsToNone() {

        if(dataFlagOutN0 == DataFlagOutDriver.data_flag_in_S_0 || dataFlagOutN0 == DataFlagOutDriver.data_flag_in_S_1) {
            dataFlagOutN0 = DataFlagOutDriver.none;
        }

        if(dataFlagOutN1 == DataFlagOutDriver.data_flag_in_S_0 || dataFlagOutN1 == DataFlagOutDriver.data_flag_in_S_1) {
            dataFlagOutN1 = DataFlagOutDriver.none;
        }

        if(dataFlagOutE0 == DataFlagOutDriver.data_flag_in_S_0 || dataFlagOutE0 == DataFlagOutDriver.data_flag_in_S_1) {
            dataFlagOutE0 = DataFlagOutDriver.none;
        }

        if(dataFlagOutE1 == DataFlagOutDriver.data_flag_in_S_0 || dataFlagOutE1 == DataFlagOutDriver.data_flag_in_S_1) {
            dataFlagOutE1 = DataFlagOutDriver.none;
        }

        if(dataFlagOutS0 == DataFlagOutDriver.data_flag_in_S_0 || dataFlagOutS0 == DataFlagOutDriver.data_flag_in_S_1) {
            dataFlagOutS0 = DataFlagOutDriver.none;
        }

        if(dataFlagOutS1 == DataFlagOutDriver.data_flag_in_S_0 || dataFlagOutS1 == DataFlagOutDriver.data_flag_in_S_1) {
            dataFlagOutS1 = DataFlagOutDriver.none;
        }

        if(dataFlagInFU0 == DataFlagInFuDriver.data_flag_in_S_0 || dataFlagInFU0 == DataFlagInFuDriver.data_flag_in_S_1) {
           dataFlagInFU0 = DataFlagInFuDriver.none;
        }

        if(dataFlagInFU1 == DataFlagInFuDriver.data_flag_in_S_0 || dataFlagInFU1 == DataFlagInFuDriver.data_flag_in_S_1) {
           dataFlagInFU1 = DataFlagInFuDriver.none;
        }

        if(dataFlagInFUMux == DataFlagInFuDriver.data_flag_in_S_0 || dataFlagInFUMux == DataFlagInFuDriver.data_flag_in_S_1) {
            dataFlagInFUMux = DataFlagInFuDriver.none;
        }

        this.checkSetActive();
    }
}
