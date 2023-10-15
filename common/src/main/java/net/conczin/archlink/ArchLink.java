package net.conczin.archlink;

import net.conczin.archlink.dto.DataModel;
import net.conczin.archlink.dto.WebhookModel;
import net.conczin.archlink.utils.ConfigService;
import net.conczin.archlink.utils.TextComponent;
import net.conczin.archlink.utils.WebService;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ArchLink {
    public static final String MOD_ID = "archlink";
    public static final Logger LOGGER = LogManager.getLogger(ArchLink.class.getSimpleName());

    private static final Map<String, DataModel> playerData = new HashMap<>();

    public static void onPlayerLoggedIn(Player player) {
        try {
            WebService client = WebService.getInstance();
            DataModel model = client.getPlayerData(player.getStringUUID());
            playerData.put(player.getStringUUID(), model);
            if (ConfigService.getInstance().getPrintLoginEvents()) {
                sendPlayerWebhook(player, playerCountFormatter(player, true));
            }
            ArchLink.LOGGER.info(player.getName() + " joined as " + model.nickname() + " (ID: " + model.id() + ")");
        } catch (Exception e) {
            RoleAPI.INSTANCE.get(player.getStringUUID().replace("_", ""), r -> {
                ArchLink.LOGGER.info(player.getName() + " tried to join but is not whitelisted by Sam, trying Hagrid...");
                if (r != null && r.isLinked()) {
                    ArchLink.LOGGER.info(player.getName() + " joined via Hagrid");
                } else {
                    ArchLink.LOGGER.error(player.getName() + " login was rejected: " + e.getMessage(), e);
                    ServerPlayer sp = (ServerPlayer) player;
                    sp.connection.disconnect(new TextComponent("You are not whitelisted").getAsComponent());
                }
            });
        }
    }

    public static void onPlayerLoggedOut(Player player) {
        if (ConfigService.getInstance().getPrintLoginEvents() && (playerData.containsKey(player.getStringUUID()))) {
            ArchLink.LOGGER.info("Dispatching leave message");
            sendPlayerWebhook(player, playerCountFormatter(player, false));
            playerData.remove(player.getStringUUID());
        }
    }

    public static void onLivingDeath(LivingEntity entity, DamageSource source) {
        if (ConfigService.getInstance().getPrintDeathEvents() && (entity instanceof Player player && (playerData.containsKey(player.getStringUUID())))) {
            ArchLink.LOGGER.info("Dispatching death message");
            String msg = source.getLocalizedDeathMessage(entity).getString();
            sendPlayerWebhook(player, msg.replace(player.getName().getString() + " ", ""));
        }
    }


    public static void onAdvancement(Advancement advancement, Player player) {
        if (ConfigService.getInstance().getPrintAdvancementEvents() && (playerData.containsKey(player.getStringUUID()))) {
            MinecraftServer server = player.getServer();
            if (server != null
                    && (advancement != null
                    && server.getPlayerList().getPlayerAdvancements((ServerPlayer) player).getOrStartProgress(advancement).isDone()
                    && advancement.getDisplay() != null
                    && advancement.getDisplay().shouldAnnounceChat())) {
                String title = advancement.getDisplay().getTitle().getString();
                String desc = advancement.getDisplay().getDescription().getString();
                ArchLink.LOGGER.info("Dispatching advancement message");
                sendPlayerWebhook(player, "just made the advancement **" + title + "**" + System.lineSeparator() + "*" + desc + "*");
            }
        }
    }

    public static void onServerStarting() {
        if (ConfigService.getInstance().getPrintServerEvents()) {
            sendSystemWebhook("Server started");
        }
    }

    public static void onServerStopping() {
        if (ConfigService.getInstance().getPrintServerEvents()) {
            sendSystemWebhook("Server stopped");
        }
    }

    private static void sendSystemWebhook(String s) {
        WebService.getInstance().sendWebhook(new WebhookModel(
                null, null, s
        ));
    }

    private static void sendPlayerWebhook(Player player, String content) {
        String name = player.getName().getString();
        String uuid = player.getStringUUID();
        String tag = playerData.get(uuid).name();
        WebService service = WebService.getInstance();
        String url = "https://visage.surgeplay.com/face/" + service.cleanUUID(uuid);
        String formattedContent = "**" + name + " (" + tag + ")** " + content;
        service.sendWebhook(new WebhookModel(name, url, formattedContent));
    }

    private static String playerCountFormatter(Player player, boolean join) {
        String base = ((join) ? "joined" : "left") + " the game";
        MinecraftServer server = player.getServer();
        if (server != null) {
            PlayerList list = server.getPlayerList();
            int count = list.getPlayerCount();
            if (!join) count = count - 1;
            if (count == 0) {
                return base + ", server is **empty**";
            }
            return base + ", **" + count + "** out of **" + list.getMaxPlayers() + "** online";
        }
        return base;
    }
}
