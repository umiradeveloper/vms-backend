package org.sim.umira.entities.CostControl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "cc_satuan")
public class SatuanEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_satuan;

    public String nama_satuan;

    public String kode_satuan;
}
