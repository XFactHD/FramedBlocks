package xfacthd.framedblocks.common.block.pillar;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class FramedWallBlock extends WallBlock implements IFramedBlock
{
    public FramedWallBlock()
    {
        super(IFramedBlock.createProperties(BlockType.FRAMED_WALL));
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.STATE_LOCKED, false)
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false)
        );
        fixShapeMaps();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.STATE_LOCKED, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    public final InteractionResult use(
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
            BlockPos currentPos,
            BlockPos facingPos
    )
    {
        BlockState newState = updateShapeLockable(
                state, level, currentPos,
                () -> super.updateShape(state, facing, facingState, level, currentPos, facingPos)
        );

        if (newState == state)
        {
            updateCulling(level, currentPos);
        }
        return newState;
    }

    @Override
    public void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving
    )
    {
        updateCulling(level, pos);
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
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public BlockType getBlockType()
    {
        return BlockType.FRAMED_WALL;
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedBlockRenderProperties.INSTANCE);
    }



    private void fixShapeMaps()
    {
        Map<BlockState, VoxelShape> shapeByIndex = ObfuscationReflectionHelper.getPrivateValue(WallBlock.class, this, "f_57955_");
        shapeByIndex = fixShapeMap(shapeByIndex);
        ObfuscationReflectionHelper.setPrivateValue(WallBlock.class, this, shapeByIndex, "f_57955_");

        Map<BlockState, VoxelShape> collisionShapeByIndex = ObfuscationReflectionHelper.getPrivateValue(WallBlock.class, this, "f_57956_");
        collisionShapeByIndex = fixShapeMap(collisionShapeByIndex);
        ObfuscationReflectionHelper.setPrivateValue(WallBlock.class, this, collisionShapeByIndex, "f_57956_");
    }

    private static Map<BlockState, VoxelShape> fixShapeMap(Map<BlockState, VoxelShape> map)
    {
        Preconditions.checkNotNull(map, "Got a null map?!");

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        builder.putAll(map);

        for (BlockState state : map.keySet())
        {
            VoxelShape shape = map.get(state);
            builder.put(state.cycle(FramedProperties.STATE_LOCKED), shape);
            builder.put(state.cycle(FramedProperties.GLOWING), shape);
            builder.put(state.cycle(FramedProperties.PROPAGATES_SKYLIGHT), shape);
            builder.put(state.cycle(FramedProperties.GLOWING).cycle(FramedProperties.STATE_LOCKED), shape);
            builder.put(state.cycle(FramedProperties.PROPAGATES_SKYLIGHT).cycle(FramedProperties.STATE_LOCKED), shape);
            builder.put(state.cycle(FramedProperties.PROPAGATES_SKYLIGHT).cycle(FramedProperties.GLOWING), shape);
            builder.put(state.cycle(FramedProperties.PROPAGATES_SKYLIGHT).cycle(FramedProperties.GLOWING).cycle(FramedProperties.STATE_LOCKED), shape);
        }

        return builder.build();
    }
}