package com.cryptic.imed.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.Date;

/**
 * @author sharafat
 */
@DatabaseTable
public class Prescription {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String title;

    @DatabaseField
    private String details;

    @DatabaseField(canBeNull = false)
    private Date issueDate;

    @DatabaseField(foreign = true)
    private Doctor prescribedBy;

    @ForeignCollectionField
    private Collection<PrescriptionMedicine> medicines;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Doctor getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(Doctor prescribedBy) {
        this.prescribedBy = prescribedBy;
    }

    public Collection<PrescriptionMedicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(Collection<PrescriptionMedicine> medicines) {
        this.medicines = medicines;
    }

    public boolean isOngoing() {
        //TODO: Implement method
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prescription that = (Prescription) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", issueDate=" + issueDate +
                ", prescribedBy=" + prescribedBy +
                ", medicines=" + medicines +
                '}';
    }
}
