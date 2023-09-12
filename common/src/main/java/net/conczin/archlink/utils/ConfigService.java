package net.conczin.archlink.utils;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class ConfigService {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ConfigService instance;
    private String webhookUrl;
    private String guildId;
    private Boolean printServerEvents;
    private Boolean printDeathEvents;
    private Boolean printLoginEvents;
    private Boolean printAdvancementEvents;

    private ConfigService() {
        File config = new File("discord.properties");
        Properties prop = new Properties();
        if (!config.exists()) {
            try (OutputStream output = new FileOutputStream(config)) {
                prop.setProperty("discord.webhook", "<url-goes-here>");
                prop.setProperty("discord.guild", "797899107124510731");
                prop.setProperty("printServerEvents", "false");
                prop.setProperty("printDeathEvents", "false");
                prop.setProperty("printLoginEvents", "false");
                prop.setProperty("printAdvancementEvents", "false");
                prop.store(output, "ForgeLink configuration file");
            } catch (IOException e) {
                LOGGER.error("Failed to generate the configuration file");
                LOGGER.error(e.getMessage(), e);
            }
        }
        try (InputStream input = new FileInputStream(config)) {
            prop.load(input);
            this.webhookUrl = prop.getProperty("discord.webhook");
            this.guildId = prop.getProperty("discord.guild");
            this.printServerEvents = Objects.equals(prop.getProperty("printServerEvents"), "true");
            this.printDeathEvents = Objects.equals(prop.getProperty("printDeathEvents"), "true");
            this.printLoginEvents = Objects.equals(prop.getProperty("printLoginEvents"), "true");
            this.printAdvancementEvents = Objects.equals(prop.getProperty("printAdvancementEvents"), "true");
        } catch (IOException e) {
            LOGGER.error("Failed to read properties");
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static ConfigService getInstance() {
        if (instance == null) instance = new ConfigService();
        return instance;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getGuildId() {
        return guildId;
    }

    public Boolean getPrintServerEvents() {
        return printServerEvents;
    }

    public Boolean getPrintDeathEvents() {
        return printDeathEvents;
    }

    public Boolean getPrintLoginEvents() {
        return printLoginEvents;
    }

    public Boolean getPrintAdvancementEvents() {
        return printAdvancementEvents;
    }
}
