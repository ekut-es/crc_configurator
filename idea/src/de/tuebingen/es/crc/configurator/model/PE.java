package de.tuebingen.es.crc.configurator.model;

/**
 * Created by Konstantin (Konze) Lübeck on 22/08/16.
 */
public class PE {

    private Configuration configuration;

    public enum OutputSource {
        NONE, N0, N1, S0, S1, W0, W1
    }

    private boolean active;
    private OutputSource N0source;
    private OutputSource N1source;
    private OutputSource E0source;
    private OutputSource E1source;
    private OutputSource S0source;
    private OutputSource S1source;

    public boolean isActive() {
        return active;
    }

    public OutputSource getN0source() {
        return N0source;
    }

    public void setN0source(OutputSource n0source) {
        N0source = n0source;
        this.checkSetActive();
    }

    public OutputSource getN1source() {
        return N1source;
    }

    public void setN1source(OutputSource n1source) {
        N1source = n1source;
        this.checkSetActive();
    }

    public OutputSource getE0source() {
        return E0source;
    }

    public void setE0source(OutputSource e0source) {
        E0source = e0source;
        this.checkSetActive();
    }

    public OutputSource getE1source() {
        return E1source;
    }

    public void setE1source(OutputSource e1source) {
        E1source = e1source;
        this.checkSetActive();
    }

    public OutputSource getS0source() {
        return S0source;
    }

    public void setS0source(OutputSource s0source) {
        S0source = s0source;
        this.checkSetActive();
    }

    public OutputSource getS1source() {
        return S1source;
    }

    public void setS1source(OutputSource s1source) {
        S1source = s1source;
        this.checkSetActive();
    }


    public PE(Configuration configuration) {
        this.configuration = configuration;
        active = false;
        N0source = OutputSource.NONE;
        N1source = OutputSource.NONE;
        E0source = OutputSource.NONE;
        E1source = OutputSource.NONE;
        S0source = OutputSource.NONE;
        S1source = OutputSource.NONE;
    }

    public PE(Configuration configuration, OutputSource N0source, OutputSource N1source, OutputSource E0source, OutputSource E1source, OutputSource S0source, OutputSource S1source) {
        this.configuration = configuration;
        this.N0source = N0source;
        this.N1source = N1source;
        this.E0source = E0source;
        this.E1source = E1source;
        this.S0source = S0source;
        this.S1source = S1source;

        this.checkSetActive();

    }

    private void checkSetActive()  {
        if(N0source == OutputSource.NONE && N1source == OutputSource.NONE && E0source == OutputSource.NONE && E1source == OutputSource.NONE && S0source == OutputSource.NONE && S1source == OutputSource.NONE) {
            active = false;
        } else {
            active = true;
        }
    }
}
