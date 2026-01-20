package org.sim.umira.dtos;

import java.util.List;

public class ResponseLoginDto<T> {
    public String token;
    public Object user;
    public List<T> menu;
    public ResponseLoginDto(String token, Object user, List<T> menu) {
        this.token = token;
        this.user = user;
        this.menu = menu;
    }

    
}
