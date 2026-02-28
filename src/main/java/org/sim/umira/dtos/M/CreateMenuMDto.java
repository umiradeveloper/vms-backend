package org.sim.umira.dtos.M;

import jakarta.ws.rs.FormParam;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class CreateMenuMDto {
    @FormParam("code_apps")
    public String code_apps;

    @FormParam("code_menu")
    public String code_menu;

    @FormParam("nama_menu")
    public String nama_menu;

    @FormParam("icon_menu")
    public FileUpload icon_menu;

    @FormParam("path_menu")
    public String path_menu;
}
