package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.block.render.ParticleHelper;
import io.github.xfacthd.framedblocks.api.camo.block.BlockCamoContent;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedOwnableBlockEntity;
import io.github.xfacthd.framedblocks.common.config.ServerConfig;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class FramedOneWayWindowBlock extends FramedBlock {
    public static final BlockCamoContent GLASS_DUMMY_CAMO = new BlockCamoContent(Blocks.TINTED_GLASS.defaultBlockState());
    public static final String LABEL_WINDOW_FACE = Utils.translationKey("label", "state_cycling.property.one_way_window.window_face");

    public FramedOneWayWindowBlock(Properties props) {
        super(BlockType.FRAMED_ONE_WAY_WINDOW, props);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.NULLABLE_FACE);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player player && level.getBlockEntity(pos) instanceof FramedOwnableBlockEntity be) {
            be.setOwner(player.getUUID(), true);
        }
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.getMainHandItem().is(FBContent.ITEM_FRAMED_WRENCH.value()) && isOwnedBy(level, pos, player)) {
            if (!level.isClientSide()) {
                if (player.isShiftKeyDown()) {
                    level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.NONE));
                } else {
                    HitResult hit = player.pick(10D, 0, false);
                    if (!(hit instanceof BlockHitResult blockHit)) {
                        return false;
                    }

                    NullableDirection face =  NullableDirection.fromDirection(blockHit.getDirection());
                    level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.NULLABLE_FACE, face));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        if (state.getValue(PropertyHolder.NULLABLE_FACE) != NullableDirection.NONE) {
            return Shapes.empty();
        }
        return super.getOcclusionShape(state);
    }

    @Override
    public boolean canOccludeNeighbor(BlockGetter level, BlockPos pos, BlockState state, BlockPos adjPos, BlockState adjState) {
        if (adjState.getBlock() != FBContent.BLOCK_FRAMED_ONE_WAY_WINDOW.value()) {
            return false;
        }
        return state.getValue(PropertyHolder.NULLABLE_FACE) == adjState.getValue(PropertyHolder.NULLABLE_FACE);
    }

    @Override
    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (state.getValue(PropertyHolder.NULLABLE_FACE) == NullableDirection.UP) {
            ParticleHelper.spawnRunningParticles(GLASS_DUMMY_CAMO, level, pos, entity);
            return true;
        }
        return super.addRunningEffects(state, level, pos, entity);
    }

    @Override
    public boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState sameState, LivingEntity entity, int count) {
        if (state.getValue(PropertyHolder.NULLABLE_FACE) == NullableDirection.UP) {
            ParticleHelper.spawnLandingParticles(GLASS_DUMMY_CAMO, level, pos, entity, count);
            return true;
        }
        return super.addLandingEffects(state, level, pos, sameState, entity, count);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        return state.setValue(PropertyHolder.NULLABLE_FACE, face.rotate(rotation));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        return state.setValue(PropertyHolder.NULLABLE_FACE, face.mirror(mirror));
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return TriState.DEFAULT;
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndLightGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        Direction dir = state.getValue(PropertyHolder.NULLABLE_FACE).toNullableDirection();
        if (dir == side && queryPos != null) {
            if (DirUtils.dirByNormal(pos, queryPos) == dir) {
                return Blocks.TINTED_GLASS.defaultBlockState();
            }
            if (queryState == null) {
                queryState = level.getBlockState(queryPos);
            }
            if (queryState.is(this) && queryState.getValue(PropertyHolder.NULLABLE_FACE).toNullableDirection() == dir) {
                return Blocks.TINTED_GLASS.defaultBlockState();
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }

    @Override
    public FramedBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedOwnableBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.builder(this)
                .property(PropertyHolder.NULLABLE_FACE, builder -> builder
                        .values(NullableDirection.CYCLE_ORDER)
                        .printer(LABEL_WINDOW_FACE)
                )
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return state.setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.EAST);
    }

    public static boolean isOwnedBy(BlockGetter level, BlockPos pos, Player player) {
        if (!ServerConfig.VIEW.isOneWayWindowOwnable()) {
            return true;
        }
        if (level.getBlockEntity(pos) instanceof FramedOwnableBlockEntity be) {
            return player.getUUID().equals(be.getOwner());
        }
        return false;
    }
}
