package io.github.xfacthd.framedblocks.client.data.extensions.block;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.render.FramedClientBlockExtensions;
import io.github.xfacthd.framedblocks.api.block.render.ParticleHelper;
import io.github.xfacthd.framedblocks.common.block.cube.FramedOneWayWindowBlock;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class OneWayWindowClientBlockExtensions extends FramedClientBlockExtensions
{
    @Override
    protected boolean addHitEffectsUnsuppressed(
            BlockState state, Level level, BlockHitResult hit, FramedBlockEntity be, ParticleEngine engine
    )
    {
        if (state.getValue(PropertyHolder.NULLABLE_FACE) != NullableDirection.NONE)
        {
            ParticleHelper.Client.addHitEffects(state, level, hit, FramedOneWayWindowBlock.GLASS_DUMMY_CAMO, null, engine);
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
            ParticleHelper.Client.addDestroyEffects(state, level, pos, FramedOneWayWindowBlock.GLASS_DUMMY_CAMO, null, engine);
        }
        return super.addDestroyEffectsUnsuppressed(state, level, pos, be, engine);
    }
}
