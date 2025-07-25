package model;

public class ParameterModel {
    private final String dataType;
    private final String name;

    public ParameterModel(String dataType, String name) {
        this.dataType = dataType;
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": " + dataType;
    }

}
