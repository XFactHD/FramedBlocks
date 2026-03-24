package io.github.xfacthd.framedblocks.client.model.item.modelprovider;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.item.block.BlockItemModelProvider;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public final class FenceBlockItemModelProvider implements BlockItemModelProvider
{
    public static final FenceBlockItemModelProvider INSTANCE = new FenceBlockItemModelProvider();

    private FenceBlockItemModelProvider() { }

    @Override
    public Supplier<BlockStateModel> create(BlockState state, ModelBaker baker)
    {
        return BlockItemModelProvider.forGeometry(state, state, FenceItemGeometry::new, baker);
    }

    private static final class FenceItemGeometry extends Geometry
    {
        private FenceItemGeometry(GeometryFactory.Context ctx) { }

        @Override
        public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
        {
            Direction quadDir = quad.direction();
            if (DirUtils.isY(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(6F/16F, 0F, 10F/16F, 4F/16F))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(6F/16F, 12F/16F, 10F/16F, 1F))
                        .export(quadMap, quadDir);

                boolean up = quadDir == Direction.UP;
                float posOne = up ? 15F/16F : 4F/16F;
                float posTwo = up ? 9F/16F : 10F/16F;

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(7F/16F, 4F/16F, 9F/16F, 12F/16F))
                        .apply(Modifiers.setPosition(posOne))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(7F/16F, 4F/16F, 9F/16F, 12F/16F))
                        .apply(Modifiers.setPosition(posTwo))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(7F/16F, 0F, 9F/16F, 2F/16F))
                        .apply(Modifiers.setPosition(posOne))
                        .apply(Modifiers.offset(Direction.SOUTH, 1F))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(7F/16F, 0F, 9F/16F, 2F/16F))
                        .apply(Modifiers.setPosition(posTwo))
                        .apply(Modifiers.offset(Direction.SOUTH, 1F))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(7F/16F, 14F/16F, 9F/16F, 1F))
                        .apply(Modifiers.setPosition(posOne))
                        .apply(Modifiers.offset(Direction.NORTH, 1F))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cutTopBottom(7F/16F, 14F/16F, 9F/16F, 1F))
                        .apply(Modifiers.setPosition(posTwo))
                        .apply(Modifiers.offset(Direction.NORTH, 1F))
                        .export(quadMap, null);
            }
            else if (DirUtils.isX(quadDir))
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.SOUTH, 4F/16F))
                        .apply(Modifiers.setPosition(10F/16F))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.NORTH, 4F/16F))
                        .apply(Modifiers.setPosition(10F/16F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 12F/16F))
                        .apply(Modifiers.cut(Direction.UP, 15F/16F))
                        .apply(Modifiers.cut(Direction.DOWN, 4F/16F))
                        .apply(Modifiers.setPosition(9F/16F))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 12F/16F))
                        .apply(Modifiers.cut(Direction.UP, 9F/16F))
                        .apply(Modifiers.cut(Direction.DOWN, 10F/16F))
                        .apply(Modifiers.setPosition(9F/16F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.SOUTH, 2F/16F))
                        .apply(Modifiers.cut(Direction.UP, 15F/16F))
                        .apply(Modifiers.cut(Direction.DOWN, 4F/16F))
                        .apply(Modifiers.setPosition(9F/16F))
                        .apply(Modifiers.offset(Direction.SOUTH, 1F))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.SOUTH, 2F/16F))
                        .apply(Modifiers.cut(Direction.UP, 9F/16F))
                        .apply(Modifiers.cut(Direction.DOWN, 10F/16F))
                        .apply(Modifiers.setPosition(9F/16F))
                        .apply(Modifiers.offset(Direction.SOUTH, 1F))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.NORTH, 2F/16F))
                        .apply(Modifiers.cut(Direction.UP, 15F/16F))
                        .apply(Modifiers.cut(Direction.DOWN, 4F/16F))
                        .apply(Modifiers.setPosition(9F/16F))
                        .apply(Modifiers.offset(Direction.NORTH, 1F))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.NORTH, 2F/16F))
                        .apply(Modifiers.cut(Direction.UP, 9F/16F))
                        .apply(Modifiers.cut(Direction.DOWN, 10F/16F))
                        .apply(Modifiers.setPosition(9F/16F))
                        .apply(Modifiers.offset(Direction.NORTH, 1F))
                        .export(quadMap, null);
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 10F/16F))
                        .export(quadMap, quadDir);
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getClockWise().getAxis(), 10F/16F))
                        .apply(Modifiers.setPosition(4F/16F))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(7F/16F, 12F/16F, 9F/16F, 15F/16F))
                        .apply(Modifiers.setPosition(18F/16F))
                        .export(quadMap, null);
                QuadModifier.of(quad)
                        .apply(Modifiers.cutSide(7F/16F, 6F/16F, 9F/16F, 9F/16F))
                        .apply(Modifiers.setPosition(18F/16F))
                        .export(quadMap, null);
            }
        }

        @Override
        public boolean useSolidNoCamoModel()
        {
            return true;
        }
    }
}
