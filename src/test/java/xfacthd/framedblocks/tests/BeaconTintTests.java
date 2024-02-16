package xfacthd.framedblocks.tests;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.gametest.GameTestHolder;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.test.TestUtils;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;

import java.util.*;

@GameTestHolder(FramedConstants.MOD_ID)
public final class BeaconTintTests
{
    private static final String BATCH_NAME = "beacon_tint";
    private static final String STRUCTURE_NAME = FramedConstants.MOD_ID + ":floor_slab_1x1";

    // All blocks that are completely unable to apply a tint to the beacon beam
    private static final Set<BlockType> NON_TINTING = Set.of(
            BlockType.FRAMED_SLOPE_EDGE,
            BlockType.FRAMED_SLAB_EDGE,
            BlockType.FRAMED_SLAB_CORNER,
            BlockType.FRAMED_PANEL,
            BlockType.FRAMED_CORNER_PILLAR,
            BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL,
            BlockType.FRAMED_DIVIDED_PANEL_VERTICAL,
            BlockType.FRAMED_FENCE,
            BlockType.FRAMED_FENCE_GATE,
            BlockType.FRAMED_DOOR,
            BlockType.FRAMED_IRON_DOOR,
            BlockType.FRAMED_LADDER,
            BlockType.FRAMED_BUTTON,
            BlockType.FRAMED_STONE_BUTTON,
            BlockType.FRAMED_LEVER,
            BlockType.FRAMED_SIGN,
            BlockType.FRAMED_WALL_SIGN,
            BlockType.FRAMED_HANGING_SIGN,
            BlockType.FRAMED_WALL_HANGING_SIGN,
            BlockType.FRAMED_TORCH,
            BlockType.FRAMED_WALL_TORCH,
            BlockType.FRAMED_SOUL_TORCH,
            BlockType.FRAMED_SOUL_WALL_TORCH,
            BlockType.FRAMED_REDSTONE_TORCH,
            BlockType.FRAMED_REDSTONE_WALL_TORCH,
            BlockType.FRAMED_LATTICE_BLOCK,
            BlockType.FRAMED_VERTICAL_STAIRS,
            BlockType.FRAMED_VERTICAL_SLICED_STAIRS,
            BlockType.FRAMED_BARS,
            BlockType.FRAMED_PANE,
            BlockType.FRAMED_FLOWER_POT,
            BlockType.FRAMED_POST,
            BlockType.FRAMED_HALF_STAIRS,
            BlockType.FRAMED_DOUBLE_HALF_STAIRS,
            BlockType.FRAMED_SLOPE_PANEL,
            BlockType.FRAMED_DOUBLE_SLOPE_PANEL,
            BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER,
            BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER,
            BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER,
            BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER,
            BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL,
            BlockType.FRAMED_SMALL_CORNER_SLOPE_PANEL_W,
            BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL,
            BlockType.FRAMED_SMALL_INNER_CORNER_SLOPE_PANEL_W,
            BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL,
            BlockType.FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W,
            BlockType.FRAMED_WALL_BOARD,
            BlockType.FRAMED_CORNER_STRIP,
            BlockType.FRAMED_GATE,
            BlockType.FRAMED_IRON_GATE,
            BlockType.FRAMED_ITEM_FRAME,
            BlockType.FRAMED_GLOWING_ITEM_FRAME,
            BlockType.FRAMED_FANCY_RAIL,
            BlockType.FRAMED_FANCY_POWERED_RAIL,
            BlockType.FRAMED_FANCY_DETECTOR_RAIL,
            BlockType.FRAMED_FANCY_ACTIVATOR_RAIL,
            BlockType.FRAMED_HALF_SLOPE,
            BlockType.FRAMED_DOUBLE_HALF_SLOPE,
            BlockType.FRAMED_CHECKERED_PANEL_SEGMENT,
            BlockType.FRAMED_CHECKERED_PANEL
    );

