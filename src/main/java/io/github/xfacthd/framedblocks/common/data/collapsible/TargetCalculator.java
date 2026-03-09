package io.github.xfacthd.framedblocks.common.data.collapsible;

import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.api.util.Triangle;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class TargetCalculator
{
    private static final float[] VERTEX_NO_OFFSET = new float[] { 1F, 1F, 1F, 1F };

    @Nullable
    public static HammerTarget computeTarget(FramedCollapsibleBlockEntity be, Player player, BlockHitResult blockHit, boolean relative, float partialTick)
    {
        Direction face = blockHit.getDirection();
        Vec3 hitLoc = blockHit.getLocation();
        Direction oldFace = be.getCollapsedFace();

        if (oldFace == null)
        {
            return new HammerTarget(face, null, hitLoc, relative);
        }
        if (face == oldFace.getOpposite())
        {
            return null;
        }

        BlockState state = be.getBlockState();
        byte[] offsets = FramedCollapsibleBlockEntity.unpackOffsets(be.getPackedOffsets(state));
        if (offsets[0] == offsets[1] && offsets[0] == offsets[2] && offsets[0] == offsets[3])
        {
            return face == oldFace ? new HammerTarget(face, oldFace, hitLoc, relative) : null;
        }

        if (face != oldFace)
        {
            VertexPair pair = VertexMappings.getEdgeVertices(oldFace, face);
            double hitHor = MathUtils.fractionInDir(blockHit.getLocation(), pair.dirToV2());
            double edgeHeight = 1D - Mth.lerp(hitHor, offsets[pair.v1()] / 16D, offsets[pair.v2()] / 16D);
            double hitHeight = MathUtils.fractionInDir(blockHit.getLocation(), oldFace);
            if (hitHeight <= edgeHeight)
            {
                return null;
            }
        }

        BlockPos pos = be.getBlockPos();
        boolean rotate = shouldRotate(state, offsets);
        Vec3 rayStart = player.getEyePosition(partialTick);
        double dist = rayStart.distanceTo(blockHit.getLocation()) + 1D;
        Vec3 rayVec = player.getLookAngle().normalize().scale(dist);

        Vec3 hitTri1 = computeTriangle(pos, oldFace, offsets, rotate, true).clip(rayStart, rayVec);
        if (hitTri1 != null)
        {
            return new HammerTarget(oldFace, oldFace, hitTri1, relative);
        }
        Vec3 hitTri2 = computeTriangle(pos, oldFace, offsets, rotate, false).clip(rayStart, rayVec);
        if (hitTri2 != null)
        {
            return new HammerTarget(oldFace, oldFace, hitTri2, relative);
        }
        return null;
    }

    public static DebugInfo computeDebugInfo(FramedCollapsibleBlockEntity be, Player player, BlockHitResult blockHit, float partialTick)
    {
        BlockState state = be.getBlockState();
        BlockPos pos = be.getBlockPos();
        Direction face = Objects.requireNonNullElse(be.getCollapsedFace(), blockHit.getDirection());
        byte[] offsets = FramedCollapsibleBlockEntity.unpackOffsets(be.getPackedOffsets(state));

        boolean rotate = shouldRotate(state, offsets);
        Triangle tri1 = computeTriangle(pos, face, offsets, rotate, true);
        Triangle tri2 = computeTriangle(pos, face, offsets, rotate, false);
        HammerTarget target = TargetCalculator.computeTarget(be, player, blockHit, false, partialTick);
        Vec3 targetPos = target == null ? null : target.pos().subtract(pos.getX(), pos.getY(), pos.getZ());
        return new DebugInfo(pos, face, tri1, tri2, targetPos, getVertexHeights(be, face), rotate);
    }

    private static boolean shouldRotate(BlockState state, byte[] offsets)
    {
        int diff02 = Math.abs(offsets[0] - offsets[2]);
        int diff13 = Math.abs(offsets[1] - offsets[3]);
        return  (diff13 > diff02) != state.getValue(PropertyHolder.ROTATE_SPLIT_LINE);
    }

    private static Triangle computeTriangle(BlockPos pos, Direction face, byte[] offsets, boolean rotate, boolean first)
    {
        if (first)
        {
            return new Triangle(
                    rotate ? vertex(pos, face, offsets, 0, 1, 1) : vertex(pos, face, offsets, 0, 0, 0),
                    rotate ? vertex(pos, face, offsets, 0, 0, 0) : vertex(pos, face, offsets, 1, 0, 3),
                    rotate ? vertex(pos, face, offsets, 1, 0, 3) : vertex(pos, face, offsets, 1, 1, 2)
            );
        }
        else
        {
            return new Triangle(
                    rotate ? vertex(pos, face, offsets, 1, 0, 3) : vertex(pos, face, offsets, 1, 1, 2),
                    rotate ? vertex(pos, face, offsets, 1, 1, 2) : vertex(pos, face, offsets, 0, 1, 1),
                    rotate ? vertex(pos, face, offsets, 0, 1, 1) : vertex(pos, face, offsets, 0, 0, 0)
            );
        }
    }

    private static Vec3 vertex(BlockPos pos, Direction face, byte[] offsets, double x, double z, int vert)
    {
        int px = pos.getX();
        int py = pos.getY();
        int pz = pos.getZ();
        double y = 1D - (offsets[vert] / 16D);
        return switch (face)
        {
            case DOWN ->  new Vec3(px +       x, py + (1 - y), pz + (1 - z));
            case UP ->    new Vec3(px +       x, py +       y, pz +       z);
            case NORTH -> new Vec3(px + (1 - x), py + (1 - z), pz + (1 - y));
            case SOUTH -> new Vec3(px +       x, py + (1 - z), pz +       y);
            case WEST ->  new Vec3(px + (1 - y), py + (1 - z), pz +       x);
            case EAST ->  new Vec3(px +       y, py + (1 - z), pz + (1 - x));
        };
    }

    public static float[] getVertexHeights(FramedCollapsibleBlockEntity be, @Nullable Direction face)
    {
        if (face == null) return VERTEX_NO_OFFSET;
        return new float[] {
                1F - (be.getVertexOffset(0) / 16F),
                1F - (be.getVertexOffset(1) / 16F),
                1F - (be.getVertexOffset(2) / 16F),
                1F - (be.getVertexOffset(3) / 16F)
        };
    }

    private TargetCalculator() { }
}
