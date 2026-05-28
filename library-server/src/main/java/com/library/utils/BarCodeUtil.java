package com.library.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class BarCodeUtil {

    private static final AtomicInteger BOOK_COUNTER = new AtomicInteger(1);
    private static final AtomicInteger CARD_COUNTER = new AtomicInteger(1);

    public static String generateBookBarcode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = BOOK_COUNTER.getAndIncrement();
        return String.format("BK%s%04d", date, seq % 10000);
    }

    public static String generateCardNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = CARD_COUNTER.getAndIncrement();
        return String.format("RD%s%04d", date, seq % 10000);
    }
}
