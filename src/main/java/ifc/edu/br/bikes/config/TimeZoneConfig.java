package ifc.edu.br.bikes.config;

import jakarta.annotation.PostConstruct;

import java.util.TimeZone;

public class TimeZoneConfig {
    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }
}
