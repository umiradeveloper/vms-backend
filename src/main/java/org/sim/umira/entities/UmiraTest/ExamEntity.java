package org.sim.umira.entities.UmiraTest;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "exam")
public class ExamEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_exam;

    public String kode_exam;

    public String type_exam;

    public String title_exam;

    public String desc_exam;

    public LocalDateTime date_exam;

    public Integer duration_exam;

    public Integer status_exam;

    public Integer limit_score_exam;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    @JsonManagedReference
    public List<ExamQuestionEntity> examQuestion;


    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    @JsonManagedReference
    public List<ExamAccessEntity> examAccess;



}
