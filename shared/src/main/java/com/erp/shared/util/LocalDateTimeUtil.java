package com.erp.shared.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@UtilityClass
public class LocalDateTimeUtil {

    public Long getCurrentTimeInMills() {
        return Instant.now().toEpochMilli();
    }

    public LocalDateTime getInstantNow() {
        return getInstantNow("UTC");
    }

    public LocalDateTime getInstantNow(String timeZone) {
        return Instant.ofEpochMilli(getCurrentTimeInMills())
                .atZone(ZoneId.of(timeZone))
                .toLocalDateTime();
    }
}
