package org.sim.umira.dtos;

import jakarta.validation.constraints.NotBlank;

public class CreateMenuDto {
    @NotBlank(message = "code_apps is required")
    public String code_apps;

    @NotBlank(message = "code_menu is required")
    public String code_menu;

    // @NotBlank(message = "nama_menu is required")
    public String nama_menu;

    // @NotBlank(message = "menu_title is required")
    public String menu_title;

    public String icon_menu;

    public String type_menu;

    public String active_menu;

    public String selected_menu;

    public String dircange_menu;

    public String path_menu;
   
}
