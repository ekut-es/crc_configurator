package de.tuebingen.es.crc.configurator.model;

/**
 * Created by Konstantin (Konze) Lübeck on 22/08/16.
 */
public class PE {

    private Configuration configuration;

    public enum DataFlagOutDriver {
        NONE, DATA_FLAG_IN_N0, DATA_FLAG_IN_N1, DATA_FLAG_IN_S0, DATA_FLAG_IN_S1, DATA_FLAG_IN_W0, DATA_FLAG_IN_W1, DATA_FLAG_OUT_FU
    }

    public enum DataFlagInFuDriver {
        NONE, DATA_FLAG_IN_N0, DATA_FLAG_IN_N1, DATA_FLAG_IN_S0, DATA_FLAG_IN_S1, DATA_FLAG_IN_W0, DATA_FLAG_IN_W1
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
    private DataFlagInFuDriver muxFlagInFU;
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
    }

    public DataFlagOutDriver getDataFlagOutN1() {
        return dataFlagOutN1;
    }

    public void setDataFlagOutN1(DataFlagOutDriver dataFlagOutN1) {
        this.dataFlagOutN1 = dataFlagOutN1;
        this.checkSetActive();
    }

    public DataFlagOutDriver getDataFlagOutE0() {
        return dataFlagOutE0;
    }

    public void setDataFlagOutE0(DataFlagOutDriver dataFlagOutE0) {
        this.dataFlagOutE0 = dataFlagOutE0;
        this.checkSetActive();
    }

    public DataFlagOutDriver getDataFlagOutE1() {
        return dataFlagOutE1;
    }

    public void setDataFlagOutE1(DataFlagOutDriver dataFlagOutE1) {
        this.dataFlagOutE1 = dataFlagOutE1;
        this.checkSetActive();
    }

    public DataFlagOutDriver getDataFlagOutS0() {
        return dataFlagOutS0;
    }

    public void setDataFlagOutS0(DataFlagOutDriver dataFlagOutS0) {
        this.dataFlagOutS0 = dataFlagOutS0;
        this.checkSetActive();
    }

    public DataFlagOutDriver getDataFlagOutS1() {
        return dataFlagOutS1;
    }

    public void setDataFlagOutS1(DataFlagOutDriver dataFlagOutS1) {
        this.dataFlagOutS1 = dataFlagOutS1;
        this.checkSetActive();
    }

    public DataFlagInFuDriver getDataFlagInFU0() {
        return dataFlagInFU0;
    }

    public void setDataFlagInFU0(DataFlagInFuDriver dataFlagInFU0) {
        this.dataFlagInFU0 = dataFlagInFU0;
        this.checkSetActive();
    }

    public DataFlagInFuDriver getDataFlagInFU1() {
        return dataFlagInFU1;
    }

    public void setDataFlagInFU1(DataFlagInFuDriver dataFlagInFU1) {
        this.dataFlagInFU1 = dataFlagInFU1;
        this.checkSetActive();
    }

    public DataFlagInFuDriver getMuxFlagInFU() {
        return muxFlagInFU;
    }

    public void setMuxFlagInFU(DataFlagInFuDriver muxFlagInFU) {
        this.muxFlagInFU = muxFlagInFU;
        this.checkSetActive();
    }

    public FUFunction getFUFunction() {
        return fuFunction;
    }

    public void setFUFunction(FUFunction fuFunction) {
        this.fuFunction = fuFunction;
    }

    public PE(Configuration configuration) {
        this.configuration = configuration;
        active = false;
        dataFlagOutN0 = DataFlagOutDriver.NONE;
        dataFlagOutN1 = DataFlagOutDriver.NONE;
        dataFlagOutE0 = DataFlagOutDriver.NONE;
        dataFlagOutE1 = DataFlagOutDriver.NONE;
        dataFlagOutS0 = DataFlagOutDriver.NONE;
        dataFlagOutS1 = DataFlagOutDriver.NONE;
        dataFlagInFU0 = DataFlagInFuDriver.NONE;
        dataFlagInFU1 = DataFlagInFuDriver.NONE;
        muxFlagInFU = DataFlagInFuDriver.NONE;
        fuFunction = FUFunction.none;
    }

    public PE(Configuration configuration, DataFlagOutDriver dataFlagOutN0, DataFlagOutDriver dataFlagOutN1, DataFlagOutDriver dataFlagOutE0, DataFlagOutDriver dataFlagOutE1, DataFlagOutDriver dataFlagOutS0, DataFlagOutDriver dataFlagOutS1, DataFlagInFuDriver dataFlagInFU0, DataFlagInFuDriver dataFlagInFU1, DataFlagInFuDriver muxFlagInFU, FUFunction fuFunction) {
        this.configuration = configuration;
        this.dataFlagOutN0 = dataFlagOutN0;
        this.dataFlagOutN1 = dataFlagOutN1;
        this.dataFlagOutE0 = dataFlagOutE0;
        this.dataFlagOutE1 = dataFlagOutE1;
        this.dataFlagOutS0 = dataFlagOutS0;
        this.dataFlagOutS1 = dataFlagOutS1;
        this.dataFlagInFU0 = dataFlagInFU0;
        this.dataFlagInFU1 = dataFlagInFU1;
        this.muxFlagInFU = muxFlagInFU;
        this.fuFunction = fuFunction;

        this.checkSetActive();

    }

    private void checkSetActive()  {
        if(dataFlagOutN0 == DataFlagOutDriver.NONE && dataFlagOutN1 == DataFlagOutDriver.NONE && dataFlagOutE0 == DataFlagOutDriver.NONE && dataFlagOutE1 == DataFlagOutDriver.NONE && dataFlagOutS0 == DataFlagOutDriver.NONE && dataFlagOutS1 == DataFlagOutDriver.NONE && dataFlagInFU0 == DataFlagInFuDriver.NONE && dataFlagInFU1 == DataFlagInFuDriver.NONE && muxFlagInFU == DataFlagInFuDriver.NONE) {
            active = false;
        } else {
            active = true;
        }
    }
}
