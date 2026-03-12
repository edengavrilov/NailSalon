package ThePerfectQueue;

public enum userType {
    CUSTOMER(1),
    PROVIDER(2),
    MANAGER(3);

    private final int code;

    userType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static userType fromCode(int code) {
        for (userType type : userType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid user type code: " + code);
    }
}
