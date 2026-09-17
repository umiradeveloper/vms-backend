package org.sim.umira.dtos.HumanResources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PayrollMasterDto {
    @NotBlank(message = "id_employee harus di isi")
    public String id_employee;
    @NotNull(message = "gaji pokok harus di isi")
    public Integer gaji_pokok;
    @NotNull(message = "tunjangan transport harus di isi")
    public Integer tunjangan_transport;
    @NotNull(message = "tunjangan jabatan harus di isi")
    public Integer tunjangan_jabatan;
    @NotNull(message = "tunjangan makan harus di isi")
    public Integer tunjangan_makan;
    @NotBlank(message = "tunjangan lembur harus di isi")
    public String tunjangan_lembur;
    @NotNull(message = "tunjangan lainnya harus di isi")
    public Integer tunjangan_lainnya;
    @NotNull(message = "BPJS Kesehatan harus di isi")
    public Integer bpjs_kesehatan;
    @NotNull(message = "BPJS Ketenagakerjaan harus di isi")
    public Integer bpjs_ketenagakerjaan;
    @NotBlank(message = "tarif bpjs kesehatan harus di isi")
    public String tarif_bpjs_kesehatan;
    @NotBlank(message = "tarif bpjs ketenagakerjaan harus di isi")
    public String tarif_bpjs_ketenagakerjaan;

    @NotNull(message = "Tunjangan pulsa harus di isi")
    public Integer tunjangan_pulsa;

    // ========================================Deduction=================================
    // @NotNull(message = "BPJS Ketenagakerjaan harus di isi")
    public Integer bpjskes;

    public Integer bpjstk;

    @NotBlank(message = "tarif bpjskes harus di isi")
    public String tarif_bpjskes;

    @NotBlank(message = "tarif bpjstk harus di isi")
    public String tarif_bpjstk;

    public Integer pph21;
}
