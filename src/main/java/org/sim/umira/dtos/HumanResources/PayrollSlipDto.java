package org.sim.umira.dtos.HumanResources;



import java.math.BigDecimal;

public class PayrollSlipDto {

    public Long idEmployee;

    public String nama;
    public String jabatan;
    public String departemen;

    public int hariKerja;
    public int izin;
    public int sakit;
    public int alpha;

    // Pendapatan
    public BigDecimal gajiPokok = BigDecimal.ZERO;
    public BigDecimal tjJabatan = BigDecimal.ZERO;
    public BigDecimal tjOperasional = BigDecimal.ZERO;
    public BigDecimal tjTransport = BigDecimal.ZERO;
    public BigDecimal tjPulsa = BigDecimal.ZERO;
    public BigDecimal tjMakan = BigDecimal.ZERO;
    public BigDecimal tjLembur = BigDecimal.ZERO;
    public BigDecimal tjLainnya = BigDecimal.ZERO;
    public BigDecimal bpjsKesehatanPendapatan = BigDecimal.ZERO;
    public BigDecimal bpjsKetenagakerjaanPendapatan = BigDecimal.ZERO;

    // Potongan
    public BigDecimal potonganKehadiran = BigDecimal.ZERO;
    public BigDecimal pinjaman = BigDecimal.ZERO;
    public BigDecimal bpjsKesehatan = BigDecimal.ZERO;
    public BigDecimal bpjsKetenagakerjaan = BigDecimal.ZERO;
    public BigDecimal potonganLainnya = BigDecimal.ZERO;
    public BigDecimal pph21 = BigDecimal.ZERO;

    public BigDecimal totalPendapatan = BigDecimal.ZERO;
    public BigDecimal totalPotongan = BigDecimal.ZERO;
    public BigDecimal takeHomePay = BigDecimal.ZERO;
}
