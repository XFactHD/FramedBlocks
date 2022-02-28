package xfacthd.framedblocks.common.block;

import com.google.common.cache.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedCollapsibleTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;
import xfacthd.framedblocks.common.util.MathUtils;

import javax.annotation.Nullable;

public class FramedCollapsibleBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        CollapseFace face = state.getValue(PropertyHolder.COLLAPSED_FACE);
        if (face == CollapseFace.NONE) { return true; }
        return dir == face.toDirection().getOpposite();
    };

    private static final LoadingCache<Integer, VoxelShape> SHAPE_CACHE = CacheBuilder.newBuilder().maximumSize(1024).build(new ShapeLoader());

    public FramedCollapsibleBlock(BlockType blockType)
    {
        super(blockType, IFramedBlock.createProperties(BlockType.FRAMED_COLLAPSIBLE_BLOCK).dynamicShape());
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.COLLAPSED_FACE, BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return withWater(defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    public static boolean onLeftClick(World world, BlockPos pos, PlayerEntity player)
    {
        if (player.getMainHandItem().getItem() != FBContent.itemFramedHammer.get()) { return false; }

        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof FramedCollapsibleTileEntity)
        {
            if (!world.isClientSide())
            {
                ((FramedCollapsibleTileEntity) te).handleDeform(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        if (isIntangible(state, world, pos, ctx)) { return VoxelShapes.empty(); }

        CollapseFace face = state.getValue(PropertyHolder.COLLAPSED_FACE);
        if (face != CollapseFace.NONE)
        {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof FramedCollapsibleTileEntity)
            {
                int offsets = ((FramedCollapsibleTileEntity) te).getPackedOffsets();
                offsets |= (face.toDirection().get3DDataValue() << 20);
                return SHAPE_CACHE.getUnchecked(offsets);
            }
        }
        return VoxelShapes.block();
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level)
    {
        return new FramedCollapsibleTileEntity();
    }



    private static class ShapeLoader extends CacheLoader<Integer, VoxelShape>
    {
        @Override
        public VoxelShape load(Integer packedData)
        {
            Direction face = Direction.from3DDataValue(packedData >> 20);
            byte[] offsets = FramedCollapsibleTileEntity.unpackOffsets(packedData & 0xFFFFF);

            boolean positive = face.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            boolean flipX = face == Direction.NORTH || face == Direction.EAST;
            boolean flipZ = face != Direction.UP;

            VoxelShape result = VoxelShapes.empty();
            for (int x = 0; x < 4; x++)
            {
                for (int z = 0; z < 4; z++)
                {
                    double x0 = flipX ? (1D - x / 4D) : (x / 4D);
                    double x1 = flipX ? (1D - (x + 1) / 4D) : ((x + 1) / 4D);
                    double z0 = flipZ ? (1D - z / 4D) : (z / 4D);
                    double z1 = flipZ ? (1D - (z + 1) / 4D) : ((z + 1) / 4D);

                    double y0 = MathHelper.lerp2(x0, z0, offsets[0], offsets[3], offsets[1], offsets[2]);
                    double y1 = MathHelper.lerp2(x1, z1, offsets[0], offsets[3], offsets[1], offsets[2]);

                    double y = positive ?
                            Math.max(16D - Math.min(y0, y1), MathUtils.EPSILON * 2D) :
                            Math.min(Math.min(y0, y1), 16D - (MathUtils.EPSILON * 2D));

                    VoxelShape shape;
                    switch (face)
                    {
                        case NORTH:
                        {
                            shape = box(x * 4, z * 4, y, (x + 1) * 4, (z + 1) * 4, 16);
                            break;
                        }
                        case EAST:
                        {
                            shape = box(0, z * 4, x * 4, y, (z + 1) * 4, (x + 1) * 4);
                            break;
                        }
                        case SOUTH:
                        {
                            shape = box(x * 4, z * 4, 0, (x + 1) * 4, (z + 1) * 4, y);
                            break;
                        }
                        case WEST:
                        {
                            shape = box(y, z * 4, x * 4, 16, (z + 1) * 4, (x + 1) * 4);
                            break;
                        }
                        case UP:
                        {
                            shape = box(x * 4, 0, z * 4, (x + 1) * 4, y, (z + 1) * 4);
                            break;
                        }
                        case DOWN:
                        {
                            shape = box(x * 4, y, z * 4, (x + 1) * 4, 16, (z + 1) * 4);
                            break;
                        }
                        default: throw new IncompatibleClassChangeError("Direction enum was tampered with!");
                    };

                    result = VoxelShapes.joinUnoptimized(result, shape, IBooleanFunction.OR);
                }
            }

            result = result.optimize();

            return result;
        }
    }
}