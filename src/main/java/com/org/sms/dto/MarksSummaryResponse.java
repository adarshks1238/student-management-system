package com.org.sms.dto;

import java.util.List;

public record MarksSummaryResponse(List<MarksResponse> subjectWiseMarks, double totalMarksObtained,
                                   double totalMaxMarks, double percentage) {
}
