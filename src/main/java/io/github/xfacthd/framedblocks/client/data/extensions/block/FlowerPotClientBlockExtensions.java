package io.github.xfacthd.framedblocks.client.data.extensions.block;

import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedFlowerPotBlockEntity;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

import java.util.List;

public final class FlowerPotClientBlockExtensions extends FramedClientBlockExtensions
{
    @Override
    protected void collectAdditionalTintValues(BlockState state, BlockAndTintGetter level, BlockPos pos, ModelData modelData, IntList tintValues)
    {
        Block flowerBlock = modelData.get(FramedFlowerPotBlockEntity.FLOWER_BLOCK);
        if (flowerBlock != null)
        {
            BlockState flowerState = flowerBlock.defaultBlockState();
            List<BlockTintSource> tintSources = Minecraft.getInstance().getBlockColors().getTintSources(flowerState);
            for (BlockTintSource tintSource : tintSources)
            {
                tintValues.add(tintSource.colorInWorld(flowerState, level, pos));
            }
        }
    }
}
