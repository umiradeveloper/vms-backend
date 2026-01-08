package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;


public class ResponseMaterialDto {
    public String id_rapa;

    public String id_proyek;

    public String kategori;

    public String kode_rap;

    public String group;

    public String item_pekerjaan;

    public String spesifikasi;

    public String satuan;

   

    public Integer harga_satuan;

    public BigDecimal harga_total;

    public BigDecimal volume_rapa;

    public BigDecimal volume_material;

    public String id_pu;


    public ResponseMaterialDto(String id_rapa, String id_proyek, String kategori, String kode_rap, String group, String item_pekerjaan, String spesifikasi, String satuan, BigDecimal volume_rapa, Integer harga_satuan, BigDecimal harga_total,  String id_pu, BigDecimal volume_material) {
        this.id_rapa = id_rapa;
        this.kategori = kategori;
        this.id_proyek = id_proyek;
        this.kode_rap = kode_rap;
        this.group = group;
        this.item_pekerjaan = item_pekerjaan;
        this.spesifikasi = spesifikasi;
        this.satuan = satuan;
        this.harga_satuan = harga_satuan;
        this.harga_total = harga_total;
        this.volume_rapa = volume_rapa;
        this.volume_material = volume_material;
        this.id_pu = id_pu;
    }

    
}
