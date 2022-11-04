package xfacthd.framedblocks.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.client.ClientUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public final class Utils
{
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final TagKey<Block> FRAMEABLE = blockTag("frameable");
    public static final TagKey<Block> BLACKLIST = blockTag("blacklisted");
    public static final TagKey<Block> CAMO_SUSTAIN_PLANT = blockTag("camo_sustain_plant");
    public static final TagKey<Item> WRENCH = itemTag("forge", "tools/wrench");
    /** Allow other mods to add items that temporarily disable intangibility to allow interaction with the targetted block */
    public static final TagKey<Item> DISABLE_INTANGIBLE = itemTag("disable_intangible");

    public static final RegistryObject<Item> FRAMED_HAMMER = RegistryObject.create(
            new ResourceLocation(FramedConstants.MOD_ID, "framed_hammer"),
            ForgeRegistries.ITEMS
    );
    public static final RegistryObject<Item> FRAMED_KEY = RegistryObject.create(
            new ResourceLocation(FramedConstants.MOD_ID, "framed_key"),
            ForgeRegistries.ITEMS
    );
    public static final RegistryObject<Item> FRAMED_SCREWDRIVER = RegistryObject.create(
            new ResourceLocation(FramedConstants.MOD_ID, "framed_screwdriver"),
            ForgeRegistries.ITEMS
    );

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        if (isY(from) || isY(to)) { throw new IllegalArgumentException("Invalid Direction!"); }
        if (from == to) { return shape; }

        VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(
                    buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static Vec3 fraction(Vec3 vec)
    {
        return new Vec3(
                vec.x() - Math.floor(vec.x()),
                vec.y() - Math.floor(vec.y()),
                vec.z() - Math.floor(vec.z())
        );
    }

    /**
     * Calculate how far into the block the coordinate of the given direction points in the given direction
     */
    public static double fractionInDir(Vec3 vec, Direction dir)
    {
        vec = fraction(vec);
        double coord = switch (dir.getAxis())
                {
                    case X -> vec.x;
                    case Y -> vec.y;
                    case Z -> vec.z;
                };
        return isPositive(dir) ? coord : (1D - coord);
    }

    /**
     * Check if the left hand value is lower than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is lower than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isLower(float lhs, float rhs)
    {
        if (Mth.equal(lhs, rhs))
        {
            return false;
        }
        return lhs < rhs;
    }

    /**
     * Check if the left hand value is higher than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is higher than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isHigher(float lhs, float rhs)
    {
        if (Mth.equal(lhs, rhs))
        {
            return false;
        }
        return lhs > rhs;
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(
            BlockEntityType<A> type, BlockEntityType<E> actualType, BlockEntityTicker<? super E> ticker
    )
    {
        return actualType == type ? (BlockEntityTicker<A>)ticker : null;
    }

    public static TranslatableComponent translate(String prefix, String postfix)
    {
        return new TranslatableComponent(prefix + "." + FramedConstants.MOD_ID + "." + postfix);
    }

    public static BlockEntity getBlockEntitySafe(BlockGetter blockGetter, BlockPos pos)
    {
        if (blockGetter instanceof Level level)
        {
            return level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
        }
        else if (blockGetter instanceof LevelChunk chunk)
        {
            return chunk.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
        }
        else if (FMLEnvironment.dist.isClient())
        {
            return ClientUtils.getBlockEntitySafe(blockGetter, pos);
        }
        return null;
    }

    public static boolean isPositive(Direction dir) { return dir.getAxisDirection() == Direction.AxisDirection.POSITIVE; }

    public static boolean isX(Direction dir) { return dir.getAxis() == Direction.Axis.X; }

    public static boolean isY(Direction dir) { return dir.getAxis() == Direction.Axis.Y; }

    public static boolean isZ(Direction dir) { return dir.getAxis() == Direction.Axis.Z; }

    public static Direction.Axis nextAxisNotEqualTo(Direction.Axis axis, Direction.Axis except)
    {
        Direction.Axis[] axes = Direction.Axis.VALUES;
        do
        {
            axis = axes[(axis.ordinal() + 1) % axes.length];
        }
        while (axis == except);

        return axis;
    }

    /**
     * Mirrors a block that is oriented towards a face of the block space.
     * @param state The {@link BlockState} to mirror
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given state must have the {@link FramedProperties#FACING_HOR} property
     */
    public static BlockState mirrorFaceBlock(BlockState state, Mirror mirror)
    {
        return mirrorFaceBlock(state, FramedProperties.FACING_HOR, mirror);
    }

    /**
     * Mirrors a block that is oriented towards a face of the block space
     * @param state The {@link BlockState} to mirror
     * @param property The {@link DirectionProperty} that should be mirrored on the given state
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given property must support at least all four cardinal directions
     */
    public static BlockState mirrorFaceBlock(BlockState state, DirectionProperty property, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }

        Direction dir = state.getValue(property);
        //Y directions are inherently ignored
        if ((mirror == Mirror.FRONT_BACK && isX(dir)) || (mirror == Mirror.LEFT_RIGHT && isZ(dir)))
        {
            return state.setValue(property, dir.getOpposite());
        }
        return state;
    }

    /**
     * Mirrors a block that is oriented into a corner of the block space.
     * @param state The {@link BlockState} to mirror
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given state must have the {@link FramedProperties#FACING_HOR} property
     */
    public static BlockState mirrorCornerBlock(BlockState state, Mirror mirror)
    {
        return mirrorCornerBlock(state, FramedProperties.FACING_HOR, mirror);
    }

    /**
     * Mirrors a block that is oriented into a corner of the block space
     * @param state The {@link BlockState} to mirror
     * @param property The {@link DirectionProperty} that should be mirrored on the given state
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given property must support at least all four cardinal directions
     */
    public static BlockState mirrorCornerBlock(BlockState state, DirectionProperty property, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }

        Direction dir = state.getValue(property);
        if (isY(dir))
        {
            return state;
        }

        if (mirror == Mirror.LEFT_RIGHT)
        {
            dir = switch (dir)
                    {
                        case NORTH -> Direction.WEST;
                        case EAST -> Direction.SOUTH;
                        case SOUTH -> Direction.EAST;
                        case WEST -> Direction.NORTH;
                        default -> throw new IllegalArgumentException("Unreachable!");
                    };
        }
        else
        {
            dir = switch (dir)
                    {
                        case NORTH -> Direction.EAST;
                        case EAST -> Direction.NORTH;
                        case SOUTH -> Direction.WEST;
                        case WEST -> Direction.SOUTH;
                        default -> throw new IllegalArgumentException("Unreachable!");
                    };
        }
        return state.setValue(property, dir);
    }

    public static TagKey<Block> blockTag(String name) { return blockTag(FramedConstants.MOD_ID, name); }

    public static TagKey<Block> blockTag(String modid, String name)
    {
        return BlockTags.create(new ResourceLocation(modid, name));
    }

    public static TagKey<Item> itemTag(String name) { return itemTag(FramedConstants.MOD_ID, name); }

    public static TagKey<Item> itemTag(String modid, String name)
    {
        return ItemTags.create(new ResourceLocation(modid, name));
    }

    public static void forAllDirections(Consumer<Direction> consumer)
    {
        consumer.accept(null);
        for (Direction dir : DIRECTIONS)
        {
            consumer.accept(dir);
        }
    }

    public static void wrapInStateCopy(LevelAccessor level, BlockPos pos, boolean writeToCamoTwo, Runnable action)
    {
        BlockState camoState = Blocks.AIR.defaultBlockState();
        ItemStack camoStack = ItemStack.EMPTY;
        boolean glowing = false;
        boolean intangible = false;

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            camoState = be.getCamoState();
            camoStack = be.getCamoStack();
            glowing = be.isGlowing();
            intangible = be.isIntangible(null);
        }

        action.run();

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            be.setCamo(camoStack, camoState, writeToCamoTwo);
            be.setGlowing(glowing);
            be.setIntangible(intangible);
        }
    }

    public static ResourceLocation rl(String path) { return new ResourceLocation(FramedConstants.MOD_ID, path); }

    public static MethodHandle unreflectMethod(Class<?> clazz, String srgMethodName, Class<?>... paramTypes)
    {
        Method method = ObfuscationReflectionHelper.findMethod(clazz, srgMethodName, paramTypes);
        try
        {
            return MethodHandles.publicLookup().unreflect(method);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect method '%s#%s()'".formatted(clazz.getName(), srgMethodName), e);
        }
    }

    public static MethodHandle unreflectField(Class<?> clazz, String srgFieldName)
    {
        Field field = ObfuscationReflectionHelper.findField(clazz, srgFieldName);
        try
        {
            return MethodHandles.publicLookup().unreflectGetter(field);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect field '%s#%s'".formatted(clazz.getName(), srgFieldName), e);
        }
    }

    public static MethodHandle unreflectFieldSetter(Class<?> clazz, String srgFieldName)
    {
        Field field = ObfuscationReflectionHelper.findField(clazz, srgFieldName);
        try
        {
            return MethodHandles.publicLookup().unreflectSetter(field);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect field '%s#%s'".formatted(clazz.getName(), srgFieldName), e);
        }
    }



    private Utils() { }
}