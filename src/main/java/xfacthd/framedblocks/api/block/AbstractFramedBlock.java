package xfacthd.framedblocks.api.block;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenCustomHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.common.IPlantable;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.api.shapes.ShapeProvider;
import xfacthd.framedblocks.api.shapes.ShapeUtils;
import xfacthd.framedblocks.api.type.IBlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public abstract class AbstractFramedBlock extends Block implements IFramedBlock, SimpleWaterloggedBlock
{
    private static final VoxelShape BEACON_BEAM_SHAPE = box(5, 0, 5, 11, 16, 11);
    private final IBlockType blockType;
    private final ShapeProvider shapes;
    private final Object2BooleanMap<BlockState> beaconBeamOcclusion;

    public AbstractFramedBlock(IBlockType blockType, Properties props)
    {
        super(props);
        this.blockType = blockType;
        this.shapes = blockType.generateShapes(getStateDefinition().getPossibleStates());
        this.beaconBeamOcclusion = computeBeaconBeamOcclusion(shapes);

        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false)
        );

        if (blockType.canOccludeWithSolidCamo())
        {
            registerDefaultState(defaultBlockState()
                    .setValue(FramedProperties.SOLID, false)
            );
        }
        if (blockType.supportsWaterLogging())
        {
            registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
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
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState facingState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos facingPos
    )
    {
        updateCulling(level, pos);
        if (isWaterLoggable() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving
    )
    {
        updateCulling(level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, ctx))
        {
            return Shapes.empty();
        }
        return shapes.get(state);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state)
    {
        return useCamoOcclusionShapeForLightOcclusion(state);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoOcclusionShape(state, level, pos);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return getCamoVisualShape(state, level, pos, ctx);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        if (isWaterLoggable() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            return Fluids.WATER.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid)
    {
        return isWaterLoggable() && SimpleWaterloggedBlock.super.canPlaceLiquid(level, pos, state, fluid);
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState)
    {
        return isWaterLoggable() && SimpleWaterloggedBlock.super.placeLiquid(pLevel, pPos, pState, pFluidState);
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (!isWaterLoggable())
        {
            return ItemStack.EMPTY;
        }
        return SimpleWaterloggedBlock.super.pickupBlock(level, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    /**
     * Return true if the given {@link BlockState} occludes the full area of the beacon beam and
     * can therefore tint the beam
     */
    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        Preconditions.checkNotNull(
                beaconBeamOcclusion,
                "Block '%s' handles shapes itself but doesn't override AbstractFramedBlock#doesBlockMaskBeaconBeam()",
                this
        );
        return beaconBeamOcclusion.getBoolean(state);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction side, IPlantable plant)
    {
        return canCamoSustainPlant(state, level, pos, side, plant);
    }

    @Override
    public IBlockType getBlockType()
    {
        return blockType;
    }

    protected final boolean isWaterLoggable()
    {
        return blockType.supportsWaterLogging();
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedBlockRenderProperties.INSTANCE);
    }



    private static Object2BooleanMap<BlockState> computeBeaconBeamOcclusion(ShapeProvider shapes)
    {
        if (shapes.isEmpty())
        {
            return null;
        }

        Object2BooleanMap<BlockState> beamColorMasking = new Object2BooleanOpenCustomHashMap<>(Util.identityStrategy());

        shapes.forEach((state, shape) ->
        {
            VoxelShape intersection = ShapeUtils.andUnoptimized(shape, BEACON_BEAM_SHAPE);

            beamColorMasking.put(
                    state,
                    intersection.min(Direction.Axis.X) <= BEACON_BEAM_SHAPE.min(Direction.Axis.X) &&
                    intersection.min(Direction.Axis.Z) <= BEACON_BEAM_SHAPE.min(Direction.Axis.Z) &&
                    intersection.max(Direction.Axis.X) >= BEACON_BEAM_SHAPE.max(Direction.Axis.X) &&
                    intersection.max(Direction.Axis.Z) >= BEACON_BEAM_SHAPE.max(Direction.Axis.Z)
            );
        });

        return beamColorMasking;
    }
}