package io.github.xfacthd.framedblocks.api.camo;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public abstract class CamoContainerClientHandler<C extends CamoContent<C>, T extends CamoContainer<C, T>>
{
    /// Return the amount of tint layers the provided camo has
    public abstract int getTintCount(T camo);

    /// Collect the tint values of the provided camo when rendered as part of a block.
    ///
    /// The amount of values added to the list must match the return value of [#getTintCount(CamoContainer)].
    ///
    /// @param camo     The camo whose tint values are being queried
    /// @param level    The level in which the camo is being rendered
    /// @param pos      The position at which the camo is being rendered
    /// @param tintList The list to append the tint values to
    public abstract void collectTintValues(T camo, BlockAndTintGetter level, BlockPos pos, IntList tintList);

    /// Collect the tint values of the provided camo when rendered as part of an item.
    ///
    /// The amount of values added to the list must match the return value of [#getTintCount(CamoContainer)].
    ///
    /// @param camo     The camo whose tint values are being queried
    /// @param stack    The stack holding the camo
    /// @param tintList The list to append the tint values to
    public abstract void collectTintValues(T camo, ItemStack stack, IntList tintList);

    static final class Default<C extends CamoContent<C>, T extends CamoContainer<C, T>> extends CamoContainerClientHandler<C, T>
    {
        static final CamoContainerClientHandler<?, ?> INSTANCE = new Default<>();

        @Override
        public int getTintCount(T camo)
        {
            C content = camo.getContent();
            return content.getClientHandler().getTintCount(content);
        }

        @Override
        public void collectTintValues(T camo, BlockAndTintGetter level, BlockPos pos, IntList tintList)
        {
            C content = camo.getContent();
            content.getClientHandler().collectTintValues(content, level, pos, tintList);
        }

        @Override
        public void collectTintValues(T camo, ItemStack stack, IntList tintList)
        {
            C content = camo.getContent();
            content.getClientHandler().collectTintValues(content, stack, tintList);
        }
    }
}
