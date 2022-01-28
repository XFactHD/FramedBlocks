package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedBouncyCube extends FramedBlock
{
    public FramedBouncyCube() { super(BlockType.FRAMED_BOUNCY_CUBE); }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity)
    {
        if (entity.isSuppressingBounce())
        {
            super.updateEntityAfterFallOn(level, entity);
        }
        else
        {
            Vec3 delta = entity.getDeltaMovement();
            if (delta.y < 0.0D)
            {
                double multY = entity instanceof LivingEntity ? 1.0D : 0.8D;
                entity.setDeltaMovement(delta.x, -delta.y * multY, delta.z);
            }
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        double dY = Math.abs(entity.getDeltaMovement().y);
        if (dY < 0.1D && !entity.isSteppingCarefully())
        {
            double multXZ = 0.4D + dY * 0.2D;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(multXZ, 1.0D, multXZ));
        }

        super.stepOn(level, pos, state, entity);
    }
}
