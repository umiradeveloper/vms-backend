package org.sim.umira.dtos.HumanResources;

public class PayrollMasterDto {
    public String id_employee;

    public Integer gaji_pokok;

    public Integer tunjangan_transport;

    public Integer tunjangan_jabatan;

    public Integer tunjangan_makan;

    public String tunjangan_lembur;

    public Integer tunjangan_lainnya;

    public Integer bpjs_kesehatan;

    public Integer bpjs_ketenagakerjaan;

    public String tarif_bpjs_kesehatan;

    public String tarif_bpjs_ketenagakerjaan;

    // ========================================Deduction=================================

    public Integer bpjskes;

    public Integer bpjstk;


    public String tarif_bpjskes;

    public String tarif_bpjstk;

    public Integer pph21;
}
