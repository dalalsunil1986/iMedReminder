package com.cryptic.imed.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author sharafat
 */
@DatabaseTable
public class Dosage {
    @DatabaseField(canBeNull = false, foreign = true, index = true)
    private PrescriptionMedicine prescriptionMedicine;

    @DatabaseField(canBeNull = false)
    private int doseNo;

    @DatabaseField(canBeNull = false)
    private float quantity;

    @DatabaseField(canBeNull = false)
    private Date time;

    public PrescriptionMedicine getPrescriptionMedicine() {
        return prescriptionMedicine;
    }

    public void setPrescriptionMedicine(PrescriptionMedicine prescriptionMedicine) {
        this.prescriptionMedicine = prescriptionMedicine;
    }

    public int getDoseNo() {
        return doseNo;
    }

    public void setDoseNo(int doseNo) {
        this.doseNo = doseNo;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dosage dosage = (Dosage) o;

        if (doseNo != dosage.doseNo) return false;
        if (Float.compare(dosage.quantity, quantity) != 0) return false;
        if (prescriptionMedicine != null ? !prescriptionMedicine.equals(dosage.prescriptionMedicine) : dosage.prescriptionMedicine != null)
            return false;
        if (time != null ? !time.equals(dosage.time) : dosage.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = prescriptionMedicine != null ? prescriptionMedicine.hashCode() : 0;
        result = 31 * result + doseNo;
        result = 31 * result + (quantity != +0.0f ? Float.floatToIntBits(quantity) : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Dosage{" +
                "prescriptionMedicine=" + prescriptionMedicine +
                ", doseNo=" + doseNo +
                ", quantity=" + quantity +
                ", time=" + time +
                '}';
    }
}
