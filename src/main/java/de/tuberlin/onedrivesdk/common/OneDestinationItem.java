package de.tuberlin.onedrivesdk.common;

/**
 * Created by Andi on 28.05.2015.
 * Wrapper class for json transport.
 */
public class OneDestinationItem {
    protected ParentReference parentReference;
    protected String name;

    public OneDestinationItem(ParentReference reference, String name) {
        this.parentReference = reference;
        this.name = name;
    }

    public OneDestinationItem(ParentReference reference) {
        this.parentReference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParentReference getParentReference() {
        return parentReference;
    }

    public void setParentReference(ParentReference parentReference) {
        this.parentReference = parentReference;
    }
}
