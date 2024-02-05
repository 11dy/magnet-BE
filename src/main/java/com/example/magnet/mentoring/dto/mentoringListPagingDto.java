package com.example.magnet.mentoring.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class mentoringListPagingDto {
    private Long mentoringId;
    private String title;
    private String content;
    private String pay;
    private String period; // 전체 기간
    private int participants;
    private String category;

    private Long mentorId;


    @QueryProjection
    public mentoringListPagingDto(Long mentoringId, String title, String content, String pay, String period, int participants, String category, Long mentorId) {
        this.mentoringId = mentoringId;
        this.title = title;
        this.content = content;
        this.pay = pay;
        this.period = period;
        this.participants = participants;
        this.category = category;
        this.mentorId = mentorId;
    }
}
