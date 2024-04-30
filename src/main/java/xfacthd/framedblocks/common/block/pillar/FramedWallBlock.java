package xfacthd.framedblocks.common.block.pillar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.api.shapes.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FramedWallBlock extends WallBlock implements IFramedBlock
{
    private static final Map<Direction, EnumProperty<WallSide>> PROPERTY_BY_DIRECTION = Map.of(
            Direction.NORTH, NORTH_WALL,
            Direction.EAST, EAST_WALL,
            Direction.SOUTH, SOUTH_WALL,
            Direction.WEST, WEST_WALL
    );
    private final ShapeProvider shapes = makeShapeProvider(states -> generateShapes(states, 14F, 16F));
    private final ShapeProvider collisionShapes = makeShapeProvider(states -> generateShapes(states, 24F, 24F));

    public FramedWallBlock()
    {
        super(IFramedBlock.createProperties(BlockType.FRAMED_WALL));
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.STATE_LOCKED, false)
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.STATE_LOCKED, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected ItemInteractionResult useItemOn(
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
    protected BlockState updateShape(
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
    public boolean connectsTo(BlockState adjState, boolean sideSolid, Direction adjSide)
    {
        if (!Utils.isY(adjSide) && adjState.getBlock() == this && adjState.getValue(FramedProperties.STATE_LOCKED))
        {
            EnumProperty<WallSide> prop = PROPERTY_BY_DIRECTION.get(adjSide);
            if (adjState.getValue(prop) == WallSide.NONE)
            {
                return false;
            }
        }
        return super.connectsTo(adjState, sideSolid, adjSide);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        updateCulling(level, pos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return shapes.get(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return collisionShapes.get(state);
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
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return true;
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

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(EAST_WALL, WallSide.LOW).setValue(WEST_WALL, WallSide.LOW);
    }

    @Override
    protected Map<BlockState, VoxelShape> makeShapes(float pWidth, float pDepth, float pWallPostHeight, float pWallMinY, float pWallLowHeight, float pWallTallHeight)
    {
        // Effectively NO-OP to conserve memory, the shape building is taken over below
        return Map.of();
    }

    private ShapeProvider makeShapeProvider(ShapeGenerator generator)
    {
        ImmutableList<BlockState> states = stateDefinition.getPossibleStates();
        if (!FMLEnvironment.production)
        {
            return new ReloadableShapeProvider(generator, states);
        }
        return generator.generate(states);
    }



    private static ShapeProvider generateShapes(
            ImmutableList<BlockState> states,
            float lowHeight,
            float tallHeight
    )
    {
        boolean sameHeight = lowHeight == tallHeight;

        VoxelShape centerLowShape = box(5F, 0F, 5F, 11F, lowHeight, 11F);
        VoxelShape centerTallShape = sameHeight ? centerLowShape : box(5F, 0F, 5F, 11F, tallHeight, 11F);
        VoxelShape postShape = box(4F, 0F, 4F, 12F, tallHeight, 12F);
        VoxelShape wallLowShape = box(5F, 0F, 0F, 11F, lowHeight, 5F);
        VoxelShape wallTallShape = sameHeight ? wallLowShape : box(5F, 0F, 0F, 11F, tallHeight, 5F);

        VoxelShape[] wallLowShapes = ShapeUtils.makeHorizontalRotations(wallLowShape, Direction.NORTH);
        VoxelShape[] wallTallShapes = sameHeight ? wallLowShapes : ShapeUtils.makeHorizontalRotations(wallTallShape, Direction.NORTH);

        VoxelShape[] shapes = new VoxelShape[512];
        for (WallSide north : NORTH_WALL.getPossibleValues())
        {
            for (WallSide east : EAST_WALL.getPossibleValues())
            {
                for (WallSide south : SOUTH_WALL.getPossibleValues())
                {
                    for (WallSide west : WEST_WALL.getPossibleValues())
                    {
                        int noUpKey = makeShapeKey(false, north, east, south, west);
                        int upKey = makeShapeKey(true, north, east, south, west);

                        boolean anyTall = north == WallSide.TALL || east == WallSide.TALL || south == WallSide.TALL || west == WallSide.TALL;
                        shapes[noUpKey] = ShapeUtils.or(
                                anyTall ? centerTallShape : centerLowShape,
                                getWallShape(north, Direction.NORTH, wallLowShapes, wallTallShapes),
                                getWallShape(east, Direction.EAST, wallLowShapes, wallTallShapes),
                                getWallShape(south, Direction.SOUTH, wallLowShapes, wallTallShapes),
                                getWallShape(west, Direction.WEST, wallLowShapes, wallTallShapes)
                        );

                        shapes[upKey] = ShapeUtils.or(
                                postShape,
                                getWallShape(north, Direction.NORTH, wallLowShapes, wallTallShapes),
                                getWallShape(east, Direction.EAST, wallLowShapes, wallTallShapes),
                                getWallShape(south, Direction.SOUTH, wallLowShapes, wallTallShapes),
                                getWallShape(west, Direction.WEST, wallLowShapes, wallTallShapes)
                        );
                    }
                }
            }
        }
        shapes[0] = Shapes.block();

        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (BlockState state : states)
        {
            int key = makeShapeKey(
                    state.getValue(UP),
                    state.getValue(NORTH_WALL),
                    state.getValue(EAST_WALL),
                    state.getValue(SOUTH_WALL),
                    state.getValue(WEST_WALL)
            );
            builder.put(state, shapes[key]);
        }
        return ShapeProvider.of(builder.build());
    }

    private static VoxelShape getWallShape(WallSide side, Direction dir, VoxelShape[] lowShapes, VoxelShape[] tallShapes)
    {
        return switch (side)
        {
            case NONE -> Shapes.empty();
            case LOW -> lowShapes[dir.get2DDataValue()];
            case TALL -> tallShapes[dir.get2DDataValue()];
        };
    }

    private static int makeShapeKey(boolean up, WallSide north, WallSide east, WallSide south, WallSide west)
    {
        return ((up ? 1 : 0) << 8) | (north.ordinal() << 6) | (east.ordinal() << 4) | (south.ordinal() << 2) | west.ordinal();
    }
}
