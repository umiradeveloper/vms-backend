package org.sim.umira.entities.UmiraTest;

import java.time.LocalDateTime;
import java.util.List;

import org.sim.umira.entities.UserEntity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "exam_result")
public class ExamResultEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_exam_result;

    public String status_pass;

    public LocalDateTime date_submit_exam;

    public Integer score;

    public Integer duration_result;

    public Integer correct_answer;

    public Integer wrong_answer;

    public Integer un_answer;


    @ManyToOne
    @JoinColumn(name = "id_exam")
    @JsonBackReference
    public ExamEntity exam;

    @ManyToOne
    @JoinColumn(name = "id_user")
    // @JsonBackReference
    public UserEntity user;


    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL)
    @JsonManagedReference
    public List<ExamResultQuestionEntity> examQuestionResult;




}
