package org.sim.umira.entities.UmiraTest;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "exam_session")
public class ExamSessionEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_exam_session;

    public String id_user;

    public String id_exam;

    public LocalDateTime session_open;

    public LocalDateTime session_close;
}
