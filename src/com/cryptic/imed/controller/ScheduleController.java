package com.cryptic.imed.controller;

import android.util.MonthDisplayHelper;
import com.cryptic.imed.app.DbHelper;
import com.cryptic.imed.common.Constants;
import com.cryptic.imed.domain.PrescriptionMedicine;
import com.cryptic.imed.util.DateWithoutTime;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sharafat
 */
@Singleton
public class ScheduleController {
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);

    private final RuntimeExceptionDao<PrescriptionMedicine, Integer> prescriptionMedicineDao;

    public ScheduleController() {
        prescriptionMedicineDao = DbHelper.getHelper().getRuntimeExceptionDao(PrescriptionMedicine.class);
    }

    public Map<DateWithoutTime, List<PrescriptionMedicine>> list(int year, int month, int weekStartDay) {
        MonthDisplayHelper monthDisplayHelper = new MonthDisplayHelper(year, month, weekStartDay);

        int firstDayDisplayedOnCalendarView = monthDisplayHelper.getDigitsForRow(0)[0];
        int lastDayDisplayedOnCalendarView = monthDisplayHelper.getDigitsForRow(
                monthDisplayHelper.getRowOf(monthDisplayHelper.getNumberOfDaysInMonth()))[6];
        log.debug("firstDayDisplayedOnCalendarView: {}, lastDayDisplayedOnCalendarView: {}",
                firstDayDisplayedOnCalendarView, lastDayDisplayedOnCalendarView);

        DateWithoutTime calendarStartDate = new DateWithoutTime(year,
                firstDayDisplayedOnCalendarView == 1 ? month : month - 1,
                firstDayDisplayedOnCalendarView);
        DateWithoutTime calendarEndDate = new DateWithoutTime(year,
                lastDayDisplayedOnCalendarView == monthDisplayHelper.getNumberOfDaysInMonth() ? month : month + 1,
                lastDayDisplayedOnCalendarView);

        String sqliteFormattedCalendarStartDate = sqliteFormattedDate(calendarStartDate);
        String sqliteFormattedCalendarEndDate = sqliteFormattedDate(calendarEndDate);
        log.debug("calendarStartDate: {}, calendarEndDate: {}",
                sqliteFormattedCalendarStartDate, sqliteFormattedCalendarEndDate);

        GenericRawResults<PrescriptionMedicine> prescriptionMedicines =
                queryPrescriptionMedicineList(sqliteFormattedCalendarStartDate, sqliteFormattedCalendarEndDate);

        return prepareSchedules(calendarStartDate, calendarEndDate, prescriptionMedicines);
    }

    private String sqliteFormattedDate(DateWithoutTime dateWithoutTime) {
        return dateWithoutTime.format(Constants.SQLITE_DATE_FORMAT).toString();
    }

    private GenericRawResults<PrescriptionMedicine> queryPrescriptionMedicineList(
            String sqliteFormattedCalendarStartDate, String sqliteFormattedCalendarEndDate) {
        return prescriptionMedicineDao.queryRaw(
                "select id, date(startDate, '+' || (totalDaysToTake * (dayInterval + 1) - 2) || 'day') as endDate " +
                        "from prescriptionmedicine " +
                        "where startDate <= ? and endDate >= ?",
                new RawRowMapper<PrescriptionMedicine>() {
                    @Override
                    public PrescriptionMedicine mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                        log.debug("prescriptionMedicines id: {}", Integer.parseInt(resultColumns[0]));
                        return prescriptionMedicineDao.queryForId(Integer.parseInt(resultColumns[0]));
                    }
                },
                sqliteFormattedCalendarEndDate,
                sqliteFormattedCalendarStartDate
        );
    }

    private Map<DateWithoutTime, List<PrescriptionMedicine>> prepareSchedules(DateWithoutTime calendarStartDate, DateWithoutTime calendarEndDate, GenericRawResults<PrescriptionMedicine> prescriptionMedicines) {
        Map<DateWithoutTime, List<PrescriptionMedicine>> schedules = new HashMap<DateWithoutTime, List<PrescriptionMedicine>>();

        for (PrescriptionMedicine prescriptionMedicine : prescriptionMedicines) {
            DateWithoutTime startingDate = new DateWithoutTime(prescriptionMedicine.getStartDate());
            DateWithoutTime endingDate = new DateWithoutTime(prescriptionMedicine.getEndDate());
            log.debug("startingDate: {}, endingDate: {}", startingDate, endingDate);

            DateWithoutTime date = startingDate.clone();

            if (startingDate.before(calendarStartDate)) {
                do {
                    date.add(DateWithoutTime.DATE, prescriptionMedicine.getDayInterval() + 1);
                } while (date.before(calendarStartDate));
            }

            log.debug("date: {}", date);

            do {
                if (!schedules.containsKey(date)) {
                    schedules.put(date.clone(), new ArrayList<PrescriptionMedicine>());
                }

                schedules.get(date).add(prescriptionMedicine);
                date.add(DateWithoutTime.DATE, prescriptionMedicine.getDayInterval() + 1);
            } while (date.beforeOrOn(endingDate) && date.beforeOrOn(calendarEndDate));
        }

        return schedules;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        DbHelper.release();
    }
}
