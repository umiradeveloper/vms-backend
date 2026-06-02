package org.sim.umira.entities.UmiraTest;

import org.sim.umira.entities.UserEntity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "exam_question_result")
public class ExamResultQuestionEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_question_exam_result;

    public String answer;

    public Integer score_question;

    @ManyToOne
    @JoinColumn(name = "id_exam_result")
    @JsonBackReference
    public ExamResultEntity examResult;

    @ManyToOne
    @JoinColumn(name = "id_question_exam")
    @JsonBackReference
    public ExamQuestionEntity QuestionExam;

}
