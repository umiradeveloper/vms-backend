package org.sim.umira.entities.UmiraTest;

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
@Table(name = "exam_question")
public class ExamQuestionEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_question_exam;

    public String question;

    public String question_type;

    public Integer score;

    public String correct_answer;

    public String question_option;

    @ManyToOne
    @JoinColumn(name = "id_exam")
    @JsonBackReference
    public ExamEntity exam;


}
