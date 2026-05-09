package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;

public class BkKategoriDto {
    public String id_kategori;

    public String kode_kategori;

    public String nama_kategori;

    public BigDecimal biaya;

    public BkKategoriDto(String id_kategori,String kode_kategori, String nama_kategori, BigDecimal biaya) {
        this.kode_kategori = kode_kategori;
        this.nama_kategori = nama_kategori;
        this.biaya = biaya;
        this.id_kategori = id_kategori;
    }

    
}
