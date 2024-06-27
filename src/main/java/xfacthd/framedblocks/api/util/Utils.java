package xfacthd.framedblocks.api.util;

import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.empty.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.registration.DeferredDataComponentType;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public final class Utils
{
    private static final ResourceLocation RL_TEMPLATE = Utils.rl(FramedConstants.MOD_ID, "");
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Direction[] HORIZONTAL_DIRECTIONS = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
    public static final TagKey<Block> FRAMEABLE = blockTag("frameable");
    public static final TagKey<Block> BLOCK_BLACKLIST = blockTag("blacklisted");
    public static final TagKey<Fluid> FLUID_BLACKLIST = TagKey.create(Registries.FLUID, rl("blacklisted"));
    /** Allow other mods to whitelist their BEs, circumventing the config setting */
    public static final TagKey<Block> BE_WHITELIST = blockTag("blockentity_whitelisted");
    public static final TagKey<Item> TOOL_WRENCH = itemTag("c", "tools/wrench");
    public static final ItemAbility ACTION_WRENCH = ItemAbility.get("wrench");
    /** Allow other mods to add items that temporarily disable intangibility to allow interaction with the targeted block */
    public static final TagKey<Item> DISABLE_INTANGIBLE = itemTag("disable_intangible");
    public static final Set<Property<?>> REQUIRED_STATE_PROPERTIES = Set.of(
            FramedProperties.GLOWING,
            FramedProperties.PROPAGATES_SKYLIGHT
    );

    public static final Holder<Item> FRAMED_HAMMER = DeferredItem.createItem(Utils.rl("framed_hammer"));
    public static final Holder<Item> FRAMED_WRENCH = DeferredItem.createItem(Utils.rl("framed_wrench"));
    public static final Holder<Item> FRAMED_KEY = DeferredItem.createItem(Utils.rl("framed_key"));
    public static final Holder<Item> FRAMED_SCREWDRIVER = DeferredItem.createItem(Utils.rl("framed_screwdriver"));
    public static final Holder<Item> FRAMED_REINFORCEMENT = DeferredItem.createItem(Utils.rl("framed_reinforcement"));

    public static final DeferredDataComponentType<CamoList> DC_TYPE_CAMO_LIST = DeferredDataComponentType.createDataComponent(
            Utils.rl("camo_list")
    );

    private static final Long2ObjectMap<Direction> DIRECTION_BY_NORMAL = Arrays.stream(Direction.values())
            .collect(Collectors.toMap(
                    side -> new BlockPos(side.getNormal()).asLong(),
                    Function.identity(),
                    (sideA, sideB) -> { throw new IllegalArgumentException("Duplicate keys"); },
                    Long2ObjectOpenHashMap::new
            ));

    public static Vec3 fraction(Vec3 vec)
    {
        return new Vec3(
                vec.x() - Math.floor(vec.x()),
                vec.y() - Math.floor(vec.y()),
                vec.z() - Math.floor(vec.z())
        );
    }

    /**
     * Calculate how far into the block the coordinate of the given direction's axis points in the given direction
     */
    public static double fractionInDir(Vec3 vec, Direction dir)
    {
        double coord = switch (dir.getAxis())
        {
            case X -> vec.x;
            case Y -> vec.y;
            case Z -> vec.z;
        };
        coord = coord - Math.floor(coord);
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

    /**
     * {@return the least common multiple of the two input values}
     */
    public static long lcm(int a, int b)
    {
        return (long) a * (long) (b / IntMath.gcd(a, b));
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
        return Component.translatable(translationKey(prefix, postfix), arguments);
    }

    public static MutableComponent translate(String prefix, String postfix)
    {
        return Component.translatable(translationKey(prefix, postfix));
    }

    public static String translationKey(String prefix, String postfix)
    {
        String key = "";
        if (prefix != null)
        {
            key = prefix + ".";
        }
        key += FramedConstants.MOD_ID;
        if (postfix != null)
        {
            key += "." + postfix;
        }
        return key;
    }

    public static String translateConfig(String type, String key)
    {
        return translationKey("config", type + "." + key);
    }

    public static <T extends Enum<T> & StringRepresentable> Component[] buildEnumTranslations(
            String prefix, String postfix, T[] values, ChatFormatting... formatting
    )
    {
        return Arrays.stream(values)
                .map(v -> translate(prefix, postfix + "." + v.getSerializedName()))
                .map(c -> c.withStyle(formatting))
                .toArray(Component[]::new);
    }

    public static <T extends Enum<T>> Component[] bindEnumTranslation(
            String key, T[] values, Component[] valueTranslations
    )
    {
        Preconditions.checkArgument(
                values.length == valueTranslations.length, "Value and translation arrays must have the same length"
        );
        Component[] components = new Component[values.length];
        for (T v : values)
        {
            components[v.ordinal()] = Component.translatable(key, valueTranslations[v.ordinal()]);
        }
        return components;
    }

    public static MutableComponent translateTag(TagKey<?> tag)
    {
        String key = Tags.getTagTranslationKey(tag);
        return Component.translatableWithFallback(key, "#" + tag.location());
    }

    public static boolean isPositive(Direction dir)
    {
        return dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
    }

    public static boolean isX(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.X;
    }

    public static boolean isY(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.Y;
    }

    public static boolean isZ(Direction dir)
    {
        return dir.getAxis() == Direction.Axis.Z;
    }

    public static Direction dirByNormal(int x, int y, int z)
    {
        return DIRECTION_BY_NORMAL.get(BlockPos.asLong(x, y, z));
    }

    public static Direction dirByNormal(BlockPos from, BlockPos to)
    {
        int nx = to.getX() - from.getX();
        int ny = to.getY() - from.getY();
        int nz = to.getZ() - from.getZ();
        return dirByNormal(nx, ny, nz);
    }

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

    public static <T> List<T> concat(List<T> listOne, List<T> listTwo)
    {
        List<T> list = new ArrayList<>(listOne.size() + listTwo.size());
        list.addAll(listOne);
        list.addAll(listTwo);
        return List.copyOf(list);
    }

    public static <T> Set<T> concat(Set<T> setOne, Set<T> setTwo)
    {
        Set<T> set = new HashSet<>(setOne.size() + setTwo.size());
        set.addAll(setOne);
        set.addAll(setTwo);
        return Set.copyOf(set);
    }

    /**
     * Copy all elements from the source list to the destination list
     * (Significantly faster than {@link ArrayList#addAll(Collection)} in benchmarks)
     */
    @SuppressWarnings({ "UseBulkOperation", "ForLoopReplaceableByForEach" })
    public static <T> ArrayList<T> copyAll(List<T> src, ArrayList<T> dest)
    {
        dest.ensureCapacity(dest.size() + src.size());
        for (int i = 0; i < src.size(); i++)
        {
            dest.add(src.get(i));
        }
        return dest;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static <T> ArrayList<T> copyAllWithModifier(List<T> src, ArrayList<T> dest, UnaryOperator<T> modifier)
    {
        dest.ensureCapacity(dest.size() + src.size());
        for (int i = 0; i < src.size(); i++)
        {
            dest.add(modifier.apply(src.get(i)));
        }
        return dest;
    }

    public static TagKey<Block> blockTag(String name)
    {
        return blockTag(FramedConstants.MOD_ID, name);
    }

    public static TagKey<Block> blockTag(String modid, String name)
    {
        return BlockTags.create(Utils.rl(modid, name));
    }

    public static TagKey<Item> itemTag(String name)
    {
        return itemTag(FramedConstants.MOD_ID, name);
    }

    public static TagKey<Item> itemTag(String modid, String name)
    {
        return ItemTags.create(Utils.rl(modid, name));
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

    public static <T extends Comparable<T>> T tryGetValue(BlockState state, Property<T> property, T _default)
    {
        return state.hasProperty(property) ? state.getValue(property) : _default;
    }

    public static void addRequiredProperties(StateDefinition.Builder<Block, BlockState> builder)
    {
        REQUIRED_STATE_PROPERTIES.forEach(builder::add);
    }

    public static BlockState copyRequiredProperties(BlockState from, BlockState to)
    {
        for (Property<?> property : REQUIRED_STATE_PROPERTIES)
        {
            to = Block.copyProperty(from, to, property);
        }
        return to;
    }

    public static void forAllDirections(Consumer<Direction> consumer)
    {
        forAllDirections(true, consumer);
    }

    public static void forAllDirections(boolean includeNull, Consumer<Direction> consumer)
    {
        if (includeNull)
        {
            consumer.accept(null);
        }
        for (Direction dir : DIRECTIONS)
        {
            consumer.accept(dir);
        }
    }

    public static void forHorizontalDirections(Consumer<Direction> consumer)
    {
        for (Direction dir : HORIZONTAL_DIRECTIONS)
        {
            consumer.accept(dir);
        }
    }

    public static int maskNullDirection(Direction dir)
    {
        return dir == null ? DIRECTIONS.length : dir.ordinal();
    }

    public static void wrapInStateCopy(
            LevelAccessor level,
            BlockPos pos,
            Player player,
            ItemStack stack,
            boolean writeToCamoTwo,
            boolean consumeItem,
            Runnable action
    )
    {
        CamoContainer<?, ?> camo = EmptyCamoContainer.EMPTY;
        boolean glowing = false;
        boolean intangible = false;
        boolean reinforced = false;

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            camo = be.getCamo();
            glowing = be.isGlowing();
            intangible = be.isIntangible(null);
            reinforced = be.isReinforced();
        }

        action.run();

        if (consumeItem && !player.isCreative())
        {
            stack.shrink(1);
            player.getInventory().setChanged();
        }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            be.setCamo(camo, writeToCamoTwo);
            be.setGlowing(glowing);
            be.setIntangible(intangible);
            be.setReinforced(reinforced);
        }
    }

    public static ResourceLocation rl(String path)
    {
        return RL_TEMPLATE.withPath(path);
    }

    public static ResourceLocation rl(String namespace, String path)
    {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> payloadType(String path)
    {
        return new CustomPacketPayload.Type<>(rl(path));
    }

    public static <T> ResourceKey<T> getKeyOrThrow(Holder<T> holder)
    {
        return holder.unwrapKey().orElseThrow(
                () -> new IllegalArgumentException("Direct holders and unbound reference holders are not supported")
        );
    }

    public static boolean isHandContext(ItemDisplayContext ctx)
    {
        return ctx.firstPerson() || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    /**
     * Place the given {@link ItemStack} in the given {@link Player}'s inventory or drop it if it doesn't fit if the
     * player is in survival or place it in the player's inventory if the player is in creative mode and doesn't
     * already have the item
     *
     * @param player The player to give the stack to
     * @param stack The stack to give to the player
     * @param giveInSurvival Whether the stack should be given to a player in survival mode
     */
    public static void giveToPlayer(Player player, ItemStack stack, boolean giveInSurvival)
    {
        if (stack.isEmpty()) return;

        boolean creative = player.isCreative();
        if (!creative && giveInSurvival)
        {
            if (!player.getInventory().add(stack))
            {
                player.drop(stack, false);
            }
        }
        else if (creative && !player.getInventory().contains(stack))
        {
            player.getInventory().add(stack);
        }
    }

    public static String formatItemStack(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return "~~EMPTY~~";
        }

        String result = stack.getCount() + "x " + stack.getItem() + "[";
        DataComponentPatch patch = stack.getComponentsPatch();
        if (patch != DataComponentPatch.EMPTY)
        {
            result += patch;
        }
        return result + "]";
    }

    public static String formatHitResult(@Nullable HitResult hitResult)
    {
        if (hitResult == null)
        {
            return "~~NULL~~";
        }

        ToStringBuilder result = new ToStringBuilder(hitResult)
                .append("Type", hitResult.getType())
                .append("Location", hitResult.getLocation());
        if (hitResult instanceof BlockHitResult blockHit)
        {
            result.append("Position", blockHit.getBlockPos())
                    .append("Side", blockHit.getDirection())
                    .append("Inside", blockHit.isInside());
        }
        else if (hitResult instanceof EntityHitResult entityHit)
        {
            result.append("Entity", entityHit.getEntity());
        }
        return result.toString();
    }

    @ApiStatus.Internal
    public static <T> T loadService(Class<T> clazz)
    {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }



    private Utils() { }
}
