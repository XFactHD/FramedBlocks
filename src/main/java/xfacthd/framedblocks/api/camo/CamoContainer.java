package xfacthd.framedblocks.api.camo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

public abstract class CamoContainer<C extends CamoContent<C>, T extends CamoContainer<C, T>>
{
    protected final C content;

    protected CamoContainer(C content)
    {
        this.content = content;
    }

    /**
     * {@return this container's camo content}
     */
    public final C getContent()
    {
        return content;
    }

    /**
     * Returns the {@link MapColor} to use on the map for this camo container
     * @param level The current level
     * @param pos The position of the framed block
     * @return The map color
     */
    public MapColor getMapColor(BlockGetter level, BlockPos pos)
    {
        return content.getMapColor(level, pos);
    }

    /**
     * Returns the tint color for use in {@link net.minecraft.client.color.block.BlockColor}
     * @param level The current level
     * @param pos The position of the framed block
     * @param tintIdx The tint index for which the color was requested
     * @return The tint color for the given index
     */
    public int getTintColor(BlockAndTintGetter level, BlockPos pos, int tintIdx)
    {
        return content.getTintColor(level, pos, tintIdx);
    }

    /**
     * Returns the color multipliers to apply to a beacon beam
     * @param level The current level
     * @param pos The position of the framed block
     * @param beaconPos The position of the beacon where the beam originates from
     * @return An array of R, G and B values to be used as the color multiplier
     */
    public Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        return content.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    public abstract boolean canRotateCamo();

    /**
     * Rotate the camo if possible. Must return {@code null} if the specific content or this container in general does
     * not support camo rotation
     * @return A new container with the rotated camo if the rotation was successful, null otherwise
     */
    @Nullable
    public abstract T rotateCamo();

    /**
     * {@return whether this container represents a non-existent camo}
     */
    public final boolean isEmpty()
    {
        return content.isEmpty();
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    /**
     * Returns the {@link CamoContainerFactory} used to load and save this container
     */
    public abstract CamoContainerFactory<T> getFactory();
}
