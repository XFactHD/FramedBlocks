package xfacthd.framedblocks.common.util;

import net.minecraft.core.Direction;
import net.minecraft.tags.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.FramedBlocks;

public class Utils
{
    public static final Tag.Named<Block> FRAMEABLE = BlockTags.bind(FramedBlocks.MODID + ":frameable");
    public static final Tag.Named<Block> BLACKLIST = BlockTags.bind(FramedBlocks.MODID + ":blacklisted");
    public static final Tag.Named<Item> WRENCH = ItemTags.bind("forge:tools/wrench");

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) { throw new IllegalArgumentException("Invalid Direction!"); }
        if (from == to) { return shape; }

        VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(
                    buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static Vec3 fraction(Vec3 vec)
    {
        return new Vec3(
                vec.x() - Math.floor(vec.x()),
                vec.y() - Math.floor(vec.y()),
                vec.z() - Math.floor(vec.z())
        );
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(
            BlockEntityType<A> type, BlockEntityType<E> actualType, BlockEntityTicker<? super E> ticker
    )
    {
        return actualType == type ? (BlockEntityTicker<A>)ticker : null;
    }
}