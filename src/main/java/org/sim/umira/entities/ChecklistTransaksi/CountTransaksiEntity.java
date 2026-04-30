package org.sim.umira.entities.ChecklistTransaksi;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "checklist_count_transaksi")
public class CountTransaksiEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_count_transaksi;

    public String kode_transaksi;

    public Integer jumlah;
}
