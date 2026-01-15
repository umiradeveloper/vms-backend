package org.sim.umira.entities.CostControl;

import java.math.BigInteger;
import java.time.LocalDate;

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
@Table(name = "cc_scurve")
public class ScurveEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_scurve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyek")
    @JsonBackReference
    public ProyekEntity proyek;

    public String week;

    public BigInteger nominal_scurve;

    public LocalDate tanggal_awal;

    public LocalDate tanggal_akhir;

    public String url_dokumen;
}
