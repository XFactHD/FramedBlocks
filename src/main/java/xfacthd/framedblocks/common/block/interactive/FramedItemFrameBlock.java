package xfacthd.framedblocks.common.block.interactive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.PlacementStateBuilder;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedItemFrameBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class FramedItemFrameBlock extends FramedBlock
{
    private static final SoundType NORMAL_SOUND = new SoundType(
            1F, 1F, SoundEvents.ITEM_FRAME_BREAK, SoundEvents.EMPTY, SoundEvents.ITEM_FRAME_PLACE, SoundEvents.SCAFFOLDING_HIT, SoundEvents.EMPTY
    );
    private static final SoundType GLOWING_SOUND = new SoundType(
            1F, 1F, SoundEvents.GLOW_ITEM_FRAME_BREAK, SoundEvents.EMPTY, SoundEvents.GLOW_ITEM_FRAME_PLACE, SoundEvents.SCAFFOLDING_HIT, SoundEvents.EMPTY
    );

    public FramedItemFrameBlock(BlockType type)
    {
        super(type, IFramedBlock.createProperties(type)
                .instabreak()
                .noCollission()
                .isSuffocating((s, l, p) -> false)
                .isViewBlocking((s, l, p) -> false)
                .sound(type == BlockType.FRAMED_ITEM_FRAME ? NORMAL_SOUND : GLOWING_SOUND)
        );
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.LEATHER, false)
                .setValue(PropertyHolder.MAP_FRAME, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, PropertyHolder.LEATHER, PropertyHolder.MAP_FRAME);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetFacing()
                .validate((state, modCtx) -> canSurvive(state, modCtx.getLevel(), modCtx.getClickedPos()))
                .build();
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos neighborPos
    )
    {
        if (!canSurvive(state, level, currentPos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        return Block.canSupportRigidBlock(level, pos.relative(dir));
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result.consumesAction()) { return result; }

        if (level.getBlockEntity(pos) instanceof FramedItemFrameBlockEntity be)
        {
            return be.handleFrameInteraction(player, hand);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().is(FBContent.ITEM_FRAMED_HAMMER.value()))
        {
            if (!level.isClientSide())
            {
                level.setBlockAndUpdate(pos, state.cycle(PropertyHolder.LEATHER));
            }
            return true;
        }
        else if (level.getBlockEntity(pos) instanceof FramedItemFrameBlockEntity be && be.hasItem())
        {
            if (!level.isClientSide())
            {
                be.removeItem(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity)
    {
        //No camo sounds here
        return getSoundType(state);
    }

    @Override
    public boolean addLandingEffects(
            BlockState state1,
            ServerLevel level,
            BlockPos pos,
            BlockState state2,
            LivingEntity entity,
            int numberOfParticles
    )
    {
        //Suppress landing impact particles
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        //Suppress sprinting particles
        return true;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        if (level.getBlockEntity(pos) instanceof FramedItemFrameBlockEntity be && be.hasItem())
        {
            return be.getCloneItem();
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        //Not rotatable by wrench
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        return state.setValue(BlockStateProperties.FACING, rot.rotate(dir));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, BlockStateProperties.FACING, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedItemFrameBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (!level.isClientSide() && state.getValue(PropertyHolder.MAP_FRAME))
        {
            return Utils.createBlockEntityTicker(
                    type, FBContent.BE_TYPE_FRAMED_ITEM_FRAME.value(), (l, p, s, be) -> be.tickWithMap()
            );
        }
        return null;
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        //Suppress hit and destroy particles
        consumer.accept(new FramedBlockRenderProperties()
        {
            @Override
            public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager)
            {
                return true;
            }

            @Override
            public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager)
            {
                return true;
            }
        });
    }



    public static ShapeProvider generateShapes(ImmutableList<BlockState> states)
    {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        VoxelShape topShape = box(2, 15, 2, 14, 16, 14);
        VoxelShape topMapShape = box(0, 15, 0, 16, 16, 16);
        VoxelShape bottomShape = box(2, 0, 2, 14, 1, 14);
        VoxelShape bottomMapShape = box(0, 0, 0, 16, 1, 16);
        VoxelShape northShape = box(2, 2, 0, 14, 14, 1);
        VoxelShape northMapShape = box(0, 0, 0, 16, 16, 1);
        VoxelShape[] horShapes = ShapeUtils.makeHorizontalRotationsWithFlag(northShape, northMapShape, Direction.NORTH);

        for (BlockState state : states)
        {
            Direction dir = state.getValue(BlockStateProperties.FACING);
            boolean map = state.getValue(PropertyHolder.MAP_FRAME);
            builder.put(state, switch (dir)
            {
                case UP -> map ? topMapShape : topShape;
                case DOWN -> map ? bottomMapShape : bottomShape;
                default -> horShapes[dir.get2DDataValue() + (map ? 4 : 0)];
            });
        }

        return ShapeProvider.of(builder.build());
    }
}
