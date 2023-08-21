package fpt.aptech.portal.enums;

public enum Role {
    SUPER_ADMIN(1),
    HQ(2),
    AREA(3),
    TOUR_GUIDE(4),
    CUSTOMER(5);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
