package com.cryptic.imed.domain;

import com.cryptic.imed.util.Filterable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * @author sharafat
 */
@DatabaseTable
public class Medicine implements Filterable, Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String details;

    @DatabaseField(canBeNull = false)
    private MedicationUnit medicationUnit;

    @DatabaseField(canBeNull = false)
    private float currentStock;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] photo;

    @DatabaseField(canBeNull = false)
    private boolean deleted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public MedicationUnit getMedicationUnit() {
        return medicationUnit;
    }

    public void setMedicationUnit(MedicationUnit medicationUnit) {
        this.medicationUnit = medicationUnit;
    }

    public float getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(float currentStock) {
        this.currentStock = currentStock;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Medicine medicine = (Medicine) o;

        return id == medicine.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", medicationUnit=" + medicationUnit +
                ", currentStock=" + currentStock +
                ", photo=" + (photo != null ? photo.length : 0) + "bytes" +
                ", deleted=" + deleted +
                '}';
    }

    @Override
    public String getFilterableText() {
        return name;
    }
}
