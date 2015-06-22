package de.tuberlin.onedrivesdk.common;

/**
 * Defines the method to handle conflicts in the copy, move and upload process.
 */
public enum ConflictBehavior {
    RENAME("rename"),REPLACE("replace"),FAIL("fail");

    public String name;
    ConflictBehavior(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
