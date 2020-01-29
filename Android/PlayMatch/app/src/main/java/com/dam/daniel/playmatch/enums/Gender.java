package com.dam.daniel.playmatch.enums;

/**
 * Note: You could simple use the ordinal() method, which would return the position of the enum constant in the enum.
 */
public enum Gender {
    // One Way To Get Value Of Each Propertie Of Enum
    MALE(1), FEMALE(2), OTHER(3);
    private final int value;

    /**
     * Constructor
     * @param value
     */
    private Gender(int value) {
        this.value = value;
    }

    /**
     * Get value(int) of each element of enum
     * @return
     */
    public int getValue() {
        return value;
    }
}
