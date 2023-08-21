package fpt.aptech.api.enums;

public enum RoleId {
        SUPER_ADMIN(1),
        HQ(2),
        AREA(3),
        TOUR_GUIDE(4),
        CUSTOMER(5);

        private final int value;

        RoleId(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
