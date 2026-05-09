package org.sim.umira.dtos.CostControl;

import java.util.List;

public class ResponseCostCodeDto {
    public String id_cost_code;

    public String cost_code;

    public String nama;

    public String klasifikasi;

    public String spesifikasi;

    public String satuan;

    public String kode_jenis;

    public String jenis;

    public String nama_kategori;

    public String kode_kategori;

    public List<ProjectCostCodeDto> proyek;

    public ResponseCostCodeDto(String id_cost_code, String cost_code, String nama, String klasifikasi,
            String spesifikasi, String satuan, String kode_jenis, String jenis,String nama_kategori, String kode_kategori,List<ProjectCostCodeDto> proyek) {
        this.id_cost_code = id_cost_code;
        this.cost_code = cost_code;
        this.nama = nama;
        this.klasifikasi = klasifikasi;
        this.spesifikasi = spesifikasi;
        this.satuan = satuan;
        this.kode_jenis = kode_jenis;
        this.jenis = jenis;
        this.proyek = proyek;
        this.nama_kategori = nama_kategori;
        this.kode_kategori = kode_kategori;
    }

    

}
