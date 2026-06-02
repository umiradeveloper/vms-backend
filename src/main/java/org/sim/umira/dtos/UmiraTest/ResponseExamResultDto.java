package org.sim.umira.dtos.UmiraTest;

import java.time.LocalDateTime;
import java.util.List;


import org.sim.umira.entities.UmiraTest.ExamResultQuestionEntity;

public class ResponseExamResultDto {
    
    public String id_exam_result;

    public String status_pass;

    public LocalDateTime date_submit_exam;

    public Integer score;

    public Integer duration_result;

    public Integer correct_answer;

    public Integer wrong_answer;

    public Integer un_answer;

    public ResponseExamDto exam;

    public List<?> examQuestionResult;

    public List<?> examQuestion;

    public ResponseExamResultDto(String id_exam_result, String status_pass, LocalDateTime date_submit_exam,
            Integer score, Integer duration_result, Integer correct_answer, Integer wrong_answer, Integer un_answer,
            ResponseExamDto exam, List<?> examQuestionResult, List<?> examQuestion) {
        this.id_exam_result = id_exam_result;
        this.status_pass = status_pass;
        this.date_submit_exam = date_submit_exam;
        this.score = score;
        this.duration_result = duration_result;
        this.correct_answer = correct_answer;
        this.wrong_answer = wrong_answer;
        this.un_answer = un_answer;
        this.exam = exam;
        this.examQuestionResult = examQuestionResult;
        this.examQuestion = examQuestion;
    }

    

}
