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
@Table(name = "cc_kategori")
public class KategoriEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_kategori;

    public String nama_kategori;

    public String kode_kategori;  
}
