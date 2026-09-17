package org.sim.umira.dtos.HumanResources;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmployeeDto {
    public String id_employee;

    @NotBlank(message = "User harus di isi")
    public String id_user;

    @NotBlank(message = "Nama Harus di isi")
    public String nama;

    @NotBlank(message = "Departemen harus di isi")
    public String departemen;

    @NotBlank(message = "Nomor induk pegawai harus di isi")
    public String nip;

    @NotBlank(message = "Jabatan harus di isi")
    public String jabatan;


    @NotBlank(message = "Email harus di isi")
    public String email;


    @NotBlank(message = "No Hp harus di isi")
    public String no_hp;

    @NotNull(message = "Tanggal masuk harus di isi")
    public LocalDate tmt;

    @NotBlank(message = "Tanggal akhir harus di isi")
    public String tmt_akhir;

    @NotBlank(message = "Status karyawan harus di isi")    
    public String status_karyawan;

    public String foto_url;

    @NotNull(message = "Tanggal lahir harus di isi")
    public LocalDate tanggal_lahir;

    @NotBlank(message = "Tempat lahir harus di isi") 
    public String tempat_lahir;

    @NotBlank(message = "Alamat harus di isi") 
    public String alamat;

    @NotBlank(message = "Npwp harus di isi") 
    public String npwp;

    @NotBlank(message = "PTKP Status harus di isi") 
    public String ptkp_status;

    @NotBlank(message = "Nama bank harus di isi") 
    public String bank_name;

    @NotBlank(message = "Akun bank harus di isi") 
    public String bank_account;

    @NotBlank(message = "BPJS Ketenagakerjaan harus di isi") 
    public String bpjs_ketenagakerjaan;

    @NotBlank(message = "BPJS Kesehatan harus di isi") 
    public String bpjs_kesehatan;

    @NotBlank(message = "NIK harus di isi") 
    public String nik;

    @NotBlank(message = "Jenis Kelamin harus di isi") 
    public String jenis_kelamin;

    @NotBlank(message = "Status pernikahan harus di isi") 
    public String marital_status;

    @NotBlank(message = "Golongan darah harus di isi") 
    public String blood_type;

    @NotBlank(message = "Grade harus di isi") 
    public String grade;

    @NotBlank(message = "Kelas harus di isi") 
    public String kelas;

    @NotNull(message = "PKWT ke harus di isi") 
    public Integer pkwt_ke;

    @NotBlank(message = "Klasifikasi works harus di isi") 
    public String klasifikasi_works;

    @NotBlank(message = "Project harus di isi") 
    public String id_project;

    @NotBlank(message = "Approval Line harus di isi") 
    public String id_employee_checker;

    @NotBlank(message = "manager harus di isi") 
    public String id_employee_signer;

    @NotBlank(message = "Nama pemilik bank harus di isi") 
    public String bank_account_holder;

    @NotBlank(message = "Agama harus di isi") 
    public String religion;

    @NotBlank(message = "Kontak darurat harus di isi") 
    public String emergency_call;

    @NotBlank(message = "Pendidikan terakhir harus di isi") 
    public String pendidikan_terakhir;

    
}
