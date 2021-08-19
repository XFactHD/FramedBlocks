package xfacthd.framedblocks.common.compat.buildinggadgets;

import com.direwolf20.buildinggadgets.common.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.tainted.building.tilesupport.*;
import com.direwolf20.buildinggadgets.common.tainted.building.view.BuildContext;
import com.direwolf20.buildinggadgets.common.tainted.inventory.materials.MaterialList;
import com.direwolf20.buildinggadgets.common.tainted.inventory.materials.objects.UniqueItem;
import com.direwolf20.buildinggadgets.common.tainted.registry.TopologicalRegistryBuilder;
import com.google.common.base.Preconditions;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.tileentity.FramedDoubleTileEntity;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import java.util.function.Supplier;

public class BuildingGadgetsCompat
{
    private static final DeferredRegister<ITileDataSerializer> SERIALIZERS = DeferredRegister.create(ITileDataSerializer.class, FramedBlocks.MODID);
    private static final RegistryObject<ITileDataSerializer> FRAMED_SERIALIZER = SERIALIZERS.register("framed_serializer", FramedTileEntityDataSerializer::new);

    public static void init()
    {
        SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BuildingGadgetsCompat::sendCompatImc);
    }

    private static void sendCompatImc(@SuppressWarnings("unused") final InterModEnqueueEvent event)
    {
        InterModComms.sendTo("buildinggadgets", "imc_tile_data_factory", BuildingGadgetsCompat::createDataFactory);
    }

    private static Supplier<TopologicalRegistryBuilder<ITileDataFactory>> createDataFactory()
    {
        return () ->
        {
            TopologicalRegistryBuilder<ITileDataFactory> factory = TopologicalRegistryBuilder.create();
            factory.addValue(
                    new ResourceLocation(FramedBlocks.MODID, "framed_block_data_factory"),
                    te -> te instanceof FramedTileEntity ? new FramedTileEntityData((FramedTileEntity) te) : null
            );
            return factory;
        };
    }

    //FIXME: on blocks that change state after placement (ie. walls) only the first block gets the data (walls, fences) or every second one (lattice)
    private static class FramedTileEntityData extends NBTTileEntityData
    {
        public FramedTileEntityData(FramedTileEntity te) { super(te.writeToBlueprint(), buildMaterialList(te)); }

        public FramedTileEntityData(CompoundNBT data, MaterialList materials) { super(data, materials); }

        @Override
        public boolean placeIn(BuildContext context, BlockState state, BlockPos pos)
        {
            BuildingGadgets.LOG.trace("Placing {} with Tile NBT at {}.", state, pos);
            context.getWorld().setBlockState(pos, state, 0);

            TileEntity te = context.getWorld().getTileEntity(pos);
            if (te != null)
            {
                CompoundNBT nbt = getNBT();
                nbt.putInt("x", pos.getX());
                nbt.putInt("y", pos.getY());
                nbt.putInt("z", pos.getZ());

                try
                {
                    te.deserializeNBT(nbt);
                }
                catch (Exception e)
                {
                    BuildingGadgets.LOG.debug("Failed to apply Tile NBT Data to {} at {} in Context {}", state, pos, context, e);
                }
            }

            return true;
        }

        @Override
        public ITileDataSerializer getSerializer() { return FRAMED_SERIALIZER.get(); }

        @SuppressWarnings("deprecation")
        private static MaterialList buildMaterialList(FramedTileEntity te)
        {
            MaterialList.SimpleBuilder builder = MaterialList.simpleBuilder();

            //Add base materials
            if (te.getBlock() == FBContent.blockFramedDoubleSlab.get())
            {
                builder.add(
                        UniqueItem.ofStack(new ItemStack(FBContent.blockFramedSlab.get())),
                        UniqueItem.ofStack(new ItemStack(FBContent.blockFramedSlab.get()))
                );
            }
            else if (te.getBlock() == FBContent.blockFramedDoublePanel.get())
            {
                builder.add(
                        UniqueItem.ofStack(new ItemStack(FBContent.blockFramedPanel.get())),
                        UniqueItem.ofStack(new ItemStack(FBContent.blockFramedPanel.get()))
                );
            }
            else
            {
                builder.add(UniqueItem.ofStack(new ItemStack(te.getBlockState().getBlock())));
            }

            //Add main camo stack
            if (!te.getCamoState().isAir())
            {
                builder.add(UniqueItem.ofStack(te.getCamoStack().copy()));
            }

            //Add secondary camo stack
            if (te instanceof FramedDoubleTileEntity && !((FramedDoubleTileEntity) te).getCamoStateTwo().isAir())
            {
                builder.add(UniqueItem.ofStack(((FramedDoubleTileEntity) te).getCamoStackTwo().copy()));
            }

            //Add glowstone
            if (te.isGlowing())
            {
                builder.add(UniqueItem.ofStack(new ItemStack(Items.GLOWSTONE_DUST)));
            }

            return builder.build();
        }
    }

    private static final class FramedTileEntityDataSerializer extends ForgeRegistryEntry<ITileDataSerializer> implements ITileDataSerializer
    {
        private FramedTileEntityDataSerializer() { }

        public CompoundNBT serialize(ITileEntityData data, boolean persisted)
        {
            Preconditions.checkArgument(data instanceof FramedTileEntityData);
            FramedTileEntityData tileData = (FramedTileEntityData)data;
            CompoundNBT res = new CompoundNBT();
            res.put("data", tileData.getNBT());
            if (tileData.getRequiredMaterials() != null)
            {
                res.put("materials", tileData.getRequiredMaterials().serialize(persisted));
            }

            return res;
        }

        public ITileEntityData deserialize(CompoundNBT tagCompound, boolean persisted)
        {
            CompoundNBT data = tagCompound.getCompound("data");
            MaterialList materialList = null;
            if (tagCompound.contains("materials", Constants.NBT.TAG_COMPOUND))
            {
                CompoundNBT materialNbt = tagCompound.getCompound("materials");
                if (materialNbt.getList("data", Constants.NBT.TAG_COMPOUND).isEmpty())
                {
                    materialNbt.getCompound("data").putString("serializer", materialNbt.getString("serializer"));
                    materialNbt = materialNbt.getCompound("data");
                }
                materialList = MaterialList.deserialize(materialNbt, persisted);
            }

            return new FramedTileEntityData(data, materialList);
        }
    }
}