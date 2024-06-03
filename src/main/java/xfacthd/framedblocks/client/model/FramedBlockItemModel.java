package xfacthd.framedblocks.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.camo.CamoContent;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.util.CamoList;
import xfacthd.framedblocks.api.util.Utils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FramedBlockItemModel extends BakedModelWrapper<BakedModel> {
    private final ConcurrentHashMap<CamoContent<?>, List<BakedModel>> modelsByContent = new ConcurrentHashMap<>();

    public FramedBlockItemModel(BakedModel originalModel) {
        super(originalModel);
    }

    private BakedModel makeDataBasedItemModel(CamoContent<?> camoContent, Item item, boolean fabulous) {
        ModelData data = ModelData.builder().with(FramedBlockData.PROPERTY, new FramedBlockData(camoContent, false)).build();
        var blockState = item instanceof BlockItem blockItem ? blockItem.getBlock().defaultBlockState() : Blocks.AIR.defaultBlockState();
        ChunkRenderTypeSet renderTypes = this.originalModel.getRenderTypes(blockState, RandomSource.create(42), data);
        var itemRenderType = renderTypes.contains(RenderType.translucent()) ? RenderTypeHelper.getEntityRenderType(RenderType.translucent(), fabulous) : Sheets.cutoutBlockSheet();
        var itemRenderTypeList = List.of(itemRenderType);
        return new BakedModelWrapper<>(this.originalModel) {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
                return super.getQuads(state, side, rand, data, null);
            }

            @Override
            public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous) {
                return itemRenderTypeList;
            }
        };
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        originalModel.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        return this;
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
        var camoList = itemStack.getOrDefault(Utils.DC_TYPE_CAMO_LIST, CamoList.EMPTY);
        var camoContent = camoList.getCamo(0).getContent();
        var modelList = modelsByContent.get(camoContent);
        if(modelList == null) {
            modelList = List.of(makeDataBasedItemModel(camoContent, itemStack.getItem(), fabulous));
            modelsByContent.put(camoContent, modelList);
        }
        return modelList;
    }
}
