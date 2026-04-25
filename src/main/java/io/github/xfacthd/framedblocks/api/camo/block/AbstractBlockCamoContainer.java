package io.github.xfacthd.framedblocks.api.camo.block;

import io.github.xfacthd.framedblocks.api.camo.CamoContainer;
import io.github.xfacthd.framedblocks.api.camo.block.rotator.BlockCamoRotator;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public abstract class AbstractBlockCamoContainer<T extends AbstractBlockCamoContainer<T>> extends CamoContainer<BlockCamoContent, T> {
    protected AbstractBlockCamoContainer(BlockState state) {
        super(new BlockCamoContent(state));
    }

    public final BlockState getState() {
        return content.getState();
    }

    @Override
    public boolean canRotateCamo() {
        BlockState state = content.getState();
        return BlockCamoRotator.of(state.getBlock()).canRotate(state);
    }

    @Override
    public @Nullable T rotateCamo() {
        BlockState state = content.getState();
        BlockState newState = BlockCamoRotator.of(state.getBlock()).rotate(state);
        return newState != null ? copyWithState(newState) : null;
    }

    @Override
    @SuppressWarnings({ "unchecked", "deprecation" })
    public T adjustForCarrierRotation(Mirror mirror, Rotation rotation) {
        BlockState state = content.getState();
        BlockState newState = state.mirror(mirror).rotate(rotation);
        return state != newState ? copyWithState(newState) : (T) this;
    }

    /**
     * {@return a copy of this camo container with the camo state replaced by the given state}
     * To be used when a mod does a similar action to {@link CamoContainer#rotateCamo()} through external means
     * such as custom item interactions.
     */
    @SuppressWarnings("unchecked")
    public final T copyWithState(BlockState state) {
        return getFactory().copyContainerWithState((T) this, state);
    }

    @Override
    public abstract AbstractBlockCamoContainerFactory<T> getFactory();
}
