package org.sim.umira.dtos.HumanResources;

import java.time.LocalDate;

public class ResponseAttendanceDto<T> {
    public LocalDate date;
    public String type; // GREEN / GRAY / RED
    public String status;
    public Boolean cuti;
    public T user;
    public ResponseAttendanceDto(LocalDate date, String type, String status, T user, Boolean cuti) {
        this.date = date;
        this.type = type;
        this.status = status;
        this.user = user;
        this.cuti = cuti;
    }

    

}
