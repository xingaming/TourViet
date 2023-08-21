package fpt.aptech.portal.enums;


public enum PaymentStatus {
    PAID((short) 1),
    UNPAID((short) 0);

    private final short value;

    PaymentStatus(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
