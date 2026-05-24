package org.sim.umira.dtos.ChecklistTransaksi;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiDetailEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiPaymentEntity;


public class ResponseApprovalTransaksiDto {
    
    public String id_transaksi;

    public String jenis_transaksi;

    public LocalDate tanggal_pengajuan;

    public String keterangan;

    public String proyek;

    public String layak_bayar;

    public String status_pengajuan;

    public LocalDateTime last_updated;

    public String upload_bukti_pembayaran;

    public String catatan_verified;

    public String kode_transaksi;

    public LocalDateTime payment_at;
     public LocalDateTime approved_at;

    public String catatan_payment;

    public String tempo_pembayaran_after_verified;

    public LocalDate tanggal_jatuh_tempo_after_verified;

    public String transaksi_via;

    public BigInteger nilai_invoice;

    public BigInteger pph;

    public BigInteger ppn;
    public BigInteger retensi;

    public BigInteger kasbon;

    public BigInteger nilai_invoice_bersih;

    public BigInteger biaya_potongan_lainnya;

    public String reference_id_transaksi_proyek;

    public String nomor_invoice;

    public String kategori;

    public String nama_vendor;

    
    public UserEntity paymentBy;

  
    public UserEntity user_pengajuan;

   
    public UserEntity approvedBy;

    
    public UserEntity updatedBy;


    public List<TransaksiDetailEntity> detailTransaksi;

    public List<TransaksiPaymentEntity> detailPayment;
    public List<?> detailPersetujuanProyek;

    public LocalDate tanggal_invoice;

    public String no_po_kontrak;

    public ResponseApprovalTransaksiDto(String id_transaksi, String jenis_transaksi, LocalDate tanggal_pengajuan,
            String keterangan, String proyek, String layak_bayar, String status_pengajuan, LocalDateTime last_updated,
            String upload_bukti_pembayaran, String catatan_verified, String kode_transaksi, LocalDateTime payment_at,
            LocalDateTime approved_at, String catatan_payment, String tempo_pembayaran_after_verified,
            LocalDate tanggal_jatuh_tempo_after_verified, String transaksi_via, BigInteger nilai_invoice,
            BigInteger pph, BigInteger ppn, BigInteger retensi, BigInteger kasbon, BigInteger nilai_invoice_bersih,
            BigInteger biaya_potongan_lainnya,
            String reference_id_transaksi_proyek, UserEntity paymentBy, UserEntity user_pengajuan,
            UserEntity approvedBy, UserEntity updatedBy, List<TransaksiDetailEntity> detailTransaksi,
            List<TransaksiPaymentEntity> detailPayment, String nama_vendor, String kategori, String nomor_invoice, List<?> detailPersetujuanProyek, LocalDate tanggal_invoice, String no_po_kontrak) {
        this.id_transaksi = id_transaksi;
        this.jenis_transaksi = jenis_transaksi;
        this.tanggal_pengajuan = tanggal_pengajuan;
        this.keterangan = keterangan;
        this.proyek = proyek;
        this.layak_bayar = layak_bayar;
        this.status_pengajuan = status_pengajuan;
        this.last_updated = last_updated;
        this.upload_bukti_pembayaran = upload_bukti_pembayaran;
        this.catatan_verified = catatan_verified;
        this.kode_transaksi = kode_transaksi;
        this.payment_at = payment_at;
        this.approved_at = approved_at;
        this.catatan_payment = catatan_payment;
        this.tempo_pembayaran_after_verified = tempo_pembayaran_after_verified;
        this.tanggal_jatuh_tempo_after_verified = tanggal_jatuh_tempo_after_verified;
        this.transaksi_via = transaksi_via;
        this.nilai_invoice = nilai_invoice;
        this.pph = pph;
        this.ppn = ppn;
        this.retensi = retensi;
        this.kasbon = kasbon;
        this.nilai_invoice_bersih = nilai_invoice_bersih;
        this.reference_id_transaksi_proyek = reference_id_transaksi_proyek;
        this.paymentBy = paymentBy;
        this.user_pengajuan = user_pengajuan;
        this.approvedBy = approvedBy;
        this.updatedBy = updatedBy;
        this.detailTransaksi = detailTransaksi;
        this.detailPayment = detailPayment;
        this.nama_vendor = nama_vendor;
        this.nomor_invoice = nomor_invoice;
        this.kategori = kategori;
        this.biaya_potongan_lainnya = biaya_potongan_lainnya;
        this.detailPersetujuanProyek = detailPersetujuanProyek;
        this.tanggal_invoice = tanggal_invoice;
        this.no_po_kontrak = no_po_kontrak;
    }

    
}
