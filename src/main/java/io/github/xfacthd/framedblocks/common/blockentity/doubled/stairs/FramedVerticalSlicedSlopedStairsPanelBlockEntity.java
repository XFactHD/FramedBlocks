package io.github.xfacthd.framedblocks.common.blockentity.doubled.stairs;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.api.util.Triangle;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedVerticalSlicedSlopedStairsPanelBlockEntity extends FramedDoubleBlockEntity
{
    static final Triangle[] TRIANGLES = Util.make(new Triangle[16], arr ->
    {
        arr[triIndex(Direction.NORTH, HorizontalRotation.UP)] = new Triangle(new Vec3(0, 1, .5), new Vec3(1, 1, .5), new Vec3(0, 0, .5));
        arr[triIndex(Direction.NORTH, HorizontalRotation.DOWN)] = new Triangle(new Vec3(1, 0, .5), new Vec3(0, 0, .5), new Vec3(1, 1, .5));
        arr[triIndex(Direction.NORTH, HorizontalRotation.RIGHT)] = new Triangle(new Vec3(1, 1, .5), new Vec3(0, 1, .5), new Vec3(1, 0, .5));
        arr[triIndex(Direction.NORTH, HorizontalRotation.LEFT)] = new Triangle(new Vec3(0, 0, .5), new Vec3(1, 0, .5), new Vec3(0, 1, .5));

        arr[triIndex(Direction.SOUTH, HorizontalRotation.UP)] = arr[triIndex(Direction.NORTH, HorizontalRotation.RIGHT)];
        arr[triIndex(Direction.SOUTH, HorizontalRotation.DOWN)] = arr[triIndex(Direction.NORTH, HorizontalRotation.LEFT)];
        arr[triIndex(Direction.SOUTH, HorizontalRotation.RIGHT)] = arr[triIndex(Direction.NORTH, HorizontalRotation.UP)];
        arr[triIndex(Direction.SOUTH, HorizontalRotation.LEFT)] = arr[triIndex(Direction.NORTH, HorizontalRotation.DOWN)];

        arr[triIndex(Direction.WEST, HorizontalRotation.UP)] = new Triangle(new Vec3(.5, 1, 1), new Vec3(.5, 1, 0), new Vec3(.5, 0, 1));
        arr[triIndex(Direction.WEST, HorizontalRotation.DOWN)] = new Triangle(new Vec3(.5, 0, 0), new Vec3(.5, 0, 1), new Vec3(.5, 1, 0));
        arr[triIndex(Direction.WEST, HorizontalRotation.RIGHT)] = new Triangle(new Vec3(.5, 1, 0), new Vec3(.5, 1, 1), new Vec3(.5, 0, 0));
        arr[triIndex(Direction.WEST, HorizontalRotation.LEFT)] = new Triangle(new Vec3(.5, 0, 1), new Vec3(.5, 0, 0), new Vec3(.5, 1, 1));

        arr[triIndex(Direction.EAST, HorizontalRotation.UP)] = arr[triIndex(Direction.WEST, HorizontalRotation.RIGHT)];
        arr[triIndex(Direction.EAST, HorizontalRotation.DOWN)] = arr[triIndex(Direction.WEST, HorizontalRotation.LEFT)];
        arr[triIndex(Direction.EAST, HorizontalRotation.RIGHT)] = arr[triIndex(Direction.WEST, HorizontalRotation.UP)];
        arr[triIndex(Direction.EAST, HorizontalRotation.LEFT)] = arr[triIndex(Direction.WEST, HorizontalRotation.DOWN)];
    });

    public FramedVerticalSlicedSlopedStairsPanelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_VERTICAL_SLICED_SLOPED_DOUBLE_STAIRS_PANEL.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = getBlockState().getValue(PropertyHolder.ROTATION);
        Direction dirTwo = rot.getOpposite().withFacing(facing);
        Direction dirThree = rot.rotate(Rotation.CLOCKWISE_90).withFacing(facing);
        Direction side = hit.getDirection();
        Vec3 hitVec = hit.getLocation();

        if (side == facing)
        {
            return false;
        }
        if (side == dirTwo || side == dirThree)
        {
            return MathUtils.fractionInDir(hitVec, facing) < .5;
        }

        if (side == dirTwo.getOpposite() || side == dirThree.getOpposite())
        {
            if (MathUtils.fractionInDir(hitVec, facing) > .5) // Crosshair is definitely on panel's half-width faces
            {
                return false;
            }
        }
        else if (side == facing.getOpposite())
        {
            double par = MathUtils.fractionInDir(hitVec, dirTwo.getOpposite());
            double perp = MathUtils.fractionInDir(hitVec, dirThree);
            if (perp > par) // Crosshair is definitely on half slope's triangle face
            {
                return true;
            }
        }

        Triangle tri = getTriangle(facing, rot);
        Vec3 origin = eyePos.subtract(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        return !tri.intersects(origin, lookVec.normalize());
    }

    static Triangle getTriangle(Direction facing, HorizontalRotation rot)
    {
        return TRIANGLES[triIndex(facing, rot)];
    }

    private static int triIndex(Direction facing, HorizontalRotation rot)
    {
        return (facing.get2DDataValue() << 2) | rot.ordinal();
    }
}
