package io.github.xfacthd.framedblocks.common.block.cube;

import io.github.xfacthd.framedblocks.api.block.CopycatStyleBlock;
import io.github.xfacthd.framedblocks.api.block.PlacementStateBuilder;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.FramedBlock;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleCubeBlockEntity;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FramedCollapsibleCubeBlock extends FramedBlock implements CopycatStyleBlock.StateDependent {
    private static final int UP = Direction.UP.ordinal();
    private static final int DOWN = Direction.DOWN.ordinal();
    private static final int NORTH = Direction.NORTH.ordinal();
    private static final int EAST = Direction.EAST.ordinal();
    private static final int SOUTH = Direction.SOUTH.ordinal();
    private static final int WEST = Direction.WEST.ordinal();
    private static final Map<Integer, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<>();
    public static final int ALL_SOLID = 0b00111111;

    public FramedCollapsibleCubeBlock(Properties props) {
        super(BlockType.FRAMED_COLLAPSIBLE_CUBE, props.dynamicShape());
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.SOLID_FACES, ALL_SOLID));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.SOLID_FACES);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return PlacementStateBuilder.of(this, ctx).withWater().build();
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        if (player.getMainHandItem().getItem() == FBContent.ITEM_FRAMED_HAMMER.value()) {
            if (level.getBlockEntity(pos) instanceof FramedCollapsibleCubeBlockEntity be) {
                if (!level.isClientSide()) {
                    be.handleDeform(player);
                }
                return true;
            }
        }
        return super.handleBlockLeftClick(state, level, pos, player);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (isIntangible(state, level, pos, ctx)) {
            return Shapes.empty();
        }

        int solid = state.getValue(PropertyHolder.SOLID_FACES);
        if (solid != ALL_SOLID && level.getBlockEntity(pos) instanceof FramedCollapsibleCubeBlockEntity be) {
            return SHAPE_CACHE.computeIfAbsent(be.getPackedOffsets(state), key -> {
                byte[] offsets = FramedCollapsibleCubeBlockEntity.unpackOffsets(key);
                return box(
                        offsets[WEST],
                        offsets[DOWN],
                        offsets[NORTH],
                        16 - offsets[EAST],
                        16 - offsets[UP],
                        16 - offsets[SOUTH]
                );
            });
        }
        return Shapes.block();
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && stack.has(FBContent.DC_TYPE_COLLAPSIBLE_CUBE_DATA)) {
            //Properly set face solidity when placed from a stack with BE NBT data
            if (level.getBlockEntity(pos) instanceof FramedCollapsibleCubeBlockEntity be) {
                be.updateFaceSolidity();
            }
        }
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        int solidFaces = state.getValue(PropertyHolder.SOLID_FACES);
        solidFaces = FramedCollapsibleCubeBlockEntity.rotateSolidFaces(solidFaces, rotation);
        return state.setValue(PropertyHolder.SOLID_FACES, solidFaces);
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }

        int solidFaces = state.getValue(PropertyHolder.SOLID_FACES);
        solidFaces = FramedCollapsibleCubeBlockEntity.mirrorSolidFaces(solidFaces, mirror);
        return state.setValue(PropertyHolder.SOLID_FACES, solidFaces);
    }

    @Override
    public TriState shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode mode, BlockState oldState, BlockState newState) {
        return TriState.TRUE;
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof FramedCollapsibleCubeBlockEntity be) {
            return be.doesOccludeBeaconBeam();
        }
        return false;
    }

    @Override
    public FramedBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FramedCollapsibleCubeBlockEntity(pos, state);
    }

    @Override
    public BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.UNSUPPORTED;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return state;
    }
}
