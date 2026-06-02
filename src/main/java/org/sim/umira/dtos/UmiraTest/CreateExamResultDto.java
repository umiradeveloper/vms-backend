package org.sim.umira.dtos.UmiraTest;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateExamResultDto {
    public String id_exam_result;

    @NotBlank(message = "id_exam must be required")
    public String id_exam;

    @NotNull(message = "result_answer must be required")
    public List<String> result_answer;
}
