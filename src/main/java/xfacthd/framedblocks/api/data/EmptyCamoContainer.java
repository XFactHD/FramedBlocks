package xfacthd.framedblocks.api.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;

public class EmptyCamoContainer extends CamoContainer
{
    private static final RegistryObject<CamoContainer.Factory> EMPTY_FACTORY = RegistryObject.create(
            new ResourceLocation(FramedConstants.MOD_ID, "empty"),
            FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_NAME,
            FramedConstants.MOD_ID
    );
    public static final EmptyCamoContainer EMPTY = new EmptyCamoContainer();

    private EmptyCamoContainer() { super(Blocks.AIR.defaultBlockState()); }

    @Override
    public int getColor(BlockAndTintGetter level, BlockPos pos, int tintIdx) { return -1; }

    @Override
    public MaterialColor getMapColor(BlockGetter level, BlockPos pos) { return null; }

    @Override
    public ItemStack toItemStack(ItemStack stack) { return ItemStack.EMPTY; }

    @Override
    public boolean isEmpty() { return true; }

    @Override
    public ContainerType getType() { return ContainerType.EMPTY; }

    @Override
    public CamoContainer.Factory getFactory() { return EMPTY_FACTORY.get(); }

    @Override
    public void save(CompoundTag tag) { }



    public static final class Factory extends CamoContainer.Factory
    {
        @Override
        public CamoContainer fromNbt(CompoundTag tag) { return EMPTY; }

        @Override
        public CamoContainer fromItem(ItemStack stack)
        {
            throw new UnsupportedOperationException("Empty camo container cannot be created from ItemStack");
        }
    }
}
