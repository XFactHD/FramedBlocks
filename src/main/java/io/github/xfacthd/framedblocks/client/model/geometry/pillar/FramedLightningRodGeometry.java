package io.github.xfacthd.framedblocks.client.model.geometry.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedLightningRodGeometry extends Geometry
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final float MIN_FRONT = 6F/16F;
    private static final float MAX_FRONT = 10F/16F;
    private static final float MIN_BACK = 7F/16F;
    private static final float MAX_BACK = 9F/16F;
    private static final QuadModifier.Modifier MOD_HEAD_VERT = Modifiers.cutTopBottom(MIN_FRONT, MIN_FRONT, MAX_FRONT, MAX_FRONT);
    private static final QuadModifier.Modifier MOD_HEAD_HOR = Modifiers.cutSide(MIN_FRONT, MIN_FRONT, MAX_FRONT, MAX_FRONT);
    private static final QuadModifier.Modifier MOD_TAIL_VERT = Modifiers.cutTopBottom(MIN_BACK, MIN_BACK, MAX_BACK, MAX_BACK);
    private static final QuadModifier.Modifier MOD_TAIL_HOR = Modifiers.cutSide(MIN_BACK, MIN_BACK, MAX_BACK, MAX_BACK);

    private final Direction facing;
    private final boolean copycatHead;

    public FramedLightningRodGeometry(GeometryFactory.Context ctx)
    {
        this.facing = ctx.state().getValue(BlockStateProperties.FACING);
        this.copycatHead = ctx.state().getValue(FramedProperties.COPYCAT_STYLE);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        Direction quadDir = quad.direction();
        boolean vertical = DirUtils.isY(quadDir);
        boolean front = quadDir == facing;
        boolean back = quadDir == facing.getOpposite();

        if (copycatHead)
        {
            for (Direction dir : DIRECTIONS)
            {
                if (dir.getAxis() == quadDir.getAxis()) continue;

                Direction dirCw = dir.getClockWise(quadDir.getAxis());
                float dirOff = getOffset(dir);
                float dirCwOff = getOffset(dirCw);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(dir, 2F / 16F))
                        .apply(Modifiers.cut(dirCw, 2F / 16F))
                        .apply(Modifiers.offset(dir, dirOff))
                        .apply(Modifiers.offset(dirCw, dirCwOff))
                        .applyIf(Modifiers.setPosition(back ? 4F / 16F : 10F / 16F), !front)
                        .export(quadMap, front ? quadDir : null);
            }
        }
        else if (front || back)
        {
            QuadModifier.of(quad)
                    .apply(vertical ? MOD_HEAD_VERT : MOD_HEAD_HOR)
                    .applyIf(Modifiers.setPosition(4F/16F), back)
                    .export(quadMap, front ? quadDir : null);
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing.getOpposite(), 4F/16F))
                    .apply(Modifiers.cut(facing.getClockWise(quadDir.getAxis()), 10F/16F))
                    .apply(Modifiers.cut(facing.getCounterClockWise(quadDir.getAxis()), 10F/16F))
                    .apply(Modifiers.setPosition(10F/16F))
                    .export(quadMap, null);
        }

        if (back)
        {
            QuadModifier.of(quad)
                    .apply(vertical ? MOD_TAIL_VERT : MOD_TAIL_HOR)
                    .export(quadMap, quadDir);
        }
        else if (!front)
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cut(facing, 12F/16F))
                    .apply(Modifiers.cut(facing.getClockWise(quadDir.getAxis()), 9F/16F))
                    .apply(Modifiers.cut(facing.getCounterClockWise(quadDir.getAxis()), 9F/16F))
                    .apply(Modifiers.setPosition(9F/16F))
                    .export(quadMap, null);
        }
    }

    private float getOffset(Direction offsetDir)
    {
        if (offsetDir == facing) return 12F/16F;
        if (offsetDir == facing.getOpposite()) return 0F;
        return 6F/16F;
    }

    @Override
    public boolean useSolidNoCamoModel()
    {
        return true;
    }
}
