package io.izzel.arclight.common.mixin.core.world.level.storage;

import io.izzel.arclight.common.bridge.core.world.level.storage.DerivedLevelDataBridge;
import io.izzel.arclight.common.mod.server.world.ArclightWorldData;
import io.izzel.arclight.i18n.ArclightConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;

@Mixin(DerivedLevelData.class)
public class DerivedLevelDataMixin implements DerivedLevelDataBridge {

    @Shadow @Final private ServerLevelData wrapped;

    private ResourceKey<LevelStem> typeKey;

    private long arclight$dayTime;
    private long arclight$gameTime;
    private boolean arclight$raining;
    private int arclight$rainTime;
    private boolean arclight$thundering;
    private int arclight$thunderTime;
    private int arclight$clearWeatherTime;
    private GameRules arclight$gameRules;

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public String getLevelName() {
        if (typeKey == null || typeKey == LevelStem.OVERWORLD) {
            return this.wrapped.getLevelName();
        } else {
            if (ArclightConfig.spec().getCompat().isSymlinkWorld()) {
                String worldName = this.wrapped.getLevelName() + "_";
                String suffix;
                if (typeKey == LevelStem.NETHER) {
                    suffix = "nether";
                } else if (typeKey == LevelStem.END) {
                    suffix = "the_end";
                } else {
                    suffix = (typeKey.location().getNamespace() + "_" + typeKey.location().getPath()).replace('/', '_');
                }
                return worldName + suffix;
            } else {
                String worldName = this.wrapped.getLevelName() + "/";
                String suffix;
                if (typeKey == LevelStem.END) {
                    suffix = "DIM1";
                } else if (typeKey == LevelStem.NETHER) {
                    suffix = "DIM-1";
                } else {
                    suffix = typeKey.location().getNamespace() + "/" + typeKey.location().getPath();
                }
                return worldName + suffix;
            }
        }
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public boolean isRaining() {
        return this.arclight$raining;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public int getRainTime() {
        return this.arclight$rainTime;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public boolean isThundering() {
        return this.arclight$thundering;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public int getThunderTime() {
        return this.arclight$thunderTime;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public int getClearWeatherTime() {
        return this.arclight$clearWeatherTime;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public void setRaining(boolean isRaining) {
        if (this.arclight$raining == isRaining) {
            return;
        }

        World world = Bukkit.getWorld(this.getLevelName());
        if (world != null) {
            WeatherChangeEvent event = new WeatherChangeEvent(world, isRaining);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }
        this.arclight$raining = isRaining;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public void setRainTime(int time) {
        this.arclight$rainTime = time;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public void setThundering(boolean thunderingIn) {
        if (this.arclight$thundering == thunderingIn) {
            return;
        }

        World world = Bukkit.getWorld(this.getLevelName());
        if (world != null) {
            ThunderChangeEvent event = new ThunderChangeEvent(world, thunderingIn);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }
        this.arclight$thundering = thunderingIn;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public void setThunderTime(int time) {
        this.arclight$thunderTime = time;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public void setClearWeatherTime(int time) {
        this.arclight$clearWeatherTime = time;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public long getDayTime() {
        return this.arclight$dayTime;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public void setDayTime(long time) {
        this.arclight$dayTime = time;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public long getGameTime() {
        return this.arclight$gameTime;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public void setGameTime(long time) {
        this.arclight$gameTime = time;
    }

    /**
     * @author IzzelAliz
     * @reason
     */
    @Overwrite
    public GameRules getGameRules() {
        if (this.arclight$gameRules == null) {
            this.arclight$gameRules = this.wrapped.getGameRules().copy();
        }
        return this.arclight$gameRules;
    }

    @Override
    public ServerLevelData bridge$getDelegate() {
        return wrapped;
    }

    @Override
    public void bridge$setDimType(ResourceKey<LevelStem> typeKey) {
        this.typeKey = typeKey;
    }

    @Override
    public void bridge$loadWorldData(ArclightWorldData data) {
        if (data.isInitialized()) {
            this.arclight$raining = data.isRaining();
            this.arclight$rainTime = data.getRainTime();
            this.arclight$thundering = data.isThundering();
            this.arclight$thunderTime = data.getThunderTime();
            this.arclight$clearWeatherTime = data.getClearWeatherTime();
            this.arclight$dayTime = data.getDayTime();
            this.arclight$gameTime = data.getGameTime();
            Map<String, String> rules = data.getGameRules();
            if (rules != null && !rules.isEmpty()) {
                applyGameRules(rules);
            }
        } else {
            this.arclight$raining = this.wrapped.isRaining();
            this.arclight$rainTime = this.wrapped.getRainTime();
            this.arclight$thundering = this.wrapped.isThundering();
            this.arclight$thunderTime = this.wrapped.getThunderTime();
            this.arclight$clearWeatherTime = this.wrapped.getClearWeatherTime();
            this.arclight$dayTime = this.wrapped.getDayTime();
            this.arclight$gameTime = this.wrapped.getGameTime();
        }
    }

    @Override
    public void bridge$saveWorldData(ArclightWorldData data) {
        data.setRaining(this.arclight$raining);
        data.setRainTime(this.arclight$rainTime);
        data.setThundering(this.arclight$thundering);
        data.setThunderTime(this.arclight$thunderTime);
        data.setClearWeatherTime(this.arclight$clearWeatherTime);
        data.setDayTime(this.arclight$dayTime);
        data.setGameTime(this.arclight$gameTime);
        if (this.arclight$gameRules != null) {
            Map<String, String> rules = new HashMap<>();
            this.arclight$gameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
                @Override
                public void visitBoolean(GameRules.Key<GameRules.BooleanValue> key, GameRules.Type<GameRules.BooleanValue> type) {
                    rules.put(key.getId(), arclight$gameRules.getRule(key).serialize());
                }

                @Override
                public void visitInteger(GameRules.Key<GameRules.IntegerValue> key, GameRules.Type<GameRules.IntegerValue> type) {
                    rules.put(key.getId(), arclight$gameRules.getRule(key).serialize());
                }
            });
            data.setGameRules(rules);
        }
    }

    private void applyGameRules(Map<String, String> rules) {
        GameRules gameRules = this.getGameRules();
        gameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            @Override
            public void visitBoolean(GameRules.Key<GameRules.BooleanValue> key, GameRules.Type<GameRules.BooleanValue> type) {
                String value = rules.get(key.getId());
                if (value != null) {
                    gameRules.getRule(key).deserialize(value);
                }
            }

            @Override
            public void visitInteger(GameRules.Key<GameRules.IntegerValue> key, GameRules.Type<GameRules.IntegerValue> type) {
                String value = rules.get(key.getId());
                if (value != null) {
                    gameRules.getRule(key).deserialize(value);
                }
            }
        });
    }
}