package io.github.xfacthd.framedblocks.client.data.extensions.block;

import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedTargetBlockEntity;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

import java.util.Objects;

public final class TargetClientBlockExtensions extends FramedClientBlockExtensions
{
    @Override
    protected void collectAdditionalTintValues(BlockState state, BlockAndTintGetter level, BlockPos pos, ModelData modelData, IntList tintValues)
    {
        DyeColor dyeColor = modelData.get(FramedTargetBlockEntity.COLOR_PROPERTY);
        tintValues.add(Objects.requireNonNullElse(dyeColor, FramedTargetBlockEntity.DEFAULT_COLOR).getTextColor());
    }
}
