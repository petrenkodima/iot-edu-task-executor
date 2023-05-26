package ru.iot.edu.config;

import lombok.Getter;
import org.springframework.stereotype.Component;


@Getter
@Component
public class RaspberryPiConfig {
    //todo read from config
    private final String adr = "192.168.0.100";
    private final String user = "pi";
    private final String pass = "1234";
    private final String scriptsFolder = "execute_scripts";
    private final String baseScriptDir = "/home/pi/" + scriptsFolder;
}
