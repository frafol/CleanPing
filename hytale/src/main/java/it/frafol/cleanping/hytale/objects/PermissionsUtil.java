package it.frafol.cleanping.hytale.objects;

import com.fancyinnovations.fancycore.api.FancyCore;
import com.fancyinnovations.fancycore.api.player.FancyPlayer;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import it.ethereallabs.etherealperms.EtherealPerms;
import it.ethereallabs.etherealperms.permissions.PermissionManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.UUID;

public class PermissionsUtil {

    public static boolean hasPermission(UUID uuid, String permission) {
        if (PermissionsModule.get().hasPermission(uuid, permission)) return true;
        if (isLuckPerms()) return hasLuckPermission(uuid, permission);
        if (isFancyCore()) return hasFancyPermission(uuid, permission);
        if (isEtherealPerms()) return hasEtherealPermission(uuid, permission);
        return PermissionsModule.get().hasPermission(uuid, permission);
    }

    private static boolean hasLuckPermission(UUID uuid, String permission) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) return false;
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    private static boolean hasFancyPermission(UUID uuid, String permission) {
        FancyCore fancyCore = FancyCore.get();
        FancyPlayer fancyPlayer = fancyCore.getPlayerService().getByUUID(uuid);
        if (fancyPlayer == null) return false;
        return fancyPlayer.checkPermission(permission);
    }

    private static boolean hasEtherealPermission(UUID uuid, String permission) {
        PermissionManager permissionManager = EtherealPerms.Companion.getPermissionManager();
        it.ethereallabs.etherealperms.permissions.models.User user = permissionManager.getUser(uuid);
        if (user == null) return false;
        if (permissionManager.getEffectivePermissions(user).get(permission) != null) {
            return permissionManager.getEffectivePermissions(user).get(permission);
        }
        return false;
    }

    private static boolean isLuckPerms() {
        return HytaleServer.get().getPluginManager().hasPlugin(PluginIdentifier.fromString("LuckPerms:LuckPerms"), SemverRange.fromString("*"));
    }

    private static boolean isFancyCore() {
        return HytaleServer.get().getPluginManager().hasPlugin(PluginIdentifier.fromString("FancyInnovations:FancyCore"), SemverRange.fromString("*"));
    }

    private static boolean isEtherealPerms() {
        return HytaleServer.get().getPluginManager().hasPlugin(PluginIdentifier.fromString("EtherealLabs:EtherealPerms"), SemverRange.fromString("*"));
    }
}
