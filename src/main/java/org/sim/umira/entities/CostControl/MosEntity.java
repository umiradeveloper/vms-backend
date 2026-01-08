package org.sim.umira.entities.CostControl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "cc_materialpu")
public class MosEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_mos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pu")
    @JsonBackReference
    public PendapatanUsahaEntity pu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rapa")
    @JsonBackReference
    public RapaEntity rapa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyek")
    @JsonBackReference
    public ProyekEntity proyek;

    public BigDecimal volume;

    public String created_by;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;


}
