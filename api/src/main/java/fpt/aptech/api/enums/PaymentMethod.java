package fpt.aptech.api.enums;


public enum PaymentMethod {
    MOMO((short) 1),
    CASH((short) 0);

    private final short value;

    PaymentMethod(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
