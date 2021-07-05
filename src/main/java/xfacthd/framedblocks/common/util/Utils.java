package xfacthd.framedblocks.common.util;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.FramedBlocks;

public class Utils
{
    public static final ITag.INamedTag<Block> FRAMEABLE = BlockTags.bind(FramedBlocks.MODID + ":frameable");
    public static final ITag.INamedTag<Block> BLACKLIST = BlockTags.bind(FramedBlocks.MODID + ":blacklisted");

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) { throw new IllegalArgumentException("Invalid Direction!"); }
        if (from == to) { return shape; }

        VoxelShape[] buffer = new VoxelShape[] { shape, VoxelShapes.empty() };

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.or(
                    buffer[1],
                    VoxelShapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = VoxelShapes.empty();
        }

        return buffer[0];
    }

    public static Vector3d fraction(Vector3d vec)
    {
        return new Vector3d(
                vec.x() - Math.floor(vec.x()),
                vec.y() - Math.floor(vec.y()),
                vec.z() - Math.floor(vec.z())
        );
    }
}