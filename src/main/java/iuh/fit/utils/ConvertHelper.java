package iuh.fit.utils;

import iuh.fit.models.enums.Gender;
import iuh.fit.models.enums.Position;
import iuh.fit.models.enums.ShiftDaysSchedule;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ConvertHelper {
    public static LocalTime localTimeConverter(Time input) {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String formattedInput = formatter.format(input);

        if (!formattedInput.matches("((([0-1][0-9]|2[0-2]):[0-5][0-9])|(23:00))"))
            throw new IllegalArgumentException(ErrorMessages.CONVERT_HELPER_INVALID_LOCALTIME);

        return LocalTime.parse(formattedInput);
    }

    public static LocalDate LocalDateConverter(Date input) {
        return input.toLocalDate();
    }

    public static LocalDateTime localDateTimeConverter(Timestamp input) {
        return input.toLocalDateTime();
    }

    public static Gender genderConverter(String input) {
        if (!input.matches("(FEMALE|MALE)"))
            throw new IllegalArgumentException(ErrorMessages.CONVERT_HELPER_INVALID_GENDER);

        return input.equalsIgnoreCase("FEMALE")
                ? Gender.FEMALE : Gender.MALE;
    }

    public static Position positionConverter(String input) {
        if (!input.matches("(MANAGER|RECEPTIONIST)"))
            throw new IllegalArgumentException(ErrorMessages.CONVERT_HELPER_INVALID_POSITION);

        return input.equalsIgnoreCase("MANAGER")
                ? Position.MANAGER : Position.RECEPTIONIST;
    }

    public static ShiftDaysSchedule shiftDaysScheduleConverter(String input) {
        return switch (input.toUpperCase()) {
            case "MON_WEB_FRI" -> ShiftDaysSchedule.MON_WED_FRI;
            case "TUE_THU_SAT" -> ShiftDaysSchedule.TUE_THU_SAT;
            case "SUNDAY" -> ShiftDaysSchedule.SUNDAY;
            default -> throw new IllegalArgumentException(ErrorMessages.CONVERT_HELPER_INVALID_SHIFT_DAYS_SCHEDULE);
        };
    }


}