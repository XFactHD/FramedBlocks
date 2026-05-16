package io.github.xfacthd.framedblocks.common.block.door;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
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
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FramedTrapDoorBlock extends TrapDoorBlock implements IFramedBlockInternal {
    private final BlockType type;

    private FramedTrapDoorBlock(BlockType type, BlockSetType blockSet, Properties props) {
        this.type = type;
        super(blockSet, props);
        BlockUtils.configureStandardProperties(this);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.ROTATE_TEXTURE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        BlockUtils.addStandardProperties(this, builder);
        builder.add(PropertyHolder.ROTATE_TEXTURE);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.is(FramedConstants.Objects.FRAMED_HAMMER.value())) {
            if (!level.isClientSide()) {
                state = state.setValue(PropertyHolder.ROTATE_TEXTURE, !state.getValue(PropertyHolder.ROTATE_TEXTURE));
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
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
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos) {
        return !state.getValue(OPEN);
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
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.builder(this)
                .property(FACING, PropertyLabels.FACING)
                .property(HALF, PropertyLabels.HALF)
                .postProcessor((state, ctx) -> {
                    boolean powered = ctx.getLevel().hasNeighborSignal(ctx.getClickedPos());
                    return state.setValue(OPEN, powered).setValue(POWERED, powered);
                })
                .reverseCycleOrder()
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }

    public static FramedTrapDoorBlock wood(Properties props) {
        return new FramedTrapDoorBlock(
                BlockType.FRAMED_TRAPDOOR,
                BlockSetType.OAK,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_TRAPDOOR)
        );
    }

    public static FramedTrapDoorBlock iron(Properties props) {
        return new FramedTrapDoorBlock(
                BlockType.FRAMED_IRON_TRAPDOOR,
                BlockSetType.IRON,
                IFramedBlock.applyDefaultProperties(props, BlockType.FRAMED_IRON_TRAPDOOR)
                        .requiresCorrectToolForDrops()
        );
    }
}
