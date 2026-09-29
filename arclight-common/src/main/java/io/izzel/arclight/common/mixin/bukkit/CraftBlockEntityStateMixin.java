package io.izzel.arclight.common.mixin.bukkit;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.craftbukkit.v.block.CraftBlockEntityState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CraftBlockEntityState.class, remap = false)
public abstract class CraftBlockEntityStateMixin {

    /**
     * @author Binaris
     * @reason Paper exposes these methods as public; plugins compiled against Paper
     * (e.g. ItemsAdder) fail with IllegalAccessError on hybrid servers otherwise.
     */
    @Overwrite
    public BlockEntity getTileEntity() {
        return ((CraftBlockEntityStateAccessor) this).arclight$getBlockEntity();
    }

    /**
     * @author Binaris
     * @reason See getTileEntity.
     */
    @Overwrite
    public BlockEntity getSnapshot() {
        return ((CraftBlockEntityStateAccessor) this).arclight$getSnapshot();
    }
}