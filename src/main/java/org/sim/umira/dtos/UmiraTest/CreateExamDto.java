package org.sim.umira.dtos.UmiraTest;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateExamDto {
    public String id_exam;


    @NotBlank(message = "kode_exam must be required")
    public String kode_exam;

    @NotBlank(message = "type_exam must be required")
    public String type_exam;

    @NotBlank(message = "title_exam must be required")
    public String title_exam;

    @NotBlank(message = "desc_exam must be required")
    public String desc_exam;

    @NotNull(message = "date_exam must be required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime date_exam;

    @NotNull(message = "duration_exam must be required")
    public Integer duration_exam;

    @NotNull(message = "status_exam must be required")
    public Integer status_exam;

    @NotNull(message = "limit_score_exam must be required")
    public Integer limit_score_exam;

    @NotNull(message = "take_question must be required")
    public Integer take_question;

    @NotNull(message = "count_user must be required")
    public Integer count_user;

    @NotNull(message = "role_access must be required")
    public List<String> role_access;

    @NotNull(message = "question must be required")
    public List<String> question;

    @NotNull(message = "question_type must be required")
    public List<String> question_type;

    @NotNull(message = "score must be required")
    public List<Integer> score;

    @NotNull(message = "correct_answer must be required")
    public List<String> correct_answer;

    @NotNull(message = "question_option must be required")
    public List<String> question_option;



}
