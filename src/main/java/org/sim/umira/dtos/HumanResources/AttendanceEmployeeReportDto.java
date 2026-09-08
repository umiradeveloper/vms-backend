package org.sim.umira.dtos.HumanResources;

import java.util.Map;

public class AttendanceEmployeeReportDto {
    public String employeeId;
    public String employeeName;
    public Map<String, Object> attendance;

    public AttendanceEmployeeReportDto(
            String employeeId,
            String employeeName,
            Map<String, Object> attendance
    ) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.attendance = attendance;
    }
}
