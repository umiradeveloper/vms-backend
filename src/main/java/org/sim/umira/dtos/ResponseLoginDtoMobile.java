package org.sim.umira.dtos;

import java.util.List;

public class ResponseLoginDtoMobile<T> {
    public String token;
    public Object user;
    public List<T> menu;
    public Object employee;
    public Object approvalChecker;
    public Object approvalSigner;
    public ResponseLoginDtoMobile(String token, Object user, List<T> menu, Object employee, Object approvalChecker, Object approvalSigner) {
        this.token = token;
        this.user = user;
        this.menu = menu;
        this.employee = employee;
        this.approvalChecker = approvalChecker;
        this.approvalSigner = approvalSigner;
    }
}
