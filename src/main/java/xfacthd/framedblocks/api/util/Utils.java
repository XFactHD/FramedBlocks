package xfacthd.framedblocks.api.util;

import com.google.common.base.Preconditions;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.util.client.ClientUtils;

public final class Utils
{
    public static final TagKey<Block> FRAMEABLE = blockTag("frameable");
    public static final TagKey<Block> BLACKLIST = blockTag("blacklisted");
    /**Allow other mods to whitelist their BEs, circumventing the config setting*/
    public static final TagKey<Block> BE_WHITELIST = blockTag("blockentity_whitelisted");
    public static final TagKey<Item> WRENCH = itemTag("forge", "tools/wrench");

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

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(
            BlockEntityType<A> type, BlockEntityType<E> actualType, BlockEntityTicker<? super E> ticker
    )
    {
        return actualType == type ? (BlockEntityTicker<A>)ticker : null;
    }

    public static MutableComponent translate(String prefix, String postfix, Object... arguments)
    {
        return Component.translatable(prefix + "." + FramedConstants.MOD_ID + "." + postfix, arguments);
    }

    public static MutableComponent translate(String prefix, String postfix)
    {
        return Component.translatable(prefix + "." + FramedConstants.MOD_ID + "." + postfix);
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

    public static FluidState readFluidStateFromNbt(CompoundTag tag)
    {
        if (!tag.contains("Name", Tag.TAG_STRING))
        {
            return Fluids.EMPTY.defaultFluidState();
        }

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("Name")));
        Preconditions.checkNotNull(fluid);

        FluidState fluidState = fluid.defaultFluidState();
        if (tag.contains("Properties", Tag.TAG_COMPOUND))
        {
            CompoundTag compoundtag = tag.getCompound("Properties");
            StateDefinition<Fluid, FluidState> statedefinition = fluid.getStateDefinition();

            for(String s : compoundtag.getAllKeys())
            {
                Property<?> property = statedefinition.getProperty(s);
                if (property != null)
                {
                    fluidState = NbtUtils.setValueHelper(fluidState, property, s, compoundtag, tag);
                }
            }
        }

        return fluidState;
    }

    public static Property<?> getRotatableProperty(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if (prop.getValueClass() == Direction.Axis.class)
            {
                return prop;
            }
            else if (prop instanceof DirectionProperty)
            {
                return prop;
            }
        }
        return null;
    }

    public static ResourceLocation rl(String path) { return new ResourceLocation(FramedConstants.MOD_ID, path); }



    private Utils() { }
}