package de.tuberlin.onedrivesdk.common;

/**
 * Created by Sebastian on 06.05.2015.
 * Defines the method to handle with conflicts in the copy, move and upload process.
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
