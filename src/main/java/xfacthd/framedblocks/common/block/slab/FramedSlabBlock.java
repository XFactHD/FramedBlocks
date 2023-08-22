package xfacthd.framedblocks.common.block.slab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.shapes.ShapeCache;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.IdentityHashMap;

@SuppressWarnings("deprecation")
public class FramedSlabBlock extends FramedBlock
{
    public FramedSlabBlock()
    {
        super(BlockType.FRAMED_SLAB);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.TOP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.TOP, BlockStateProperties.WATERLOGGED, FramedProperties.SOLID);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return withWater(
                withTop(
                        defaultBlockState(),
                        context.getClickedFace(),
                        context.getClickLocation()
                ),
                context.getLevel(),
                context.getClickedPos()
        );
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == FBContent.BLOCK_FRAMED_SLAB.get().asItem())
        {
            boolean top = state.getValue(FramedProperties.TOP);
            Direction face = hit.getDirection();
            if ((face == Direction.UP && !top) || (face == Direction.DOWN && top))
            {
                if (!level.isClientSide())
                {
                    Utils.wrapInStateCopy(level, pos, player, stack, top, true, () ->
                            level.setBlockAndUpdate(pos, FBContent.BLOCK_FRAMED_DOUBLE_SLAB.get().defaultBlockState())
                    );

                    SoundType sound = FBContent.BLOCK_FRAMED_CUBE.get().getSoundType(FBContent.BLOCK_FRAMED_CUBE.get().defaultBlockState());
                    level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        return type == PathComputationType.WATER && level.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, Direction side, Rotation rot)
    {
        if (rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }



    public static final ShapeCache<Boolean> SHAPES = new ShapeCache<>(new IdentityHashMap<>(), map ->
    {
        map.put(Boolean.FALSE, box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D));
        map.put(Boolean.TRUE, box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    });

    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for (BlockState state : states)
        {
            builder.put(state, SHAPES.get(state.getValue(FramedProperties.TOP)));
        }

        return ShapeProvider.of(builder.build());
    }
}