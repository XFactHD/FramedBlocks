package io.github.xfacthd.framedblocks.client.model.geometry.interactive;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public class FramedPressurePlateGeometry extends Geometry
{
    private final boolean pressed;
    private final boolean useBaseModel;

    public FramedPressurePlateGeometry(GeometryFactory.Context ctx)
    {
        this(ctx.state().getValue(BlockStateProperties.POWERED), false);
    }

    protected FramedPressurePlateGeometry(boolean powered, boolean useBaseModel)
    {
        this.pressed = powered;
        this.useBaseModel = useBaseModel;
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object modelData)
    {
        Direction quadDir = quad.direction();
        float height = pressed ? .5F / 16F : 1F / 16F;

        if (DirUtils.isY(quadDir))
        {
            boolean up = quadDir == Direction.UP;
            QuadModifier.of(quad)
                    .apply(Modifiers.cutTopBottom(1F/16F, 1F/16F, 15F/16F, 15F/16F))
                    .applyIf(Modifiers.setPosition(height), up)
                    .export(quadMap, up ? null : Direction.DOWN);
        }
        else
        {
            QuadModifier.of(quad)
                    .apply(Modifiers.cutSide(1F/16F, 0F, 15F/16F, height))
                    .apply(Modifiers.setPosition(15F/16F))
                    .export(quadMap, null);
        }
    }

    @Override
    public boolean useBaseModel()
    {
        return useBaseModel;
    }
}