package xfacthd.framedblocks.common.block.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.CtmPredicate;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class FramedTrapDoorBlock extends TrapDoorBlock implements IFramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        if (state.getValue(BlockStateProperties.OPEN))
        {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite() == dir;
        }
        else if (state.getValue(BlockStateProperties.HALF) == Half.TOP)
        {
            return dir == Direction.UP;
        }
        return dir == Direction.DOWN;
    };
    private static final int[] SOUNDS = new int[] {
            LevelEvent.SOUND_OPEN_WOODEN_TRAP_DOOR,
            LevelEvent.SOUND_CLOSE_WOODEN_TRAP_DOOR,
            LevelEvent.SOUND_OPEN_IRON_TRAP_DOOR,
            LevelEvent.SOUND_CLOSE_IRON_TRAP_DOOR
    };

    private final BlockType type;

    private FramedTrapDoorBlock(BlockType type, Properties props)
    {
        super(props);
        this.type = type;
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.SOLID, false)
                .setValue(FramedProperties.GLOWING, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID, FramedProperties.GLOWING);
    }

    @Override
    public final InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        InteractionResult result = handleUse(state, level, pos, player, hand, hit);
        if (result.consumesAction()) { return result; }

        return material == FramedDoorBlock.IRON_WOOD ? InteractionResult.PASS :super.use(state, level, pos, player, hand, hit);
    }

    @Override
    protected void playSound(@Nullable Player player, Level level, BlockPos pos, boolean isOpened)
    {
        int sound = (isOpened ? 0 : 1) + (material == FramedDoorBlock.IRON_WOOD ? 2 : 0);
        level.levelEvent(player, SOUNDS[sound], pos, 0);

        level.gameEvent(player, isOpened ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        BlockState newState = super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        if (level.isClientSide() && newState == state)
        {
            updateCulling(level, currentPos, facingState, facing, false);
        }
        return newState;
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState)
    {
        onStateChange(level, pos, oldState, newState);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) { return useCamoOcclusionShapeForLightOcclusion(state); }

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
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) { return getLight(state, level, pos); }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity)
    {
        return getCamoSound(state, level, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return getCamoExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        return getCamoSlipperiness(state, level, pos, entity);
    }

    @Override
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir)
    {
        return doesHideNeighborFace(level, pos, state, neighborState, dir);
    }

    @Override
    public MaterialColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MaterialColor defaultColor)
    {
        return getCamoMapColor(level, pos, defaultColor);
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        if (!state.getValue(OPEN))
        {
            return getCamoBeaconColorMultiplier(level, pos, beaconPos);
        }
        return null;
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction side, IPlantable plant)
    {
        return canCamoSustainPlant(state, level, pos, side, plant);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedBlockEntity(pos, state); }

    @Override
    public BlockType getBlockType() { return type; }



    public static FramedTrapDoorBlock wood()
    {
        return new FramedTrapDoorBlock(
                BlockType.FRAMED_TRAPDOOR,
                IFramedBlock.createProperties(BlockType.FRAMED_TRAPDOOR)
        );
    }

    public static FramedTrapDoorBlock iron()
    {
        return new FramedTrapDoorBlock(
                BlockType.FRAMED_IRON_TRAPDOOR,
                IFramedBlock.createProperties(BlockType.FRAMED_IRON_TRAPDOOR, FramedDoorBlock.IRON_WOOD)
                        .requiresCorrectToolForDrops()
        );
    }
}