package de.tuebingen.es.crc.configurator.model;

/**
 * Created by Konstantin (Konze) Lübeck on 22/08/16.
 */
public class PE {

    private final Configuration configuration;

    public enum DataFlagOutDriver {
        none, data_flag_in_N_0, data_flag_in_N_1, data_flag_in_S_0, data_flag_in_S_1, data_flag_in_W_0, data_flag_in_W_1, data_flag_out_FU
    }

    public enum DataFlagInFuDriver {
        none, data_flag_in_N_0, data_flag_in_N_1, data_flag_in_S_0, data_flag_in_S_1, data_flag_in_W_0, data_flag_in_W_1
    }

    public enum FUFunction {
        none, add, sub, mul, div, and, or, xor, not, shift_left, shift_right, compare_eq, compare_neq, compare_lt, compare_gt, compare_leq, compare_geq, mux_0, mux_1
    }

    private boolean active;

    private DataFlagOutDriver dataFlagOutN0;
    private DataFlagOutDriver dataFlagOutN1;
    private DataFlagOutDriver dataFlagOutE0;
    private DataFlagOutDriver dataFlagOutE1;
    private DataFlagOutDriver dataFlagOutS0;
    private DataFlagOutDriver dataFlagOutS1;
    private DataFlagInFuDriver dataFlagInFU0;
    private DataFlagInFuDriver dataFlagInFU1;
    private DataFlagInFuDriver flagInFUMux;
    private FUFunction fuFunction;

    public boolean isActive() {
        return active;
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

    public DataFlagInFuDriver getFlagInFuMux() {
        return flagInFUMux;
    }

    public void setFlagInFuMux(DataFlagInFuDriver flagInFUMux) {
        this.flagInFUMux = flagInFUMux;
        this.checkSetActive();
        configuration.notifyAllObservers();
    }

    public FUFunction getFuFunction() {
        return fuFunction;
    }

    public void setFuFunction(FUFunction fuFunction) {
        this.fuFunction = fuFunction;
        configuration.notifyAllObservers();
    }

    public PE(Configuration configuration) {
        this.configuration = configuration;
        active = false;
        dataFlagOutN0 = DataFlagOutDriver.none;
        dataFlagOutN1 = DataFlagOutDriver.none;
        dataFlagOutE0 = DataFlagOutDriver.none;
        dataFlagOutE1 = DataFlagOutDriver.none;
        dataFlagOutS0 = DataFlagOutDriver.none;
        dataFlagOutS1 = DataFlagOutDriver.none;
        dataFlagInFU0 = DataFlagInFuDriver.none;
        dataFlagInFU1 = DataFlagInFuDriver.none;
        flagInFUMux = DataFlagInFuDriver.none;
        fuFunction = FUFunction.none;
    }

// --Commented out by Inspection START (01/09/16 14:35):
//    public PE(Configuration configuration, DataFlagOutDriver dataFlagOutN0, DataFlagOutDriver dataFlagOutN1, DataFlagOutDriver dataFlagOutE0, DataFlagOutDriver dataFlagOutE1, DataFlagOutDriver dataFlagOutS0, DataFlagOutDriver dataFlagOutS1, DataFlagInFuDriver dataFlagInFU0, DataFlagInFuDriver dataFlagInFU1, DataFlagInFuDriver flagInFUMux, FUFunction fuFunction) {
//        this.configuration = configuration;
//        this.dataFlagOutN0 = dataFlagOutN0;
//        this.dataFlagOutN1 = dataFlagOutN1;
//        this.dataFlagOutE0 = dataFlagOutE0;
//        this.dataFlagOutE1 = dataFlagOutE1;
//        this.dataFlagOutS0 = dataFlagOutS0;
//        this.dataFlagOutS1 = dataFlagOutS1;
//        this.dataFlagInFU0 = dataFlagInFU0;
//        this.dataFlagInFU1 = dataFlagInFU1;
//        this.flagInFUMux = flagInFUMux;
//        this.fuFunction = fuFunction;
//
//        this.checkSetActive();
//
//    }
// --Commented out by Inspection STOP (01/09/16 14:35)

    /**
     * copies content of another PE
     * @param pe
     */
    public void copy(PE pe) {
        this.dataFlagOutN0 = pe.getDataFlagOutN0();
        this.dataFlagOutN1 = pe.getDataFlagOutN1();
        this.dataFlagOutE0 = pe.getDataFlagOutE0();
        this.dataFlagOutE1 = pe.getDataFlagOutE1();
        this.dataFlagOutS0 = pe.getDataFlagOutS1();
        this.dataFlagInFU0 = pe.getDataFlagInFu0();
        this.dataFlagInFU1 = pe.getDataFlagInFu1();
        this.flagInFUMux = pe.getFlagInFuMux();
        this.fuFunction = pe.getFuFunction();

        this.checkSetActive();
    }

    /**
     * checks if the PE is active
     */
    private void checkSetActive()  {
        if(
                dataFlagOutN0 == DataFlagOutDriver.none &&
                        dataFlagOutN1 == DataFlagOutDriver.none &&
                        dataFlagOutE0 == DataFlagOutDriver.none &&
                        dataFlagOutE1 == DataFlagOutDriver.none &&
                        dataFlagOutS0 == DataFlagOutDriver.none &&
                        dataFlagOutS1 == DataFlagOutDriver.none &&
                        dataFlagInFU0 == DataFlagInFuDriver.none &&
                        dataFlagInFU1 == DataFlagInFuDriver.none &&
                        flagInFUMux == DataFlagInFuDriver.none) {
            active = false;
        } else {
            active = true;
        }
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

        if(flagInFUMux == DataFlagInFuDriver.data_flag_in_S_0 || flagInFUMux == DataFlagInFuDriver.data_flag_in_S_1) {
            flagInFUMux = DataFlagInFuDriver.none;
        }

        this.checkSetActive();
    }
}
