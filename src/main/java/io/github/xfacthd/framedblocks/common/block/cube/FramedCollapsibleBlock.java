package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.shapes.ShapeUtils;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FramedCollapsibleBlock extends FramedBlock
{
    private static final Map<Integer, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<>();
    private static final double MIN_DEPTH = Mth.EPSILON * 2D;

    public FramedCollapsibleBlock(BlockType blockType, Properties props)
    {
        super(blockType, props.dynamicShape());
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.ROTATE_SPLIT_LINE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.NULLABLE_FACE, PropertyHolder.ROTATE_SPLIT_LINE);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx).withWater().build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() == FBContent.ITEM_FRAMED_WRENCH.value())
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
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
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
                int offsets = be.getPackedOffsets(state);
                offsets |= (face.toDirection().get3DDataValue() << 20);
                return SHAPE_CACHE.computeIfAbsent(offsets, FramedCollapsibleBlock::buildShape);
            }
        }
        return Shapes.block();
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state)
    {
        return Shapes.empty();
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        NullableDirection collapsedFace = state.getValue(PropertyHolder.NULLABLE_FACE);
        return state.setValue(PropertyHolder.NULLABLE_FACE, collapsedFace.rotate(rotation));
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        return face == NullableDirection.NONE || DirUtils.isY(face.toDirection());
    }

    @Override
    public FramedBlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedCollapsibleBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    @Nullable
    public Direction getHorizontalOrientation(BlockState state)
    {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static VoxelShape buildShape(Integer packedData)
    {
        Direction face = Direction.from3DDataValue(packedData >> 20);
        byte[] offsets = FramedCollapsibleBlockEntity.unpackOffsets(packedData & 0xFFFFF);

        boolean positive = DirUtils.isPositive(face);
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

                double y = Math.min(y0, y1);
                y = positive ? Math.max(16D - y, MIN_DEPTH) : Math.min(y, 16D - MIN_DEPTH);

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

        return ShapeUtils.optimize(result);
    }
}
