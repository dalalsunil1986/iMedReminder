package com.cryptic.imed.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author sharafat
 */
@DatabaseTable
public class PrescriptionMedicine implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, foreign = true, index = true,
            columnDefinition = "integer references prescription(id) on delete cascade")
    private Prescription prescription;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private Medicine medicine;

    @DatabaseField(canBeNull = false)
    private Date startDate;

    @DatabaseField(canBeNull = false)
    private int dosesToTake;

    @DatabaseField(canBeNull = false)
    private int dayInterval;

    @DatabaseField(canBeNull = false)
    private int totalDaysToTake;

    @ForeignCollectionField(orderColumnName = "doseNo")
    private Collection<Dosage> dosageReminders;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getDosesToTake() {
        return dosesToTake;
    }

    public void setDosesToTake(int dosesToTake) {
        this.dosesToTake = dosesToTake;
    }

    public int getDayInterval() {
        return dayInterval;
    }

    public void setDayInterval(int dayInterval) {
        this.dayInterval = dayInterval;
    }

    public int getTotalDaysToTake() {
        return totalDaysToTake;
    }

    public void setTotalDaysToTake(int totalDaysToTake) {
        this.totalDaysToTake = totalDaysToTake;
    }

    public Collection<Dosage> getDosageReminders() {
        return dosageReminders;
    }

    public void setDosageReminders(Collection<Dosage> dosageReminders) {
        this.dosageReminders = dosageReminders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrescriptionMedicine that = (PrescriptionMedicine) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "PrescriptionMedicine{" +
                "id=" + id +
                ", prescriptionId=" + (prescription != null ? prescription.getId() : null) +
                ", medicine=" + medicine +
                ", startDate=" + startDate +
                ", dosesToTake=" + dosesToTake +
                ", dayInterval=" + dayInterval +
                ", totalDaysToTake=" + totalDaysToTake +
                ", dosageReminders=" + dosageReminders +
                '}';
    }
}
