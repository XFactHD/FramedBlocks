package xfacthd.framedblocks.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.client.ClientUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Utils
{
    public static final TagKey<Block> FRAMEABLE = blockTag("frameable");
    public static final TagKey<Block> BLACKLIST = blockTag("blacklisted");
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



    private Utils() { }
}