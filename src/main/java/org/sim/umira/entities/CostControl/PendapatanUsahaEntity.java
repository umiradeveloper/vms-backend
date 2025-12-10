package org.sim.umira.entities.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cc_pendapatanusaha")
public class PendapatanUsahaEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_pu;

    public Integer nominal_pu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyek")
    @JsonBackReference
    public ProyekEntity proyek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rapa")
    @JsonBackReference
    public RapaEntity rapa;
}
