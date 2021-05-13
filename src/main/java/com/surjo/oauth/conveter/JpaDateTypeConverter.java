package com.surjo.oauth.conveter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class JpaDateTypeConverter {

    @Converter(autoApply = true)
    public static class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

        @Override
        public Date convertToDatabaseColumn(LocalDate localDate) {
            if(localDate != null) {
                return Date.valueOf(localDate);
            }
            return null;
        }

        @Override
        public LocalDate convertToEntityAttribute(Date date) {
            if(date != null) {
                return date.toLocalDate();
            }
            return null;
        }
    }

    @Converter(autoApply = true)
    public static class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

        @Override
        public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
            if(localDateTime != null) {
                return Timestamp.valueOf(localDateTime);
            }
            return null;
        }

        @Override
        public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
            if(timestamp != null) {
                return timestamp.toLocalDateTime();
            }
            return null;
        }
    }

    @Converter(autoApply = true)
    public static class LocalTimeConverter implements AttributeConverter<LocalTime, Time> {

        @Override
        public Time convertToDatabaseColumn(LocalTime localTime) {
            if(localTime != null) {
                return Time.valueOf(localTime);
            }
            return null;
        }

        @Override
        public LocalTime convertToEntityAttribute(Time time) {
            if(time != null) {
                return time.toLocalTime();
            }
            return null;
        }
    }

    @Converter(autoApply = true)
    public static class BooleanConverter implements AttributeConverter<Boolean, Boolean> {
        @Override
        public Boolean convertToDatabaseColumn(Boolean aBoolean) {
            return aBoolean == null ? false : aBoolean;
        }

        @Override
        public Boolean convertToEntityAttribute(Boolean aBoolean) {
            return aBoolean == null ? false : aBoolean;
        }
    }
}
