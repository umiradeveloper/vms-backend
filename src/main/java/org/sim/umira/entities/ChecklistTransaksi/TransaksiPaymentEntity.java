package org.sim.umira.entities.ChecklistTransaksi;

import java.math.BigInteger;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "checklist_bukti_bayar")
public class TransaksiPaymentEntity extends PanacheEntityBase {
     @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_checklist_bukti_bayar;

    public String id_transaksi;

    public String reference_id_transaksi;

    public String bukti_bayar;

    public BigInteger nominal_bayar;

}