    @GameTestGenerator
    public static Collection<TestFunction> generateBeaconTintTests()
    {
        return Arrays.stream(BlockType.values())
                .filter(type -> !NON_TINTING.contains(type))
                .map(type -> Utils.rl(type.getName()))
                .map(BuiltInRegistries.BLOCK::get)
                .filter(b -> b != Blocks.AIR)
                .map(BeaconTintTests::getTestState)
                .map(state -> new TestFunction(
                        BATCH_NAME,
                        getTestName(state),
                        STRUCTURE_NAME,
                        100,
                        0,
                        true,
                        helper -> TestUtils.testBeaconBeamTinting(helper, state, getCamoSides(state.getBlock()))
                ))
                .toList();
    }

    private static BlockState getTestState(Block block)
    {
        Preconditions.checkArgument(block instanceof IFramedBlock);

        IBlockType type = ((IFramedBlock) block).getBlockType();
        if (type instanceof BlockType blockType)
        {
            BlockState state = block.defaultBlockState();
            return switch (blockType)
            {
                case FRAMED_PILLAR -> state.setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
                case FRAMED_HALF_PILLAR -> state.setValue(BlockStateProperties.FACING, Direction.DOWN);
                case FRAMED_PRISM -> state.setValue(PropertyHolder.FACING_AXIS, DirectionAxis.UP_X);
                case FRAMED_SLOPED_PRISM -> state.setValue(PropertyHolder.FACING_DIR, CompoundDirection.UP_NORTH);
                case FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL,
                     FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL_W,
                     FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL,
                     FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL,
                     FRAMED_INV_DOUBLE_CORNER_SLOPE_PANEL_W,
                     FRAMED_STACKED_CORNER_SLOPE_PANEL,
                     FRAMED_STACKED_CORNER_SLOPE_PANEL_W,
                     FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL,
                     FRAMED_STACKED_INNER_CORNER_SLOPE_PANEL_W -> state.setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
                case FRAMED_LARGE_BUTTON, FRAMED_LARGE_STONE_BUTTON -> state.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR);
                case FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W -> state.setValue(PropertyHolder.ROTATION, HorizontalRotation.RIGHT);
                default -> state;
            };
        }
        return block.defaultBlockState();
    }

    private static String getTestName(BlockState state)
    {
        ResourceLocation regName = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return String.format("beacontinttests.test_%s", regName.getPath());
    }

    private static List<Direction> getCamoSides(Block block)
    {
        Preconditions.checkArgument(block instanceof IFramedBlock);

        IBlockType type = ((IFramedBlock) block).getBlockType();
        if (!type.isDoubleBlock() || !(type instanceof BlockType))
        {
            return List.of(Direction.UP);
        }

        return switch ((BlockType) type)
        {
            case FRAMED_DIVIDED_SLAB,
                 FRAMED_DOUBLE_PANEL,
                 FRAMED_DOUBLE_SLOPE_PANEL,
                 FRAMED_INV_DOUBLE_SLOPE_PANEL,
                 FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL,
                 FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_STACKED_SLOPE_PANEL,
                 FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER,
                 FRAMED_SMALL_DOUBLE_CORNER_SLOPE_PANEL_W,
                 FRAMED_EXT_DOUBLE_CORNER_SLOPE_PANEL,
                 FRAMED_EXT_INNER_DOUBLE_CORNER_SLOPE_PANEL_W,
                 FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> List.of(Direction.NORTH, Direction.SOUTH);

            case FRAMED_DIVIDED_PANEL_VERTICAL,
                 FRAMED_DIVIDED_SLOPE,
                 FRAMED_DIVIDED_STAIRS,
                 FRAMED_SLICED_STAIRS_SLAB -> List.of(Direction.EAST, Direction.WEST);

            case FRAMED_CHECKERED_SLAB -> List.of(Direction.NORTH, Direction.WEST);

            default -> List.of(Direction.DOWN, Direction.UP);
        };
    }



    private BeaconTintTests() { }
}
