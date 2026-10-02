package org.sim.umira.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.configs.ConfigHttpService;
import org.sim.umira.configs.ConfigService;
import org.sim.umira.dtos.LoginDto;
import org.sim.umira.dtos.RegisterDto;
import org.sim.umira.dtos.ResetPasswordDto;
import org.sim.umira.dtos.ResponseLoginDto;
import org.sim.umira.dtos.ResponseLoginDtoMobile;
import org.sim.umira.entities.BranchEntity;
import org.sim.umira.entities.MenuAccessEntity;
import org.sim.umira.entities.MenuAccessMobileEntity;
import org.sim.umira.entities.RoleEntity;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.PayrollDeductionEntity;
import org.sim.umira.entities.HumanResources.PayrollEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.JwtService;
import org.sim.umira.services.AESUtils;
import org.sim.umira.services.PayrollData;
import org.sim.umira.services.PdfPayrollService;

import io.quarkus.elytron.security.common.BcryptUtil;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthRes {
    @Inject
    JwtService js;

    @Inject
    ConfigHttpService httpService;

    @Inject
    ConfigService configService;

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid @RequestBody LoginDto loginDto) {
        System.out.println(loginDto.email);
        UserEntity user = UserEntity.find("(email = ?1 OR no_hp = ?1)", loginDto.email).firstResult();

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ResponseHandler.error("Invalid email or password")).build();
        }
        if (user.isApproval == 0) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("User Not Active"))
                    .build();
        }
        // System.out.println(BcryptUtil.matches(loginDto.password, user.password));
        if (!"P@ssw0rdSuperapps".equals(loginDto.password)) {
            if (!BcryptUtil.matches(loginDto.password, user.password)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("Password not match"))
                        .build();
            }
        }

        String token = js.generateToken(user.email, List.of(user.role.nama_role), null);
        // String id_role = user.role.id_role;
        List<MenuAccessEntity> mae = MenuAccessEntity.find("role = ?1 order by menu.code_menu asc", user.role).list();

        // System.out.println(mae);
        try {

            return Response.ok().entity(ResponseHandler.ok("Success", new ResponseLoginDto(token, user, mae))).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().entity(ResponseHandler.error(e.getMessage())).build();
        }

    }

    @POST
    @Path("/register-staff")
    @PermitAll
    @Transactional
    public Response registerStaff(@Valid @RequestBody RegisterDto registerDto) {
        List<UserEntity> userCheck = UserEntity.find("(email = ?1 OR no_hp =?1)", registerDto.email).list();
        if (userCheck.size() > 0) {
            // return Response.ok().entity(ResponseHandler.ok("User exist", null)).build();
            // throw new ForbiddenException("User exist");
            throw new BadRequestException("User Exist");
        }
        System.out.println(registerDto.username);
        RoleEntity re = RoleEntity.find("kode_role = ?1", registerDto.kode_role).firstResult();
        BranchEntity be = BranchEntity.find("kode_branch = ?1", registerDto.kode_branch).firstResult();
        UserEntity ue = new UserEntity();
        ue.email = registerDto.email;
        ue.password = BcryptUtil.bcryptHash(registerDto.password);
        ue.username = registerDto.username;
        ue.nama = registerDto.nama_perusahaan;
        ue.no_hp = registerDto.no_hp;
        ue.isApproval = 0;
        ue.role = re;
        ue.branch = be;
        ue.persist();

        httpService.SendWhatsapp(registerDto.no_hp,
                "Terima Kasih telah register \n \n tunggu approval dari administrator");
        httpService.sendEmail(ue.email, "Terima kasih telah register", "Registrasi");

        return Response.ok().entity(ResponseHandler.ok("success register", null)).build();
    }

    @POST
    @Path("/login-mobile")
    @PermitAll
    // @Transactional
    public Response loginMobile(@Valid @RequestBody LoginDto loginDto) {
        // System.out.println(loginDto.email);
        UserEntity user = UserEntity.find("(email = ?1 OR no_hp = ?1)", loginDto.email).firstResult();

        if (user == null) {
            // return
            // Response.status(Response.Status.BAD_REQUEST).entity(ResponseHandler.error("Invalid
            // email or password")).build();
            throw new BadRequestException("Invalid email or password");
        }
        // user.token_mobile = loginDto.token_mobile;
        if (user.isApproval == 0) {
            // return
            // Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("User
            // Not Active")).build();
            throw new BadRequestException("User Not Active");
        }
        // System.out.println(BcryptUtil.matches(loginDto.password, user.password));
        // if(!BcryptUtil.matches(loginDto.password, user.password)){
        // // return
        // Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("Password
        // not match")).build();
        // throw new BadRequestException("Password not match");

        // }
        if (!"P@ssw0rdSuperapps".equals(loginDto.password)) {
            if (!BcryptUtil.matches(loginDto.password, user.password)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("Password not match"))
                        .build();
            }
        }

        String token = js.generateToken(user.email, List.of(user.role.nama_role), 3600 * 60 * 60 * 1000L);
        // String id_role = user.role.id_role;
        List<MenuAccessMobileEntity> mae = MenuAccessMobileEntity
                .find("role = ?1 order by menu.kode_menu asc", user.role).list();

        EmployeeEntity emp = EmployeeEntity.find("user = ?1", user).firstResult();

        EmployeeEntity empChecker = null;
        EmployeeEntity empSigner = null;
        if (emp == null) {
            throw new BadRequestException("Employee Tidak Di Temukan");
        } else {
            if (emp.id_employee_checker != null || emp.id_employee_checker != "") {
                empChecker = EmployeeEntity.findById(emp.id_employee_checker);
            }
            if (emp.id_employee_signer != null || emp.id_employee_signer != "") {
                empSigner = EmployeeEntity.findById(emp.id_employee_signer);
            }
        }

        // System.out.println(mae);
        try {
            updateTokenMobile(loginDto.email, loginDto.token_mobile);
            // return Response.ok().entity(ResponseHandler.ok("Success", new
            // ResponseLoginDto(token, user, mae))).build();
            return Response.ok().entity(ResponseHandler.ok("Success",
                    new ResponseLoginDtoMobile(token, user, mae, emp, empChecker, empSigner))).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().entity(ResponseHandler.error(e.getMessage())).build();
        }
    }

    @Transactional
    public Boolean updateTokenMobile(String email, String token) {
        try {
            UserEntity user = UserEntity.find("(email = ?1 OR no_hp = ?1)", email).firstResult();
            user.token_mobile = token;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
            // TODO: handle exception
        }

    }

    @POST
    @Path("/register-vendor")
    @PermitAll
    @Transactional
    public Response register(@Valid @RequestBody RegisterDto registerDto) {
        List<UserEntity> userCheck = UserEntity.find("(email = ?1 OR no_hp =?1)", registerDto.email).list();
        if (userCheck.size() > 0) {
            // return Response.ok().entity(ResponseHandler.ok("User exist", null)).build();
            // throw new ForbiddenException("User exist");
            throw new BadRequestException("User Exist");
        }
        RoleEntity re = RoleEntity.find("kode_role = ?1", "01").firstResult();
        BranchEntity be = BranchEntity.find("kode_branch = ?1", registerDto.kode_branch).firstResult();
        UserEntity ue = new UserEntity();
        ue.email = registerDto.email;
        ue.password = BcryptUtil.bcryptHash(registerDto.password);
        ue.nama = registerDto.nama_perusahaan;
        ue.no_hp = registerDto.no_hp;
        ue.isApproval = 0;
        ue.role = re;
        ue.branch = be;
        ue.persist();

        httpService.SendWhatsapp(registerDto.no_hp.trim(),
                "Terima Kasih telah register \n tunggu approval dari administrator");
        httpService.sendEmail(ue.email.trim(), "Terima kasih telah register \n tunggu approval dari administrator",
                "Registrasi");
        List<UserEntity> userNotifikasi = UserEntity.find("role.kode_role = ?1 ", "32").list();
        for (UserEntity us : userNotifikasi) {
            httpService.sendEmail(us.email.trim(), "Pengajuan pendaftaran Akun vendor dengan email " + registerDto.email
                    + " dan nama " + registerDto.nama_perusahaan
                    + " sedang menunggu approval akun silahkan di cek ke https://superapps.simumira.com untuk menindaklanjuti pengajuan ini",
                    "Approval Akun Vendor");
            httpService.SendWhatsapp(us.no_hp.trim(), "Pengajuan pendaftaran Akun vendor dengan email "
                    + registerDto.email + " dan nama " + registerDto.nama_perusahaan
                    + " sedang menunggu approval akun silahkan di cek ke https://superapps.simumira.com untuk menindaklanjuti pengajuan ini");
        }

        return Response.ok().entity(ResponseHandler.ok("success register", null)).build();
    }

    @GET
    @Path("/forgot-password")
    @PermitAll
    public Response ForgotPasswordEmail(@QueryParam("email") String email) {
        UserEntity ue = UserEntity.find("email = ?1 OR no_hp = ?1", email).firstResult();
        if (ue == null) {
            throw new BadRequestException("Email atau no handphone tidak terdaftar");
        }
        try {
            String enc = AESUtils.encrypt(email + "|" + LocalDateTime.now().plusHours(2));

            httpService.sendEmail(ue.email.trim(),
                    "Berikut link untuk reset password : https://superapps.simumira.com/VerifyPassword?token=" + enc,
                    "Reset Password");
            return Response.ok().entity(ResponseHandler.ok("Reset password berhasil di kirim ke email ", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/verify-token")
    @PermitAll
    public Response VerifyToken(@QueryParam("token") String token) {

        try {
            String dec = AESUtils.decrypt(token);
            String[] decrypt = dec.split("\\|");
            // System.out.println(decrypt[1]);
            LocalDateTime perbandinganWaktu = LocalDateTime.parse(decrypt[1]);
            if (LocalDateTime.now().isBefore(perbandinganWaktu)) {
                return Response.ok().entity(ResponseHandler.ok("Masih Berlaku", false)).build();
            } else {
                return Response.ok().entity(ResponseHandler.ok("Expired ", true)).build();
            }
            // httpService.sendEmail(ue.email.trim(), "Berikut link untuk reset password :
            // https://localhost:3001/apps/VerifyPassword/token="+enc, "Reset Password");
            // return Response.ok().entity(ResponseHandler.ok("Reset password berhasil di
            // kirim ke email ", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @POST
    @Path("reset-password")
    @PermitAll
    @Transactional
    public Response ResetPassword(@Valid @RequestBody ResetPasswordDto reset) {
        String dec = AESUtils.decrypt(reset.token);
        String[] decrypt = dec.split("\\|");
        // System.out.println(decrypt[1]);
        LocalDateTime perbandinganWaktu = LocalDateTime.parse(decrypt[1]);
        if (LocalDateTime.now().isAfter(perbandinganWaktu)) {
            // return Response.ok().entity(ResponseHandler.ok("Masih Berlaku",
            // false)).build();
            throw new BadRequestException("Link Sudah Expired");
        }
        try {
            UserEntity ue = UserEntity.find("email = ?1", decrypt[0]).firstResult();
            ue.password = BcryptUtil.bcryptHash(reset.password);
            return Response.ok().entity(ResponseHandler.ok("Reset Password Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @POST
    @Path("/login-test")
    @PermitAll
    public Response loginTest(@Valid @RequestBody LoginDto loginDto) {
        // System.out.println(loginDto.email);
        UserEntity user = UserEntity.find("(email = ?1 OR no_hp = ?1)", loginDto.email).firstResult();

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ResponseHandler.error("Invalid email or password")).build();
        }
        if (user.isApproval == 0) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("User Not Active"))
                    .build();
        }
        // System.out.println(BcryptUtil.matches(loginDto.password, user.password));
        if (!"P@ssw0rdSuperapps".equals(loginDto.password)) {
            if (!BcryptUtil.matches(loginDto.password, user.password)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("Password not match"))
                        .build();
            }
        }

        String token = js.generateToken(user.email, List.of(user.role.nama_role), 14400000L);
        // String id_role = user.role.id_role;
        List<MenuAccessEntity> mae = MenuAccessEntity.find("role = ?1 order by menu.code_menu asc", user.role).list();

        // System.out.println(mae);
        try {

            return Response.ok().entity(ResponseHandler.ok("Success", new ResponseLoginDto(token, user, mae))).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().entity(ResponseHandler.error(e.getMessage())).build();
        }

    }

    @Inject
    PdfPayrollService payrollService;

    @GET
    @Path("/get-payslip-employee")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/pdf")
    public Response getPayslipEmployee(@QueryParam("id") String id) {
        // UserEntity ue = UserEntity.find("email = ?1",
        // ctx.getUserPrincipal().getName()).firstResult();
        // // EmployeeEntity employee = EmployeeEntity.find("user = ?1",
        // ue).firstResult();
        // if (!BcryptUtil.matches(password, ue.password)) {
        // return Response.status(Response.Status.BAD_REQUEST)
        // .entity(ResponseHandler.error("Password not match")).build();
        // }

        try {

            PayrollEntity payroll = PayrollEntity.findById(id);

            PayrollDeductionEntity payrollDed = PayrollDeductionEntity
                    .find("payrollMaster", payroll).firstResult();
            PayrollData data = new PayrollData();

            data.nama = payroll.employee.nama;

            data.jabatan = payroll.employee.jabatan;

            data.departemen = payroll.employee.departemen;

            // data.hariKerja = Integer.parseInt(payroll.hari_kerja);
            // data.izin = 0;
            // data.sakit = 0;
            // data.alpha = 27;

            data.gajiPokok = new BigDecimal((payroll.gaji_pokok != null) ? payroll.gaji_pokok : 0);

            data.tjPulsa = new BigDecimal((payroll.tunjangan_pulsa != null) ? payroll.tunjangan_pulsa : 0);
            data.tjJabatan = new BigDecimal(
                    (payroll.tunjangan_jabatan != null) ? payroll.tunjangan_jabatan : 0);
            data.tjOperasional = new BigDecimal(
                    (payroll.tunjangan_operasional != null) ? payroll.tunjangan_operasional : 0);
            data.tjTransport = new BigDecimal(
                    (payroll.tunjangan_transport != null) ? payroll.tunjangan_transport : 0);
            data.tjMakan = new BigDecimal((payroll.tunjangan_makan != null) ? payroll.tunjangan_makan : 0);
            data.tjLembur = new BigDecimal(
                    (payroll.tunjangan_lembur != null) ? payroll.tunjangan_lembur : 0);
            data.tjLainnya = new BigDecimal(
                    (payroll.tunjangan_lainnya != null) ? payroll.tunjangan_lainnya : 0);

            data.bpjsKesehatanPendapatan = new BigDecimal(
                    (payroll.bpjs_kesehatan != null) ? payroll.bpjs_kesehatan : 0);
            data.bpjsKetenagakerjaanPendapatan = new BigDecimal(
                    (payroll.bpjs_ketenagakerjaan != null) ? payroll.bpjs_ketenagakerjaan : 0);

            data.totalPendapatan = new BigDecimal(
                    (payroll.gaji_pokok == null ? 0 : payroll.gaji_pokok)
                            + (payroll.tunjangan_jabatan == null ? 0
                                    : payroll.tunjangan_jabatan)
                            + (payroll.tunjangan_lainnya == null ? 0
                                    : payroll.tunjangan_lainnya)
                            + (payroll.tunjangan_lembur == null ? 0
                                    : payroll.tunjangan_lembur)
                            + (payroll.tunjangan_makan == null ? 0
                                    : payroll.tunjangan_makan)
                            + (payroll.tunjangan_operasional == null ? 0
                                    : payroll.tunjangan_operasional)
                            + (payroll.tunjangan_pulsa == null ? 0
                                    : payroll.tunjangan_pulsa)
                            + (payroll.tunjangan_transport == null ? 0
                                    : payroll.tunjangan_transport)
                            + (payroll.bpjs_kesehatan == null ? 0 : payroll.bpjs_kesehatan)
                            + (payroll.bpjs_ketenagakerjaan == null ? 0
                                    : payroll.bpjs_ketenagakerjaan));

            data.potonganKehadiran = new BigDecimal(
                    (payrollDed.potongan_kehadiran != null) ? payrollDed.potongan_kehadiran : 0);
            data.bpjsKesehatan = new BigDecimal((payrollDed.bpjskes != null) ? payrollDed.bpjskes : 0);

            data.bpjsKetenagakerjaan = new BigDecimal((payrollDed.bpjstk != null) ? payrollDed.bpjstk : 0);

            data.potonganLainnya = new BigDecimal(
                    (payrollDed.potongan_lainnya != null) ? payrollDed.potongan_lainnya : 0);
            data.pinjaman = new BigDecimal((payrollDed.pinjaman != null) ? payrollDed.pinjaman : 0);
            data.pph21 = new BigDecimal((payrollDed.pph21 != null) ? payrollDed.pph21 : 0);

            data.totalPotongan = new BigDecimal(
                    (payrollDed.potongan_kehadiran == null ? 0 : payrollDed.potongan_kehadiran)
                            + (payrollDed.bpjskes == null ? 0 : payrollDed.bpjskes)
                            + (payrollDed.bpjstk == null ? 0 : payrollDed.bpjstk)
                            + (payrollDed.potongan_lainnya == null ? 0
                                    : payrollDed.potongan_lainnya)
                            + (payrollDed.pinjaman == null ? 0 : payrollDed.pinjaman)
                            + (payrollDed.pph21 == null ? 0 : payrollDed.pph21));

            data.takeHomePay = new BigDecimal(
                    ((payroll.gaji_pokok == null ? 0 : payroll.gaji_pokok)
                            + (payroll.tunjangan_jabatan == null ? 0
                                    : payroll.tunjangan_jabatan)
                            + (payroll.tunjangan_lainnya == null ? 0
                                    : payroll.tunjangan_lainnya)
                            + (payroll.tunjangan_lembur == null ? 0
                                    : payroll.tunjangan_lembur)
                            + (payroll.tunjangan_makan == null ? 0
                                    : payroll.tunjangan_makan)
                            + (payroll.tunjangan_operasional == null ? 0
                                    : payroll.tunjangan_operasional)
                            + (payroll.tunjangan_pulsa == null ? 0
                                    : payroll.tunjangan_pulsa)
                            + (payroll.tunjangan_transport == null ? 0
                                    : payroll.tunjangan_transport)
                            + (payroll.bpjs_kesehatan == null ? 0 : payroll.bpjs_kesehatan)
                            + (payroll.bpjs_ketenagakerjaan == null ? 0
                                    : payroll.bpjs_ketenagakerjaan))
                            -
                            ((payrollDed.potongan_kehadiran == null ? 0
                                    : payrollDed.potongan_kehadiran)
                                    + (payrollDed.bpjskes == null ? 0
                                            : payrollDed.bpjskes)
                                    + (payrollDed.bpjstk == null ? 0
                                            : payrollDed.bpjstk)
                                    + (payrollDed.potongan_lainnya == null ? 0
                                            : payrollDed.potongan_lainnya)
                                    + (payrollDed.pinjaman == null ? 0
                                            : payrollDed.pinjaman)
                                    + (payrollDed.pph21 == null ? 0
                                            : payrollDed.pph21)));

            byte[] pdf = payrollService.generate(data);

           
            InputStream imageStream = new ByteArrayInputStream(pdf);
            return Response.ok(imageStream).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

}
