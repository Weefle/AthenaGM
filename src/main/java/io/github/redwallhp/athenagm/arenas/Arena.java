package io.github.redwallhp.athenagm.arenas;


import io.github.redwallhp.athenagm.AthenaGM;
import io.github.redwallhp.athenagm.configuration.ConfiguredArena;
import io.github.redwallhp.athenagm.maps.MapLoader;
import io.github.redwallhp.athenagm.maps.Rotation;
import io.github.redwallhp.athenagm.matches.Match;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

public class Arena {

    private ArenaHandler handler;
    private AthenaGM plugin;
    private String id;
    private String name;
    private String gameMode;
    private Integer maxPlayers;
    private Integer timeLimit;
    private List<String> mapList;
    private Rotation rotation;
    private MapLoader mapLoader;
    private WeakReference<World> world;
    private File worldFile;
    private Match match;


    public Arena(ArenaHandler handler, ConfiguredArena configuredArena) {

        this.handler = handler;
        this.plugin = handler.getPluginInstance();
        this.id = configuredArena.getId();
        this.name = configuredArena.getName();
        this.gameMode = configuredArena.getGameMode();
        this.maxPlayers = configuredArena.getMaxPlayers();
        this.timeLimit = configuredArena.getTimeLimit();
        this.mapList = configuredArena.getMapList();

        this.rotation = new Rotation(plugin, this.mapList);
        this.mapLoader = new MapLoader(rotation.getCurrentMap(), this);

        Bukkit.getScheduler().runTask(this.plugin, new Runnable() {
            public void run() {
                startNewMatch();
            }
        });

    }


    public void startNewMatch() {
        if (rotation.getNextMap().equals(mapLoader.getMap())) {
            rotation.advance();
        }
        World oldWorld = (this.world == null) ? null : getWorld();
        File oldFile = (this.worldFile == null) ? null : getWorldFile();
        mapLoader.load();
        this.match = new Match(this, this.mapLoader.getUUID(), this.mapLoader.getMap());
        //todo: throw match start/end events so game plugins can do setup/cleanup (MatchCreateEvent)
        mapLoader = new MapLoader(rotation.getNextMap(), this);
        if (oldWorld != null) {
            Bukkit.unloadWorld(oldWorld, false);
            mapLoader.destroyWorldInstanceCopy(oldFile);
        }
    }


    public ArenaHandler getHandler() {
        return this.handler;
    }

    public AthenaGM getPlugin() {
        return this.plugin;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getGameMode() {
        return this.gameMode;
    }

    public Integer getMaxPlayers() {
        return this.maxPlayers;
    }

    public Integer getTimeLimit() {
        return this.getTimeLimit();
    }

    public List<String> getMapList() {
        return this.mapList;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public World getWorld() {
        return this.world.get();
    }

    public void setWorld(World world) {
        this.world = new WeakReference<World>(world);
    }

    public File getWorldFile() {
        return this.worldFile;
    }

    public void setWorldFile(File file) {
        this.worldFile = file;
    }

    public Match getMatch() {
        return this.match;
    }


}
