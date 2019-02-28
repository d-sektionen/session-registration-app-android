package se.dsektionen.dcide.Utilities;

public enum EventEnum {
    MEETING(1),
    EVENT(2);

    public int getValue() {
        return value;
    }

    private int value;
    EventEnum(int i) {
        this.value = value;
    }

}
