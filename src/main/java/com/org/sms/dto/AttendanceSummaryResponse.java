package com.org.sms.dto;

import java.util.List;

public record AttendanceSummaryResponse(List<AttendanceResponse> records, long totalClasses,
                                        long presentCount, long absentCount, double attendancePercentage) {
}
