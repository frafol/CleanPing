package it.frafol.cleanping.velocity.hooks;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import it.frafol.cleanping.velocity.CleanPing;
import it.frafol.cleanping.velocity.enums.VelocityConfig;
import it.frafol.cleanping.velocity.enums.VelocityMessages;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class RedisListener {

    public final CleanPing PLUGIN;

    public RedisListener(CleanPing plugin) {
        this.PLUGIN = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
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

            if (!PLUGIN.getServer().getPlayer(player_uuid).isPresent()) {
                return;
            }

            final Optional<Player> final_player = PLUGIN.getServer().getPlayer(player_uuid);

            if (!final_player.isPresent()) {
                return;
            }

            final long ping = final_player.get().getPing();

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

            if (!PLUGIN.getServer().getPlayer(source).isPresent()) {
                return;
            }

            if (!(VelocityConfig.DYNAMIC_PING.get(Boolean.class))) {

                PLUGIN.getServer().getPlayer(source).get().sendMessage(LegacyComponentSerializer.legacy('§').deserialize(VelocityMessages.OTHERS_PING.color()
                        .replace("%prefix%", VelocityMessages.PREFIX.color())
                        .replace("%user%", player_name)
                        .replace("%ping%", String.valueOf(ping))));

                return;

            }
            PLUGIN.getServer().getPlayer(source).get().sendMessage(LegacyComponentSerializer.legacy('§').deserialize(VelocityMessages.OTHERS_PING.color()
                    .replace("%prefix%", VelocityMessages.PREFIX.color())
                    .replace("%user%", player_name)
                    .replace("%ping%", colorBasedOnPing(ping) + ping)));
        }
    }

    private static String colorBasedOnPing(long ping) {
        if (ping < VelocityConfig.MEDIUM_MS.get(Integer.class)) {
            return VelocityConfig.LOW_MS_COLOR.color();
        } else if (ping > VelocityConfig.MEDIUM_MS.get(Integer.class) && ping < VelocityConfig.HIGH_MS.get(Integer.class)) {
            return VelocityConfig.MEDIUM_MS_COLOR.color();
        } else {
            return VelocityConfig.HIGH_MS_COLOR.color();
        }
    }
}
