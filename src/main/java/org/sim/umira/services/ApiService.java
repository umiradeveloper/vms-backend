package org.sim.umira.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.sim.umira.dtos.Cuti.CreateCutiBulkDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.Cuti.CutiEntity;
import org.sim.umira.entities.Cuti.SaldoCutiEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.MasterCounterCutiEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class ApiService {
     @Transactional
    public void createCutiEmployee(
            EmployeeEntity idEmployee,
            EmployeeEntity idEmployeeApproval,
            String idUser,
            CreateCutiBulkDto create) {

        /*
         * Ambil employee berdasarkan ID
         */
        EmployeeEntity emp =
                EmployeeEntity.findById(idEmployee);

        if (emp == null) {
            throw new BadRequestException(
                    "Employee tidak ditemukan: "
                            + idEmployee
            );
        }

        /*
         * Ambil employee approval
         */
        EmployeeEntity empApproval =
                EmployeeEntity.findById(
                        idEmployeeApproval
                );

        if (empApproval == null) {
            throw new BadRequestException(
                    "Employee approval tidak ditemukan"
            );
        }

        /*
         * ==============================
         * VALIDASI TANGGAL
         * ==============================
         */

        if (create.tanggal_mulai == null) {
            throw new BadRequestException(
                    "Tanggal mulai wajib diisi"
            );
        }

        if (create.tanggal_selesai == null) {
            throw new BadRequestException(
                    "Tanggal selesai wajib diisi"
            );
        }

        if (create.tanggal_mulai
                .isAfter(create.tanggal_selesai)) {

            throw new BadRequestException(
                    "Tanggal mulai tidak boleh lebih besar "
                            + "dari tanggal selesai"
            );
        }

        /*
         * ==============================
         * CEK CUTI APPROVED
         * ==============================
         *
         * Formula overlap:
         *
         * tanggal_mulai <= tanggal_selesai_baru
         * AND
         * tanggal_selesai >= tanggal_mulai_baru
         */

        List<CutiEntity> cutiApproved =
                CutiEntity.find(
                        "employee_pengajuan = ?1 " +
                        "AND tanggal_mulai <= ?2 " +
                        "AND tanggal_selesai >= ?3 " +
                        "AND status_cuti = ?4",

                        emp,

                        create.tanggal_selesai,

                        create.tanggal_mulai,

                        "APPROVED"
                ).list();

        if (!cutiApproved.isEmpty()) {

            throw new BadRequestException(
                    "Tanggal sudah digunakan "
                            + "untuk employee: "
                            + getEmployeeName(emp)
            );
        }

        /*
         * ==============================
         * CEK CUTI PENDING
         * ==============================
         */

        List<CutiEntity> cutiPending =
                CutiEntity.find(
                        "employee_pengajuan = ?1 " +
                        "AND tanggal_mulai <= ?2 " +
                        "AND tanggal_selesai >= ?3 " +
                        "AND status_cuti = ?4",

                        emp,

                        create.tanggal_selesai,

                        create.tanggal_mulai,

                        "PENDING"
                ).list();

        if (!cutiPending.isEmpty()) {

            throw new BadRequestException(
                    "Sedang proses pengajuan cuti "
                            + "untuk employee: "
                            + getEmployeeName(emp)
            );
        }

        /*
         * ==============================
         * ANNUAL LEAVE
         * ==============================
         */

        if ("ANNUAL_LEAVE".equals(
                create.jenis_cuti)) {

            checkAnnualLeaveBalance(
                    emp,
                    create
            );
        }

        /*
         * ==============================
         * GENERATE ID CUTI
         * ==============================
         */

        String idCuti =
                generateIdCuti(
                        create.kode_cuti
                );

        /*
         * ==============================
         * CREATE CUTI
         * ==============================
         */

        CutiEntity cuti =
                new CutiEntity();

        cuti.employee_pengajuan = emp;

        cuti.jenis_cuti =
                create.jenis_cuti;

        cuti.tanggal_mulai =
                create.tanggal_mulai;

        cuti.tanggal_selesai =
                create.tanggal_selesai;

        cuti.status_cuti =
                "APPROVED";

        cuti.created_at =
                LocalDateTime.now();

        cuti.created_by =
                idUser;

        cuti.kode_cuti =
                idCuti;

        cuti.employee_approval =
                empApproval;

        cuti.tanggal_approval =
                LocalDateTime.now();

        /*
         * Kalau manager memang sama dengan approval,
         * gunakan empApproval.
         *
         * Kalau manager berbeda, ganti dengan
         * employee manager yang sebenarnya.
         */
        cuti.employee_manager =
                empApproval;

        cuti.tanggal_manager =
                LocalDateTime.now();

        /*
         * ==============================
         * SAVE
         * ==============================
         */

        cuti.persist();
    }

    /*
     * ==================================================
     * CEK SALDO CUTI
     * ==================================================
     */

    private void checkAnnualLeaveBalance(
            EmployeeEntity emp,
            CreateCutiBulkDto create) {

        /*
         * Sesuaikan bagian ini dengan relasi
         * EmployeeEntity -> UserEntity Anda.
         *
         * Misalnya:
         *
         * emp.user.id_user
         */

        String userId =
                emp.user.id_user;

        int tahun =
                create.tanggal_mulai.getYear();

        SaldoCutiEntity balance =
                SaldoCutiEntity.findByUserAndTahun(
                        userId,
                        tahun
                );

        /*
         * Jangan otomatis membuat saldo 12 di sini
         * kalau ini adalah proses approval/bulk.
         *
         * Lebih aman saldo harus sudah tersedia.
         */

        if (balance == null) {

            throw new BadRequestException(
                    "Saldo cuti belum tersedia untuk "
                            + getEmployeeName(emp)
            );
        }

        /*
         * Hitung hari kerja
         */

        long totalDays =
                create.tanggal_mulai
                        .datesUntil(
                                create.tanggal_selesai
                                        .plusDays(1)
                        )
                        .filter(d ->
                                d.getDayOfWeek()
                                        != DayOfWeek.SATURDAY
                                        &&
                                d.getDayOfWeek()
                                        != DayOfWeek.SUNDAY
                        )
                        .count();

        /*
         * Validasi saldo
         */

        if (balance.sisa_cuti < totalDays) {

            throw new BadRequestException(
                    "Sisa cuti tidak mencukupi "
                            + "untuk "
                            + getEmployeeName(emp)
                            + ". Sisa: "
                            + balance.sisa_cuti
                            + " hari, Dibutuhkan: "
                            + totalDays
                            + " hari"
            );
        }
    }

    /*
     * ==================================================
     * GENERATE ID CUTI
     * ==================================================
     */

    @Transactional
    public String generateIdCuti(
            String kodeCuti) {

        String years =
                String.valueOf(
                        LocalDate.now().getYear()
                );

        MasterCounterCutiEntity counter =
                MasterCounterCutiEntity.find(
                        "year = ?1 AND jenis_cuti = ?2",
                        years,
                        kodeCuti
                ).firstResult();

        if (counter == null) {

            counter =
                    new MasterCounterCutiEntity();

            counter.jenis_cuti =
                    kodeCuti;

            counter.year =
                    years;

            counter.counter =
                    1;

            counter.persist();

            return kodeCuti
                    + "-"
                    + years
                    + String.format(
                            "%05d",
                            counter.counter
                    );
        }

        /*
         * IMPORTANT:
         *
         * Jangan:
         *
         * counter.counter++;
         * counter.counter + 1
         *
         * karena akan lompat nomor.
         */

        counter.counter++;

        return kodeCuti
                + "-"
                + years
                + String.format(
                        "%05d",
                        counter.counter
                );
    }

    /*
     * ==================================================
     * EMPLOYEE NAME
     * ==================================================
     */

    private String getEmployeeName(
            EmployeeEntity emp) {

        if (emp == null) {
            return "-";
        }

        /*
         * Sesuaikan dengan field entity Anda.
         *
         * Misalnya emp.nama
         */

        return emp.nama != null
                ? emp.nama
                : String.valueOf(
                        emp.id_employee
                );
    }

}
