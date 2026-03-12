package ThePerfectQueue;

public enum SpecialtyType {
    MANICURE(1),
    PEDICURE(2),
    HAIR_STYLING(3),
    MAKEUP(4),
    SKIN_CARE(5),
    MASSAGE(6);

    private final int code;  

    SpecialtyType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static SpecialtyType fromCode(int code) {
        for (SpecialtyType specialty : SpecialtyType.values()) {
            if (specialty.getCode() == code) {
                return specialty;
            }
        }
        throw new IllegalArgumentException("Invalid specialty code: " + code);
    }
}
