package it.frafol.cleanping.bungee.hooks;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import it.frafol.cleanping.bungee.CleanPing;
import it.frafol.cleanping.bungee.enums.BungeeConfig;
import it.frafol.cleanping.bungee.enums.BungeeMessages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RedisListener implements Listener {

    public final CleanPing PLUGIN;

    public RedisListener(CleanPing plugin) {
        this.PLUGIN = plugin;
    }

    @EventHandler
    public void onRedisBungeeMessage(@NotNull PubSubMessageEvent event) {

        final RedisBungeeAPI redisBungeeAPI = RedisBungeeAPI.getRedisBungeeApi();
        if (event.getChannel().equals("CleanPing-Request")) {

            final String received_message = event.getMessage();
            final String player_name = received_message.split(";")[0];
            final UUID player_uuid = UUID.fromString(received_message.split(";")[1]);
            final String player_server = received_message.split(";")[2];
            final UUID source = UUID.fromString(received_message.split(";")[3]);

            if (!redisBungeeAPI.getProxyId().equals(player_server)) {
                return;
            }

            if (PLUGIN.getProxy().getPlayer(player_uuid) == null) {
                return;
            }

            final ProxiedPlayer final_player = PLUGIN.getProxy().getPlayer(player_uuid);

            if (final_player == null) {
                return;
            }

            final long ping = final_player.getPing();

            final String response_message = player_name + ";" + player_uuid + ";" + redisBungeeAPI.getProxy(player_uuid) + ";" + source + ";" + ping;
            redisBungeeAPI.sendChannelMessage("CleanPing-Response", response_message);
        }

        if (event.getChannel().equals("CleanPing-Response")) {

            final String received_message = event.getMessage();
            final String player_name = received_message.split(";")[0];
            final String player_server = received_message.split(";")[2];
            final UUID source = UUID.fromString(received_message.split(";")[3]);
            final long ping = Long.parseLong(received_message.split(";")[4]);

            if (redisBungeeAPI.getProxyId().equals(player_server)) {
                return;
            }

            if (PLUGIN.getProxy().getPlayer(source) == null) {
                return;
            }

            if (!(BungeeConfig.DYNAMIC_PING.get(Boolean.class))) {
                PLUGIN.getProxy().getPlayer(source).sendMessage(TextComponent.fromLegacyText(BungeeMessages.OTHERS_PING.color()
                        .replace("%prefix%", BungeeMessages.PREFIX.color())
                        .replace("%user%", player_name)
                        .replace("%ping%", String.valueOf(ping))));
                return;
            }

            PLUGIN.getProxy().getPlayer(source).sendMessage(TextComponent.fromLegacyText(BungeeMessages.OTHERS_PING.color()
                    .replace("%prefix%", BungeeMessages.PREFIX.color())
                    .replace("%user%", player_name)
                    .replace("%ping%", colorBasedOnPing(ping) + ping)));
        }
    }

    private static String colorBasedOnPing(long ping) {
        if (ping < BungeeConfig.MEDIUM_MS.get(Integer.class)) {
            return BungeeConfig.LOW_MS_COLOR.color();
        } else if (ping > BungeeConfig.MEDIUM_MS.get(Integer.class) && ping < BungeeConfig.HIGH_MS.get(Integer.class)) {
            return BungeeConfig.MEDIUM_MS_COLOR.color();
        } else {
            return BungeeConfig.HIGH_MS_COLOR.color();
        }
    }
}
