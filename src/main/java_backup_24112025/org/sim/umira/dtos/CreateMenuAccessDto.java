package org.sim.umira.dtos;

import jakarta.validation.constraints.NotBlank;

public class CreateMenuAccessDto {
    @NotBlank(message = "code_apps is required")
    public String code_apps;

    @NotBlank(message = "code_menu is required")
    public String code_menu;

    @NotBlank(message = "id_role is required")
    public String id_role;


}
