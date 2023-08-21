package fpt.aptech.api.enums;

public enum Status {
    BLOCK(2),
    ACTIVE(1),
    WAIT(0);

    private final int value;

    Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
