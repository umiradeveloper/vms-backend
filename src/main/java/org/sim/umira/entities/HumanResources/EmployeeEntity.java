package org.sim.umira.entities.HumanResources;

import java.time.LocalDate;

import org.sim.umira.entities.MasterProjectEntity;
import org.sim.umira.entities.UserEntity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "hr_employee")
public class EmployeeEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_employee;

    public String nama;

    public String departemen;

    public String nip;

    public String jabatan;

    public String email;

    public String no_hp;

    public LocalDate tmt;

    public String tmt_akhir;

    public String status_karyawan;

    public String foto_url;

    public LocalDate tanggal_lahir;

    public String tempat_lahir;

    public String alamat;

    public String npwp;

    public String ptkp_status;

    public String bank_name;

    public String bank_account;

    public String bpjs_ketenagakerjaan;

    public String bpjs_kesehatan;

    public String nik;

    public String jenis_kelamin;

    public String marital_status;

    public String blood_type;

    public String grade;

    public String kelas;

    public Integer pkwt_ke;

    public String id_employee_checker;

    public String id_employee_signer;


    public String bank_account_holder;

    public String religion;

    public String emergency_call;

    public String pendidikan_terakhir;

    @OneToOne
    @JoinColumn(name = "id_user")
    public UserEntity user;

    
    @ManyToOne
    @JoinColumn(name = "id_klasifikasi_works")
    public KlasifikasiWorkEntity klasifikasi_works;


    @ManyToOne
    @JoinColumn(name = "id_project")
    public MasterProjectEntity project;


    
}
