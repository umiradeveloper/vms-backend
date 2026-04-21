package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;

import org.sim.umira.entities.CostControl.RapaEntity;

public class RapaDto {
    public String id_rapa;
    public String kode_rap;
    public String kategori;
    public String item_pekerjaan;

    public String spesifikasi;

    public String satuan;

    public BigDecimal volume;

    public Integer harga_satuan;

    public BigDecimal harga_total;

    public RapaDto(RapaEntity r) {
        this.id_rapa = r.id_rapa;

        if (r.costCodeRapa != null) {
            this.kode_rap = r.costCodeRapa.cost_code;
            this.item_pekerjaan = r.costCodeRapa.nama;
            this.spesifikasi = r.costCodeRapa.spesifikasi;
            this.satuan = r.costCodeRapa.satuan;
            this.volume = r.volume;
            this.harga_satuan = r.harga_satuan;
            this.harga_total = r.harga_total;

            if (r.costCodeRapa.kategori != null) {
                this.kategori = r.costCodeRapa.kategori.nama_kategori;
            }
        }
    }
}
