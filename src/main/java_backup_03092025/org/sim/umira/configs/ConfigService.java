package org.sim.umira.configs;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;

// @ApplicationScoped
@Singleton
public class ConfigService {


    @ConfigProperty(name = "vms.secretKey")
    public String secretKey;


    @ConfigProperty(name = "vms.apiKeyWhatsapp")
    public String apiWa;

    @ConfigProperty(name = "vms.accountKeyWhatsapp")
    public String accountWa;

    @ConfigProperty(name = "vms.urlWhatsapp")
    public String urlWa;

    // public ConfigService() {
    // }

    
}
