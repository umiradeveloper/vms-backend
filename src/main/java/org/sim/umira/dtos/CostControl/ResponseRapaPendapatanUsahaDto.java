package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;


public class ResponseRapaPendapatanUsahaDto {

    public String id_rapa;

    public String Kategori;

    public String kode_rap;

    public String group;

    public String item_pekerjaan;

    public String spesifikasi;

    public String satuan;

    public BigDecimal volume;

    public Integer harga_satuan;

    public BigDecimal harga_total;

    public BigDecimal total_bk_rapa;

    public ResponseRapaPendapatanUsahaDto(String id_rapa, String kategori, String kode_rap, String group,
            String item_pekerjaan, String spesifikasi, String satuan, BigDecimal volume, Integer harga_satuan,
            BigDecimal harga_total, BigDecimal total_bk_rapa) {
        this.id_rapa = id_rapa;
        Kategori = kategori;
        this.kode_rap = kode_rap;
        this.group = group;
        this.item_pekerjaan = item_pekerjaan;
        this.spesifikasi = spesifikasi;
        this.satuan = satuan;
        this.volume = volume;
        this.harga_satuan = harga_satuan;
        this.harga_total = harga_total;
        this.total_bk_rapa = total_bk_rapa;
    }

}
