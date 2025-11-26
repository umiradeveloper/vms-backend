package org.sim.umira.dtos;

import jakarta.validation.constraints.NotBlank;

public class CreateAppsDto {
    @NotBlank(message = "code_apps is required")
    public String code_apps;
    @NotBlank(message = "name_apps is required")
    public String name_apps;
}
