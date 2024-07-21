package xfacthd.framedblocks.client.data.extensions.block;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.api.block.render.ParticleHelper;
import xfacthd.framedblocks.common.block.cube.FramedOneWayWindowBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

public final class OneWayWindowClientBlockExtensions extends FramedBlockRenderProperties
{
    @Override
    protected boolean addHitEffectsUnsuppressed(
            BlockState state, Level level, BlockHitResult hit, FramedBlockEntity be, ParticleEngine engine
    )
    {
        if (state.getValue(PropertyHolder.NULLABLE_FACE) != NullableDirection.NONE)
        {
            ParticleHelper.Client.addHitEffects(state, level, hit, FramedOneWayWindowBlock.GLASS_DUMMY_CAMO, engine);
        }
        return super.addHitEffectsUnsuppressed(state, level, hit, be, engine);
    }

    @Override
    protected boolean addDestroyEffectsUnsuppressed(
            BlockState state, Level level, BlockPos pos, FramedBlockEntity be, ParticleEngine engine
    )
    {
        if (state.getValue(PropertyHolder.NULLABLE_FACE) != NullableDirection.NONE)
        {
            ParticleHelper.Client.addDestroyEffects(state, level, pos, FramedOneWayWindowBlock.GLASS_DUMMY_CAMO, engine);
        }
        return super.addDestroyEffectsUnsuppressed(state, level, pos, be, engine);
    }
}
