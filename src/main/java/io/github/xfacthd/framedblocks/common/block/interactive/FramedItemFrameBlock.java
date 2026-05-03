package io.github.xfacthd.framedblocks.common.block.interactive;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.item.FramedBlockItem;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedItemFrameBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class FramedItemFrameBlock extends FramedBlock {
    @SuppressWarnings("deprecation")
    private static final SoundType NORMAL_SOUND = new SoundType(
            1F, 1F, SoundEvents.ITEM_FRAME_BREAK, SoundEvents.EMPTY, SoundEvents.ITEM_FRAME_PLACE, SoundEvents.SCAFFOLDING_HIT, SoundEvents.EMPTY
    );
    @SuppressWarnings("deprecation")
    private static final SoundType GLOWING_SOUND = new SoundType(
            1F, 1F, SoundEvents.GLOW_ITEM_FRAME_BREAK, SoundEvents.EMPTY, SoundEvents.GLOW_ITEM_FRAME_PLACE, SoundEvents.SCAFFOLDING_HIT, SoundEvents.EMPTY
    );

    public FramedItemFrameBlock(BlockType type, Properties props) {
        super(type, props, modProps -> modProps.instabreak()
                .noCollision()
                .isSuffocating((_, _, _) -> false)
                .isViewBlocking((_, _, _) -> false)
                .sound(type == BlockType.FRAMED_ITEM_FRAME ? NORMAL_SOUND : GLOWING_SOUND)
        );
        registerDefaultState(defaultBlockState()
                .setValue(PropertyHolder.LEATHER, false)
                .setValue(PropertyHolder.MAP_FRAME, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, PropertyHolder.LEATHER, PropertyHolder.MAP_FRAME);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx)
                .withTargetFacing()
                .withWater()
                .validate((state, modCtx) -> canSurvive(state, modCtx.getLevel(), modCtx.getClickedPos()))
                .build();
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random
    ) {
        if (!canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        return Block.canSupportRigidBlock(level, pos.relative(dir));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hit);
        if (result.consumesAction()) {
            return result;
        }
        if (level.getBlockEntity(pos) instanceof FramedItemFrameBlockEntity be) {
            return be.handleFrameInteraction(player, hand);
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.getMainHandItem().is(FBContent.ITEM_FRAMED_HAMMER.value())) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, state.cycle(PropertyHolder.LEATHER));
            }
            return true;
        } else if (level.getBlockEntity(pos) instanceof FramedItemFrameBlockEntity be && be.hasItem()) {
            if (!level.isClientSide()) {
                be.removeItem(player);
            }
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
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
    ) {
        //Suppress landing impact particles
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        //Suppress sprinting particles
        return true;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        if (level.getBlockEntity(pos) instanceof FramedItemFrameBlockEntity be && be.hasItem()) {
            return be.getCloneItem();
        }
        return super.getCloneItemStack(level, pos, state, includeData, player);
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        //Not rotatable by wrench
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return BlockUtils.rotate(state, BlockStateProperties.FACING, rotation);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return BlockUtils.mirrorFaceBlock(state, BlockStateProperties.FACING, mirror);
    }

    @Override
    public FramedBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedItemFrameBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide() && state.getValue(PropertyHolder.MAP_FRAME)) {
            return BlockUtils.createBlockEntityTicker(
                    type, FBContent.BE_TYPE_FRAMED_ITEM_FRAME.value(), (_, _, _, be) -> be.tickWithMap()
            );
        }
        return null;
    }

    @Override
    public IFramedBlockItem createBlockItem(Item.Properties props) {
        return new FramedBlockItem(this, props, true);
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return state.setValue(BlockStateProperties.FACING, Direction.SOUTH);
    }

    @Override
    public float getJadeRenderScale(BlockState state) {
        return 1.3F;
    }
}
