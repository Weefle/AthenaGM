package io.github.redwallhp.athenagm.hub;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.regions.CuboidRegion;
import org.bukkit.GameMode;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

public class HubListener implements Listener {


    private AthenaGM plugin;
    private Hub hub;


    public HubListener(AthenaGM plugin, Hub hub) {
        this.plugin = plugin;
        this.hub = hub;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /**
     * Spawn the player when they join the server
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        hub.spawnPlayer(event.getPlayer());
    }


    /**
     * Ensure that a player coming into the Hub from another world always gets reset properly
     */
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (hub.hasPlayer(event.getPlayer())) {
            hub.playerSetUp(event.getPlayer());
        }
    }


    /**
     * If a player manages to die in the Hub, ensure they respawn properly
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (hub.hasPlayer(event.getPlayer())) {
            event.setRespawnLocation(hub.getWorld().getSpawnLocation());
            hub.playerSetUp(event.getPlayer());
        }
    }


    /**
     * Handle player death in the Hub
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (hub.hasPlayer(event.getEntity())) {
            event.getDrops().clear();
        }
    }


    /**
     * Stop hunger depletion on the Hub
     */
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && hub.hasPlayer((Player)event.getEntity())) {
            event.setCancelled(true);
        }
    }

    /**
     * Stop the player from dropping items in the Hub
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (hub.hasPlayer(event.getPlayer())) {
            if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
            if (event.getPlayer().getOpenInventory() != null) {
                event.setCancelled(true);
            } else {
                event.getPlayer().setItemInHand(event.getItemDrop().getItemStack());
                event.getItemDrop().remove();
            }
        }
    }


    /**
     * Block portal events for Hub portals
     */
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (hub.hasPlayer(event.getPlayer())) {
            CuboidRegion fromRegion = plugin.getRegionHandler().getApplicableRegion(event.getFrom());
            if (fromRegion != null && fromRegion.getFlagValue("join_arena") != null) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Take the player to the appropriate Arena when they interact with a HubSign
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!hub.hasPlayer(event.getPlayer())) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;
        Vector vector = event.getClickedBlock().getLocation().toVector();
        for (HubSign sign : hub.getConfig().getSigns()) {
            if (sign.getVector().equals(vector)) {
                sign.warp(event.getPlayer());
                return;
            }
        }
    }


    /**
     * Stop players from removing items from item frames
     */
    @EventHandler
    public void onPlayerPunchItemFrame(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        if (!hub.hasPlayer(player)) return;
        if (event.getEntity().getType().equals(EntityType.ITEM_FRAME) && player.getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }


    /**
     * Stop players from rotating item frames on the hub
     */
    @EventHandler
    public void onPlayerRotateItemFrame(PlayerInteractEntityEvent event) {
        if (!hub.hasPlayer(event.getPlayer())) return;
        if (event.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                event.setCancelled(true);
            }
        }
    }


}
