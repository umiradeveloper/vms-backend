package org.sim.umira.entities.UmiraTest;

import org.sim.umira.entities.RoleEntity;

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
@Table(name = "exam_access")
public class ExamAccessEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_exam_access;

    @ManyToOne
    @JoinColumn(name = "id_exam")
    @JsonBackReference
    public ExamEntity exam;

    @ManyToOne
    @JoinColumn(name = "id_role")
    // @JsonBackReference
    public RoleEntity role;



}
