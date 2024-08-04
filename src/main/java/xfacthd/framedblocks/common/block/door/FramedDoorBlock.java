package xfacthd.framedblocks.common.block.door;

import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.blueprint.BlueprintData;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.IFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.special.FramedDoorBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.blueprint.DoorCopyBehaviour;

import java.util.*;

public class FramedDoorBlock extends DoorBlock implements IFramedBlock
{
    private final BlockType type;

    private FramedDoorBlock(BlockType type, BlockSetType blockSet, Properties props)
    {
        super(blockSet, props);
        this.type = type;
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.SOLID, false)
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        ItemInteractionResult result = handleUse(state, level, pos, player, hand, hit);
        return result.consumesAction() ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        //noinspection ConstantConditions
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos.above()) instanceof FramedDoorBlockEntity be)
        {
            be.applyComponentsFromItemStack(stack);
        }

        tryApplyCamoImmediately(level, pos, placer, stack);
        tryApplyCamoImmediately(level, pos.above(), placer, stack); //Apply to upper half as well
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction side, BlockState adjState, LevelAccessor level, BlockPos pos, BlockPos adjPos)
    {
        BlockState newState = super.updateShape(state, side, adjState, level, pos, adjPos);
        if (newState.getBlock() == this)
        {
            newState = newState.setValue(FramedProperties.SOLID, state.getValue(FramedProperties.SOLID));
            newState = Utils.copyRequiredProperties(state, newState);
        }
        if (newState == state)
        {
            updateCulling(level, pos);
        }
        return newState;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        updateCulling(level, pos);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state)
    {
        return useCamoOcclusionShapeForLightOcclusion(state);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoOcclusionShape(state, level, pos, null);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return getCamoVisualShape(state, level, pos, ctx);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> lines, TooltipFlag flag)
    {
        appendCamoHoverText(stack, lines);
    }

    @Override
    public BlockType getBlockType()
    {
        return type;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoorBlockEntity(pos, state);
    }

    @Override
    public Optional<MutableComponent> printCamoBlock(BlueprintData blueprintData)
    {
        CamoContainer<?, ?> camoContainer = blueprintData.camos().getCamo(0);
        CamoContainer<?, ?> camoContainerTwo = DoorCopyBehaviour.getSecondData(blueprintData).camos().getCamo(0);
        return IFramedDoubleBlock.printCamoData(camoContainer, camoContainerTwo, true);
    }

    @Override
    public Optional<MutableComponent> printCamoData(CamoList camos, boolean blueprint)
    {
        return IFramedDoubleBlock.printCamoData(camos.getCamo(0), camos.getCamo(1), blueprint);
    }

    @Override
    @Nullable
    public BlockState getItemModelSource()
    {
        return null;
    }

    @Override
    public boolean shouldRenderAsBlockInJadeTooltip()
    {
        return false;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }



    public static FramedDoorBlock wood()
    {
        return new FramedDoorBlock(
                BlockType.FRAMED_DOOR,
                BlockSetType.OAK,
                IFramedBlock.createProperties(BlockType.FRAMED_DOOR)
        );
    }

    public static FramedDoorBlock iron()
    {
        return new FramedDoorBlock(
                BlockType.FRAMED_IRON_DOOR,
                BlockSetType.IRON,
                IFramedBlock.createProperties(BlockType.FRAMED_IRON_DOOR)
                        .requiresCorrectToolForDrops()
        );
    }



    public static final class DoorStateMerger implements StateMerger
    {
        public static final DoorStateMerger INSTANCE = new DoorStateMerger();

        private final StateMerger ignoreMerger = StateMerger.ignoring(Utils.concat(
                Set.of(BlockStateProperties.POWERED),
                WrapHelper.IGNORE_SOLID
        ));

        private DoorStateMerger() { }

        @Override
        public BlockState apply(BlockState state)
        {
            state = ignoreMerger.apply(state);
            if (state.getValue(BlockStateProperties.OPEN))
            {
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

        @Override
        public Set<Property<?>> getHandledProperties(Holder<Block> block)
        {
            return Utils.concat(
                    ignoreMerger.getHandledProperties(block),
                    Set.of(BlockStateProperties.OPEN, BlockStateProperties.DOOR_HINGE)
            );
        }
    }
}
