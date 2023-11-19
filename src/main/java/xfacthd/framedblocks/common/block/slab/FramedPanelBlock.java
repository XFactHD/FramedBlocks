package xfacthd.framedblocks.common.block.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.EnumMap;

@SuppressWarnings("deprecation")
public class FramedPanelBlock extends FramedBlock
{
    public FramedPanelBlock()
    {
        super(BlockType.FRAMED_PANEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetOrHorizontalFacing()
                .withWater()
                .build();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == FBContent.BLOCK_FRAMED_PANEL.value().asItem())
        {
            Direction facing = state.getValue(FramedProperties.FACING_HOR);
            if (hit.getDirection() == facing.getOpposite())
            {
                if (!level.isClientSide())
                {
                    Direction newFacing = (facing == Direction.NORTH || facing == Direction.EAST) ? facing : facing.getOpposite();
                    BlockState newState = FBContent.BLOCK_FRAMED_DOUBLE_PANEL.value().defaultBlockState();

                    Utils.wrapInStateCopy(level, pos, player, stack, facing != newFacing, true, () ->
                            level.setBlockAndUpdate(pos, newState.setValue(FramedProperties.FACING_NE, newFacing))
                    );

                    SoundType sound = FBContent.BLOCK_FRAMED_CUBE.value().getSoundType(FBContent.BLOCK_FRAMED_CUBE.value().defaultBlockState());
                    level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, mirror);
    }



    public static final ShapeCache<Direction> SHAPES = new ShapeCache<>(new EnumMap<>(Direction.class), map ->
    {
        VoxelShape shape = box(0, 0, 0, 16, 16, 8);
        ShapeUtils.makeHorizontalRotations(shape, Direction.NORTH, map);
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            builder.put(state, SHAPES.get(dir));
        }

        return ShapeProvider.of(builder.build());
    }
}