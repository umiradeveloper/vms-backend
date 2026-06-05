package org.sim.umira.dtos.UmiraTest;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseExamDto {
    public String id_exam;

    public String kode_exam;

    public String type_exam;

    public String title_exam;

    public String desc_exam;

    public LocalDateTime date_exam;

    public Integer duration_exam;

    public Integer status_exam;

    public Integer limit_score_exam;

    public Integer take_question;

    public Integer count_user;

    public List<?> examResult;

    public List<?> examQuestion;

    public List<?> examAccess;

    public ResponseExamDto(String id_exam, String kode_exam, String type_exam, String title_exam, String desc_exam,
            LocalDateTime date_exam, Integer duration_exam, Integer status_exam, Integer limit_score_exam, Integer take_question, Integer count_user , List<?> examResult, List<?> examQuestion, List<?> examAccess) {
        this.id_exam = id_exam;
        this.kode_exam = kode_exam;
        this.type_exam = type_exam;
        this.title_exam = title_exam;
        this.desc_exam = desc_exam;
        this.date_exam = date_exam;
        this.duration_exam = duration_exam;
        this.status_exam = status_exam;
        this.limit_score_exam = limit_score_exam;
        this.examResult = examResult;
        this.examQuestion = examQuestion;
        this.examAccess = examAccess;
        this.count_user = count_user;
        this.take_question = take_question;
    }

    
}
