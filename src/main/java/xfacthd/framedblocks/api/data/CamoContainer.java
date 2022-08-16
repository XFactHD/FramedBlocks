package xfacthd.framedblocks.api.data;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MaterialColor;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.util.Utils;

public abstract class CamoContainer
{
    private static final Logger LOGGER = LogUtils.getLogger();

    protected BlockState state;

    protected CamoContainer(BlockState state) { this.state = state; }

    /**
     * Returns the {@link BlockState} used as the data source in the model
     */
    public BlockState getState() { return state; }

    /**
     * Returns the fluid contained in this camo container, if applicable
     * @apiNote Must be overriden by CamoContainers returning {@link ContainerType#FLUID} from {@link CamoContainer#getType()}
     */
    public Fluid getFluid()
    {
        throw new UnsupportedOperationException("CamoContainer#getType() returns ContainerType.FLUID but doesn't override CamoContainer#getFluid()");
    }

    /**
     * Returns the {@link MaterialColor} to use on the map for this camo container
     * @param level The current level
     * @param pos The position of the framed block
     * @return The map color
     */
    public MaterialColor getMapColor(BlockGetter level, BlockPos pos) { return state.getMapColor(level, pos); }

    /**
     * Returns the color multipliers to apply to a beacon beam
     * @param level The current level
     * @param pos The position of the framed block
     * @param beaconPos The position of the beacon where the beam originates from
     * @return An array of R, G and B values to be used as the color multiplier
     */
    public float[] getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos)
    {
        return state.getBeaconColorMultiplier(level, pos, beaconPos);
    }

    /**
     * Returns the tint color for use in {@link net.minecraft.client.color.block.BlockColor}
     * @param level The current level
     * @param pos The position of the framed block
     * @param tintIdx The tint index for which the color was requested
     * @return The tint color for the given index
     */
    public abstract int getColor(BlockAndTintGetter level, BlockPos pos, int tintIdx);

    /**
     * Returns the item to be dropped when the block is destroyed or when the item for the BuildingGadgets material
     * list is requested
     * @param stack The item used to interact with the block, mainly important for fluid camo containers as they
     *              may use this item as the container
     * @return An {@link ItemStack} to drop, {@link ItemStack#EMPTY} when the drop fails or, in the case of fluid
     *         containers, the input stack if it needs to be modified to fill it with the fluid
     * @implNote Must return a new {@link ItemStack}, the empty ItemStack or the given {@code ItemStack} on every call
     */
    public abstract ItemStack toItemStack(ItemStack stack);

    /**
     * Rotate the camo by cycling through the first property considered rotatable by {@link Utils#getRotatableProperty(BlockState)}.
     * @return True if the rotation was successful, requiring a render update
     */
    public boolean rotateCamo()
    {
        Property<?> prop = Utils.getRotatableProperty(getState());
        if (prop == null)
        {
            return false;
        }

        state = state.cycle(prop);
        return true;
    }

    /**
     * Returns the {@link SoundType} to use for the camo this container holds
     */
    public SoundType getSoundType() { return state.getSoundType(); }

    /**
     * Returns true if this container is empty
     */
    public boolean isEmpty() { return false; }

    /**
     * Returns the type of camo this container holds
     */
    public abstract ContainerType getType();

    /**
     * Returns the {@link Factory} used to reconstruct
     */
    public abstract CamoContainer.Factory getFactory();

    /**
     * Save the data of this container to the given {@link CompoundTag}
     */
    public abstract void save(CompoundTag tag);



    public static CompoundTag save(CamoContainer camo)
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", camo.getFactory().getId());
        camo.save(tag);
        return tag;
    }

    public static CamoContainer load(CompoundTag tag)
    {
        if (tag.isEmpty()) { return EmptyCamoContainer.EMPTY; }

        ResourceLocation id = ResourceLocation.tryParse(tag.getString("type"));
        Factory factory = FramedBlocksAPI.getInstance().getCamoContainerFactoryRegistry().getValue(id);
        if (factory == null)
        {
            LOGGER.error("Unknown ICamoContainer with ID {}, dropping!", id);
            return EmptyCamoContainer.EMPTY;
        }
        return factory.fromNbt(tag);
    }



    public static abstract class Factory
    {
        private String id;

        public final String getId()
        {
            if (id == null)
            {
                //noinspection ConstantConditions
                id = FramedBlocksAPI.getInstance().getCamoContainerFactoryRegistry().getKey(this).toString();
            }
            return id;
        }

        /**
         * Reconstruct the {@link CamoContainer} from the given {@link CompoundTag}
         */
        public abstract CamoContainer fromNbt(CompoundTag tag);

        /**
         * Construct a {@link CamoContainer} from the given {@link ItemStack}
         * @return A new CamoContainer or {@link EmptyCamoContainer#EMPTY}
         */
        public abstract CamoContainer fromItem(ItemStack stack);
    }
}
