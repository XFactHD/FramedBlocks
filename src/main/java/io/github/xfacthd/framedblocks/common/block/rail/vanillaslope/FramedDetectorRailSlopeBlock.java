package io.github.xfacthd.framedblocks.common.block.rail.vanillaslope;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.shapes.ShapeLookup;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.block.ISlopeBlock;
import io.github.xfacthd.framedblocks.common.block.rail.fancyslope.FramedFancyDetectorRailSlopeBlock;
import io.github.xfacthd.framedblocks.common.blockentity.doubled.rail.FramedFancyRailSlopeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.util.FramedUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class FramedDetectorRailSlopeBlock<BE extends FramedBlockEntity> extends DetectorRailBlock implements IFramedBlockInternal, ISlopeBlock.IRailSlopeBlock
{
    private final BlockType type;
    private final ShapeLookup shapes;
    private final BlockEntityType.BlockEntitySupplier<BE> beFactory;

    protected FramedDetectorRailSlopeBlock(BlockType type, Properties props, BlockEntityType.BlockEntitySupplier<BE> beFactory)
    {
        super(IFramedBlock.applyDefaultProperties(props, type));
        this.type = type;
        this.shapes = ShapeLookup.of(this);
        this.beFactory = beFactory;
        BlockUtils.configureStandardProperties(this);
        registerDefaultState(defaultBlockState()
                .setValue(POWERED, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void registerDefaultState() { }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        BlockUtils.addRequiredProperties(builder);
        builder.add(FramedProperties.SOLID, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        return Objects.requireNonNull(PlacementStateBuilder.of(this, ctx)
                .withCustom((state, modCtx) -> state.setValue(
                        PropertyHolder.ASCENDING_RAIL_SHAPE,
                        FramedUtils.getAscendingRailShapeFromDirection(modCtx.getHorizontalDirection())
                ))
                .withWater()
                .build());
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
    )
    {
        BlockState newState = super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
        if (newState == state)
        {
            updateCulling(level, pos);
        }
        return newState;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return true;
    }

    @Override //Copy of AbstractRailBlock#neighborChanged() to disable removal
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving)
    {
        updateCulling(level, pos);
        if (!level.isClientSide() && level.getBlockState(pos).is(this))
        {
            updateState(state, level, pos, block);
        }
    }

    @Override
    public Property<RailShape> getShapeProperty()
    {
        return PropertyHolder.ASCENDING_RAIL_SHAPE;
    }

    @Override
    public boolean isValidRailShape(RailShape shape)
    {
        return shape.isSlope();
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state)
    {
        return useCamoOcclusionShapeForLightOcclusion(state);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state)
    {
        return getCamoOcclusionShape(state, shapes);
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
    protected boolean propagatesSkylightDown(BlockState state)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return shapes.getOcclusionShape(state);
    }

    @Override //The default implementation defers to the AbstractBlock#getShape() overload without ISelectionContext argument
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        if (context instanceof EntityCollisionContext ctx && ctx.getEntity() instanceof AbstractMinecart)
        {
            return Shapes.empty();
        }
        return getShape(state, worldIn, pos, context);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        Direction dir = FramedUtils.getDirectionFromAscendingRailShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
        dir = rotation.rotate(dir);
        return state.setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, FramedUtils.getAscendingRailShapeFromDirection(dir));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE) { return state; }

        Direction dir = FramedUtils.getDirectionFromAscendingRailShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));

        if ((mirror == Mirror.FRONT_BACK && Utils.isZ(dir)) || (mirror == Mirror.LEFT_RIGHT && Utils.isX(dir)))
        {
            dir = dir.getOpposite();
            return state.setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, FramedUtils.getAscendingRailShapeFromDirection(dir));
        }
        return state;
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return true;
    }

    @Override
    public final BE newBlockEntity(BlockPos pos, BlockState state)
    {
        return beFactory.create(pos, state);
    }

    @Override
    public BlockType getBlockType()
    {
        return type;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, RailShape.ASCENDING_SOUTH);
    }

    @Override
    public Direction getHorizontalOrientation(BlockState state)
    {
        return FramedUtils.getDirectionFromAscendingRailShape(state.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE));
    }

    @Override
    public Class<? extends Block> getJadeTargetClass()
    {
        return FramedDetectorRailSlopeBlock.class;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return getItemModelSource();
    }



    public static FramedDetectorRailSlopeBlock<FramedBlockEntity> normal(Properties props)
    {
        return new FramedDetectorRailSlopeBlock<>(
                BlockType.FRAMED_DETECTOR_RAIL_SLOPE,
                props,
                FBContent.getDefaultBlockEntityFactory()
        );
    }

    public static FramedDetectorRailSlopeBlock<FramedDoubleBlockEntity> fancy(Properties props)
    {
        return new FramedFancyDetectorRailSlopeBlock(props, FramedFancyRailSlopeBlockEntity::new);
    }
}
