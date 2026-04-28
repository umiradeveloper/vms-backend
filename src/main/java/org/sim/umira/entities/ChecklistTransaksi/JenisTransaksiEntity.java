package org.sim.umira.entities.ChecklistTransaksi;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "checklist_jenis_transaksi")
public class JenisTransaksiEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_jenis_transaksi;

    public String jenis_transaksi;

    public String nama_transaksi;

    public Integer no_urut;
    
    public String tipe;


}
