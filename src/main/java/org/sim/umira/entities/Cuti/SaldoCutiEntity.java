package org.sim.umira.entities.Cuti;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_saldo_cuti")
public class SaldoCutiEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_balance;

    public String id_user;

    public Integer tahun;

    public Integer sisa_cuti;

    public Integer used_cuti;

    public LocalDateTime created_at;

    public static SaldoCutiEntity findByUserAndTahun(String id_user, int tahun) {
        return find("id_user = ?1 and tahun = ?2", id_user, tahun).firstResult();
    }
}