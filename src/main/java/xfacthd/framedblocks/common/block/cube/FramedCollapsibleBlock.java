package xfacthd.framedblocks.common.block.cube;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.data.property.NullableDirection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FramedCollapsibleBlock extends FramedBlock
{
    private static final Map<Integer, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<>();

    public FramedCollapsibleBlock(BlockType blockType)
    {
        super(blockType, IFramedBlock.createProperties(BlockType.FRAMED_COLLAPSIBLE_BLOCK).dynamicShape());
        registerDefaultState(defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(PropertyHolder.ROTATE_SPLIT_LINE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.NULLABLE_FACE, BlockStateProperties.WATERLOGGED, PropertyHolder.ROTATE_SPLIT_LINE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx).withWater().build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.is(Utils.WRENCH))
        {
            boolean rotSplitLine = state.getValue(PropertyHolder.ROTATE_SPLIT_LINE);
            level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.ROTATE_SPLIT_LINE, !rotSplitLine));
            return true;
        }
        else if (heldItem.getItem() == FBContent.ITEM_FRAMED_HAMMER.value())
        {
            if (level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)
            {
                if (!level.isClientSide())
                {
                    be.handleDeform(player);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, ctx))
        {
            return Shapes.empty();
        }

        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        if (face != NullableDirection.NONE)
        {
            if (level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)
            {
                int offsets = be.getPackedOffsets();
                offsets |= (face.toDirection().get3DDataValue() << 20);
                return SHAPE_CACHE.computeIfAbsent(offsets, FramedCollapsibleBlock::buildShape);
            }
        }
        return Shapes.block();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);

        //noinspection ConstantConditions
        if (!level.isClientSide() && stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
        {
            //Properly set collapsed face when placed from a stack with BE NBT data
            if (level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)
            {
                Direction collapseFace = be.getCollapsedFace();
                if (state.getValue(PropertyHolder.NULLABLE_FACE).toDirection() != collapseFace)
                {
                    level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.fromDirection(collapseFace)));
                }
            }
        }
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        return face == NullableDirection.NONE || Utils.isY(face.toDirection());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedCollapsibleBlockEntity(pos, state);
    }



    @SuppressWarnings("SuspiciousNameCombination")
    private static VoxelShape buildShape(Integer packedData)
    {
        Direction face = Direction.from3DDataValue(packedData >> 20);
        byte[] offsets = FramedCollapsibleBlockEntity.unpackOffsets(packedData & 0xFFFFF);

        boolean positive = Utils.isPositive(face);
        boolean flipX = face == Direction.NORTH || face == Direction.EAST;
        boolean flipZ = face != Direction.UP;

        VoxelShape result = Shapes.empty();
        for (int x = 0; x < 4; x++)
        {
            for (int z = 0; z < 4; z++)
            {
                double x0 = flipX ? (1D - x / 4D) : (x / 4D);
                double x1 = flipX ? (1D - (x + 1) / 4D) : ((x + 1) / 4D);
                double z0 = flipZ ? (1D - z / 4D) : (z / 4D);
                double z1 = flipZ ? (1D - (z + 1) / 4D) : ((z + 1) / 4D);

                double y0 = Mth.lerp2(x0, z0, offsets[0], offsets[3], offsets[1], offsets[2]);
                double y1 = Mth.lerp2(x1, z1, offsets[0], offsets[3], offsets[1], offsets[2]);

                double y = positive ?
                        Math.max(16D - Math.min(y0, y1), Mth.EPSILON * 2D) :
                        Math.min(Math.min(y0, y1), 16D - (Mth.EPSILON * 2D));

                VoxelShape shape = switch (face)
                {
                    case NORTH -> box(x * 4, z * 4, y, (x + 1) * 4, (z + 1) * 4, 16);
                    case EAST -> box(0, z * 4, x * 4, y, (z + 1) * 4, (x + 1) * 4);
                    case SOUTH -> box(x * 4, z * 4, 0, (x + 1) * 4, (z + 1) * 4, y);
                    case WEST -> box(y, z * 4, x * 4, 16, (z + 1) * 4, (x + 1) * 4);
                    case UP -> box(x * 4, 0, z * 4, (x + 1) * 4, y, (z + 1) * 4);
                    case DOWN -> box(x * 4, y, z * 4, (x + 1) * 4, 16, (z + 1) * 4);
                };

                result = ShapeUtils.orUnoptimized(result, shape);
            }
        }

        return result.optimize();
    }
}
