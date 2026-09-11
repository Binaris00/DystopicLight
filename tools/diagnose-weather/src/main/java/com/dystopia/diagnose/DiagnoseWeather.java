package com.dystopia.diagnose;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DiagnoseWeather extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("[DIAG] DiagnoseWeather enabled");
        if (Boolean.getBoolean("diag.auto")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    runDiagnostics();
                }
            }.runTaskLater(this, 100L);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("[DIAG] uso: diag <state|storm <world>|clear <world>|clear60 <world>>");
            return true;
        }
        String action = args[0].toLowerCase();
        if (action.equals("state")) {
            dumpAll("CMD-STATE");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("[DIAG] falta el mundo");
            return true;
        }
        World w = Bukkit.getWorld(args[1]);
        if (w == null) {
            sender.sendMessage("[DIAG] mundo '" + args[1] + "' no encontrado");
            return true;
        }
        switch (action) {
            case "storm":
                w.setStorm(true);
                log("CMD storm=true en " + w.getName());
                dumpAll("CMD-STORM-" + w.getName());
                break;
            case "clear":
                w.setStorm(false);
                w.setWeatherDuration(0);
                w.setClearWeatherDuration(0);
                log("CMD clear en " + w.getName());
                dumpAll("CMD-CLEAR-" + w.getName());
                break;
            case "clear60":
                w.setStorm(false);
                w.setWeatherDuration(0);
                w.setClearWeatherDuration(60);
                log("CMD clear60 en " + w.getName());
                dumpAll("CMD-CLEAR60-" + w.getName());
                break;
            default:
                sender.sendMessage("[DIAG] accion desconocida");
        }
        return true;
    }

    private World overworld() {
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NORMAL) {
                return w;
            }
        }
        return Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
    }

    private World nether() {
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NETHER) {
                return w;
            }
        }
        return null;
    }

    private void log(String msg) {
        getLogger().info("[DIAG] " + msg);
    }

    private String state(World w) {
        return String.format("%s(env=%s storm=%s thunder=%s rainDur=%d clearDur=%d)",
                w.getName(), w.getEnvironment(), w.hasStorm(), w.isThundering(),
                w.getWeatherDuration(), w.getClearWeatherDuration());
    }

    private void dumpAll(String tag) {
        for (World w : Bukkit.getWorlds()) {
            log(tag + " | " + state(w));
        }
    }

    private void runDiagnostics() {
        log("=== DUMP INICIAL ===");
        dumpAll("INIT");

        World ow = overworld();
        World nt = nether();
        log("overworld=" + (ow == null ? "NULL" : ow.getName()) + " | nether=" + (nt == null ? "NULL" : nt.getName()));

        if (nt == null) {
            log("NO NETHER FOUND - skip T1/T2/T3");
        } else {
            log("=== T1: setStorm(false) en nether '" + nt.getName() + "' (esperado: no-op, overworld sigue) ===");
            boolean beforeOw = ow.hasStorm();
            nt.setStorm(false);
            boolean afterOw = ow.hasStorm();
            boolean afterNt = nt.hasStorm();
            log("T1 | overworld.hasStorm antes=" + beforeOw + " despues=" + afterOw + " | nether.hasStorm despues=" + afterNt);
            dumpAll("T1-AFTER");

            log("=== T2: setStorm(true) en overworld '" + ow.getName() + "' (esperado: nether lo comparte) ===");
            ow.setStorm(true);
            log("T2 | overworld.hasStorm=" + ow.hasStorm() + " | nether.hasStorm=" + nt.hasStorm());
            dumpAll("T2-AFTER");

            log("=== T3: clear con clearWeatherDuration=60 en overworld (esperado: el ciclo revierte) ===");
            ow.setStorm(false);
            ow.setClearWeatherDuration(60);
            ow.setWeatherDuration(0);
            log("T3 | estado inicial: " + state(ow));
            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    ticks += 20;
                    boolean storm = ow.hasStorm();
                    log("T3 | tick=" + ticks + " overworld.hasStorm=" + storm);
                    if (storm) {
                        log("T3 | RESULTADO: la lluvia revirtio en ~" + ticks + " ticks");
                        cancel();
                    } else if (ticks >= 400) {
                        log("T3 | RESULTADO: sin revertir en 400 ticks (permanecio despejado)");
                        cancel();
                    }
                }
            }.runTaskTimer(this, 20L, 20L);
        }

        log("=== T5: duraciones por mundo ===");
        dumpAll("T5");
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        log("EVENT WeatherChange world=" + e.getWorld().getName() + " toWeatherState=" + e.toWeatherState() + " cancelled=" + e.isCancelled());
    }

    @EventHandler
    public void onThunder(ThunderChangeEvent e) {
        log("EVENT ThunderChange world=" + e.getWorld().getName() + " toThunderState=" + e.toThunderState() + " cancelled=" + e.isCancelled());
    }
}