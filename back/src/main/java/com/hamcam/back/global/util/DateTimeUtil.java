package com.hamcam.back.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * [DateTimeUtil]
 *
 * 날짜 및 시간 관련 공통 유틸리티 클래스
 * LocalDateTime 및 LocalDate를 다양한 포맷으로 변환하거나
 * 문자열 → 날짜 객체 변환 등에 사용됨.
 *
 * [주요 기능]
 * - 현재 시간 또는 날짜 반환
 * - 날짜/시간 포맷 문자열 변환
 * - 문자열을 LocalDate/LocalDateTime으로 변환
 */
public class DateTimeUtil {

    // 기본 날짜-시간 포맷 (ex: 2025-04-10 15:32:00)
    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 기본 날짜 포맷 (ex: 2025-04-10)
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 현재 날짜-시간을 문자열로 반환 (기본 포맷: yyyy-MM-dd HH:mm:ss)
     */
    public static String getNowDateTimeString() {
        return LocalDateTime.now().format(DEFAULT_DATETIME_FORMATTER);
    }

    /**
     * 현재 날짜를 문자열로 반환 (기본 포맷: yyyy-MM-dd)
     */
    public static String getNowDateString() {
        return LocalDate.now().format(DEFAULT_DATE_FORMATTER);
    }

    /**
     * LocalDateTime → 문자열 변환
     * @param dateTime LocalDateTime 객체
     * @return yyyy-MM-dd HH:mm:ss 형식의 문자열
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_DATETIME_FORMATTER);
    }

    /**
     * LocalDate → 문자열 변환
     * @param date LocalDate 객체
     * @return yyyy-MM-dd 형식의 문자열
     */
    public static String formatDate(LocalDate date) {
        return date.format(DEFAULT_DATE_FORMATTER);
    }

    /**
     * 문자열 → LocalDateTime 변환
     * @param dateTimeString yyyy-MM-dd HH:mm:ss 형식 문자열
     * @return LocalDateTime 객체
     */
    public static LocalDateTime parseToDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DEFAULT_DATETIME_FORMATTER);
    }

    /**
     * 문자열 → LocalDate 변환
     * @param dateString yyyy-MM-dd 형식 문자열
     * @return LocalDate 객체
     */
    public static LocalDate parseToDate(String dateString) {
        return LocalDate.parse(dateString, DEFAULT_DATE_FORMATTER);
    }
}
