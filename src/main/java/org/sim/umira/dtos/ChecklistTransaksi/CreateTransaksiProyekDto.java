package org.sim.umira.dtos.ChecklistTransaksi;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateTransaksiProyekDto {
     @FormParam("jenis_transaksi")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "jenis_transaksi must be required")
    public String jenis_transaksi;

     @FormParam("proyek")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "proyek must be required")
    public String proyek;


    @FormParam("tempo_pembayaran_after_verified")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "Tempo must be required")
    public String tempo_pembayaran_after_verified;

    @FormParam("catatan")
    @PartType(MediaType.TEXT_PLAIN)
    public String catatan;

    @FormParam("kode_transaksi")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "kode_transaksi must be required")
    public String kode_transaksi;

    @FormParam("nilai_invoice")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "nilai_invoice must be required")
    public BigInteger nilai_invoice;

    @FormParam("ppn")
    @PartType(MediaType.TEXT_PLAIN)
    // @NotNull(message = "ppn must be required")
    public BigInteger ppn;

    @FormParam("pph")
    @PartType(MediaType.TEXT_PLAIN)
    // @NotNull(message = "pph must be required")
    public BigInteger pph;

    @FormParam("retensi")
    @PartType(MediaType.TEXT_PLAIN)
    // @NotNull(message = "retensi must be required")
    public BigInteger retensi;

    @FormParam("kasbon")
    @PartType(MediaType.TEXT_PLAIN)
    // @NotNull(message = "kasbon must be required")
    public BigInteger kasbon;


    @FormParam("nomor_invoice")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "nomor invoice must be required")
    public String nomor_invoice;

    @FormParam("kategori")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "kategori must be required")
    public String kategori;


    @FormParam("nama_vendor")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "Nama Vendor must be required")
    public String nama_vendor;

    @FormParam("tanggal_invoice")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "Nama Vendor must be required")
    public LocalDate tanggal_invoice;

    @FormParam("no_po_kontrak")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "Nama Vendor must be required")
    public String no_po_kontrak;

    @FormParam("nilai_invoice_bersih")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "nilai_invoice_bersih must be required")
    public BigInteger nilai_invoice_bersih;

    @FormParam("biaya_potongan_lainnya")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "nilai_invoice_bersih must be required")
    public BigInteger biaya_potongan_lainnya;

    @FormParam("nama_transaksi")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "nama_transaksi wajib ada")
    @Size(min = 1, message = "nama_transaksi minimal 1 item")
    public List<
        @NotBlank(message = "nama_transaksi tidak boleh kosong")
        String
    > nama_transaksi;


    @FormParam("files")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @NotNull(message = "files wajib ada")
    @Size(min = 1, message = "files minimal 1 item")
    public List<
        @NotNull(message = "file tidak boleh null")
        FileUpload
    > files;


    @FormParam("approval")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "approval wajib ada")
    @Size(min = 1, message = "approval minimal 1 item")
    public List<
        @NotBlank(message = "approval tidak boleh kosong")
        String
    > approval;

    @FormParam("urutan")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "urutan wajib ada")
    @Size(min = 1, message = "urutan minimal 1 item")
    public List<
        @NotNull(message = "urutan tidak boleh kosong")
        Integer
    > urutan;
}
