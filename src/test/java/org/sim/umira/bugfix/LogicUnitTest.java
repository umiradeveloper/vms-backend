package org.sim.umira.bugfix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.sim.umira.jwt.JwtService;
import org.sim.umira.resources.HumanResources.OvertimeRes;
import org.sim.umira.services.AESUtils;
import org.sim.umira.services.YearCalendarService;

/** Unit test murni (tanpa Quarkus/DB) untuk logika yang diperbaiki. */
class LogicUnitTest {

    private static final int TUTUP_BUKU = 25;

    @Test
    void periodeLemburAkhirDesemberMasukJanuariTahunBerikutnya() {
        YearMonth periode = new OvertimeRes().getPeriode(LocalDate.of(2026, 12, 28), TUTUP_BUKU);
        assertEquals(YearMonth.of(2027, 1), periode);
        // rentang periode harus 26 Des 2026 s/d 25 Jan 2027 (sebelumnya memakai Jan 2026)
        LocalDate start = periode.minusMonths(1).atDay(TUTUP_BUKU + 1);
        LocalDate end = periode.atDay(Math.min(TUTUP_BUKU, periode.lengthOfMonth()));
        assertEquals(LocalDate.of(2026, 12, 26), start);
        assertEquals(LocalDate.of(2027, 1, 25), end);
        assertTrue(!LocalDate.of(2026, 12, 28).isBefore(start) && !LocalDate.of(2026, 12, 28).isAfter(end));
    }

    @Test
    void periodeLemburSebelumTutupBukuTetapBulanSama() {
        assertEquals(YearMonth.of(2026, 10), new OvertimeRes().getPeriode(LocalDate.of(2026, 10, 25), TUTUP_BUKU));
        assertEquals(YearMonth.of(2026, 11), new OvertimeRes().getPeriode(LocalDate.of(2026, 10, 26), TUTUP_BUKU));
    }

    @Test
    void kalenderKerjaSabtuLiburVsMasuk() {
        // 31 Okt 2026 = Sabtu, 1 Nov 2026 = Minggu
        List<YearCalendarService.DayInfo> off = YearCalendarService.generatedDay(2026, Set.of(),
                "2026-10-30", "2026-11-01", true);
        assertEquals("Work", off.get(0).status);
        assertEquals("Weekend", off.get(1).status);
        assertEquals("Weekend", off.get(2).status);
        List<YearCalendarService.DayInfo> on = YearCalendarService.generatedDay(2026, Set.of(),
                "2026-10-30", "2026-11-01", false);
        assertEquals("Work", on.get(1).status);
        assertEquals("Weekend", on.get(2).status);
    }

    @Test
    void liburNasionalDitandaiMerah() {
        List<YearCalendarService.DayInfo> d = YearCalendarService.generatedDay(2026,
                Set.of(LocalDate.of(2026, 12, 25)), "2026-12-25", "2026-12-25", true);
        assertEquals("RED", d.get(0).type);
    }

    @Test
    void tokenResetPasswordBisaDidekripsiKembali() {
        String plain = "user@example.com|2026-10-27T13:00";
        assertEquals(plain, AESUtils.decrypt(AESUtils.encrypt(plain)));
    }

    @Test
    void jwtMenyimpanRoleDanSubject() {
        JwtService js = new JwtService();
        String token = js.generateToken("a@b.com", List.of("Super Admin"), null);
        assertEquals("a@b.com", js.validateToken(token));
        assertEquals(List.of("Super Admin"), js.getRoles(token));
    }

    @Test
    void stringDariDatabaseHarusDibandingkanDenganEquals() {
        // mensimulasikan nilai kode_role dari DB (objek String baru, bukan literal)
        String kodeRoleDariDb = new String("99");
        assertTrue(kodeRoleDariDb != "99"); // inilah sebabnya `== "99"` selalu false
        assertTrue("99".equals(kodeRoleDariDb));
    }
}
