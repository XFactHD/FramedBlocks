package io.github.xfacthd.framedblocks.common.block.door;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMergers;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedDoorBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class FramedDoorBlock extends DoorBlock implements IFramedBlockInternal {
    public static final StateMerger STATE_MERGER = StateMergers.compound(StateMergers.POWERED, new DoorStateMerger());

    private final BlockType type;

    private FramedDoorBlock(BlockType type, BlockSetType blockSet, Properties props) {
        this.type = type;
        super(blockSet, props);
        BlockUtils.configureStandardProperties(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        BlockUtils.addStandardProperties(this, builder);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos.above()) instanceof FramedDoorBlockEntity be) {
            be.applyComponentsFromItemStack(stack);
        }

        tryApplyCamoImmediately(level, pos, placer, stack);
        tryApplyCamoImmediately(level, pos.above(), placer, stack); //Apply to upper half as well
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
        BlockState newState = super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
        if (newState.getBlock() == this) {
            newState = BlockUtils.copyStandardProperties(this, state, newState, false);
        }
        if (newState == state) {
            updateCulling(level, pos);
        }
        return newState;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
        updateCulling(level, pos);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return useCamoOcclusionShapeForLightOcclusion(state);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return getCamoOcclusionShape(state, null);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getCamoVisualShape(state, level, pos, ctx);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return TriState.DEFAULT;
    }

    @Override
    public BlockType getBlockType() {
        return type;
    }

    @Override
    public FramedBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedDoorBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return createStateCycleSpec(this, true);
    }

    static StateCycleSpec createStateCycleSpec(Block block, boolean full) {
        return StateCycleSpec.builder(block)
                .property(FACING, PropertyLabels.FACING)
                .property(HINGE, PropertyLabels.HINGE_SIDE)
                .postProcessor((state, ctx) -> {
                    Level level = ctx.getLevel();
                    BlockPos pos = ctx.getClickedPos();
                    boolean powered = level.hasNeighborSignal(pos) || (full && level.hasNeighborSignal(pos.above()));
                    return state.setValue(OPEN, powered).setValue(POWERED, powered);
                })
                .build();
    }

    @Override
    public boolean shouldRenderAsBlockInJadeTooltip() {
        return false;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return state;
    }

    public static FramedDoorBlock wood(Properties props) {
        return new FramedDoorBlock(
                BlockType.FRAMED_DOOR,
                BlockSetType.OAK,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_DOOR)
        );
    }

    public static FramedDoorBlock iron(Properties props) {
        return new FramedDoorBlock(
                BlockType.FRAMED_IRON_DOOR,
                BlockSetType.IRON,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_IRON_DOOR)
                        .requiresCorrectToolForDrops()
        );
    }

    private static final class DoorStateMerger extends StateMerger {
        private DoorStateMerger() {
            super(Set.of(BlockStateProperties.OPEN, BlockStateProperties.DOOR_HINGE));
        }

        @Override
        public BlockState apply(BlockState state) {
            if (state.getValue(BlockStateProperties.OPEN)) {
                Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                DoorHingeSide hinge = state.getValue(BlockStateProperties.DOOR_HINGE);
                boolean right = hinge == DoorHingeSide.RIGHT;

                // Rotate to the visually equivalent closed variant
                Direction newDir = right ? dir.getCounterClockWise() : dir.getClockWise();
                // Flip hinge to match expected door knob position
                DoorHingeSide newHinge = right ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;

                state = state.setValue(BlockStateProperties.OPEN, false)
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, newDir)
                        .setValue(BlockStateProperties.DOOR_HINGE, newHinge);
            }
            return state;
        }
    }
}
