package io.izzel.arclight.common.mod.server.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ArclightWorldData extends SavedData {

    private static final String TAG_PREFIX = "gr_";

    private boolean initialized;

    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private int clearWeatherTime;

    private long dayTime;
    private long gameTime;

    private Map<String, String> gameRules;

    public ArclightWorldData() {
        this.initialized = false;
    }

    private ArclightWorldData(CompoundTag tag, HolderLookup.Provider provider) {
        this.initialized = true;
        this.raining = tag.getBoolean("raining");
        this.rainTime = tag.getInt("rainTime");
        this.thundering = tag.getBoolean("thundering");
        this.thunderTime = tag.getInt("thunderTime");
        this.clearWeatherTime = tag.getInt("clearWeatherTime");
        this.dayTime = tag.getLong("dayTime");
        this.gameTime = tag.getLong("gameTime");
        this.gameRules = new HashMap<>();
        for (String key : tag.getAllKeys()) {
            if (key.startsWith(TAG_PREFIX)) {
                this.gameRules.put(key.substring(TAG_PREFIX.length()), tag.getString(key));
            }
        }
    }

    public static Factory<ArclightWorldData> factory() {
        return new SavedData.Factory<>(ArclightWorldData::new, ArclightWorldData::new, null);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.putBoolean("raining", raining);
        tag.putInt("rainTime", rainTime);
        tag.putBoolean("thundering", thundering);
        tag.putInt("thunderTime", thunderTime);
        tag.putInt("clearWeatherTime", clearWeatherTime);
        tag.putLong("dayTime", dayTime);
        tag.putLong("gameTime", gameTime);
        if (gameRules != null) {
            gameRules.forEach((key, value) -> tag.putString(TAG_PREFIX + key, value));
        }
        return tag;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isRaining() {
        return raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
        setDirty();
    }

    public int getRainTime() {
        return rainTime;
    }

    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
        setDirty();
    }

    public boolean isThundering() {
        return thundering;
    }

    public void setThundering(boolean thundering) {
        this.thundering = thundering;
        setDirty();
    }

    public int getThunderTime() {
        return thunderTime;
    }

    public void setThunderTime(int thunderTime) {
        this.thunderTime = thunderTime;
        setDirty();
    }

    public int getClearWeatherTime() {
        return clearWeatherTime;
    }

    public void setClearWeatherTime(int clearWeatherTime) {
        this.clearWeatherTime = clearWeatherTime;
        setDirty();
    }

    public long getDayTime() {
        return dayTime;
    }

    public void setDayTime(long dayTime) {
        this.dayTime = dayTime;
        setDirty();
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
        setDirty();
    }

    public Map<String, String> getGameRules() {
        return gameRules;
    }

    public void setGameRules(Map<String, String> gameRules) {
        this.gameRules = gameRules;
        setDirty();
    }
}