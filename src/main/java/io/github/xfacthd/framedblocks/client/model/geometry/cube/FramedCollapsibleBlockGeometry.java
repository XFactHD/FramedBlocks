package io.github.xfacthd.framedblocks.client.model.geometry.cube;

import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.QuadMapBuilder;
import io.github.xfacthd.framedblocks.api.model.geometry.Geometry;
import io.github.xfacthd.framedblocks.api.model.quad.Modifiers;
import io.github.xfacthd.framedblocks.api.model.quad.QuadModifier;
import io.github.xfacthd.framedblocks.api.model.wrapping.GeometryFactory;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.blockentity.PackedCollapsibleBlockOffsets;
import io.github.xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class FramedCollapsibleBlockGeometry extends Geometry
{
    public static final String ALT_BASE_MODEL_KEY = "alt_base";
    private static final float MIN_DEPTH = .001F;

    private final BlockState state;
    @Nullable
    private final Direction collapsedFace;
    private final boolean rotSplitLine;
    private final BlockStateModel altBaseModel;

    public FramedCollapsibleBlockGeometry(GeometryFactory.Context ctx)
    {
        this.state = ctx.state();
        this.collapsedFace = ctx.state().getValue(PropertyHolder.NULLABLE_FACE).toNullableDirection();
        this.rotSplitLine = ctx.state().getValue(PropertyHolder.ROTATE_SPLIT_LINE);
        this.altBaseModel = ctx.auxModels().getModel(ALT_BASE_MODEL_KEY);
    }

    @Override
    public void transformQuad(QuadMapBuilder quadMap, BakedQuad quad, FramedBlockData blockData, @Nullable Object cacheKeyUserData)
    {
        Direction quadDir = quad.direction();
        if (collapsedFace == null || quadDir == collapsedFace.getOpposite())
        {
            quadMap.getOrCreate(quadDir).add(quad);
            return;
        }

        int offsets = PackedCollapsibleBlockOffsets.unwrap(cacheKeyUserData, state);
        float[] vertexPos = new float[] { 1F, 1F, 1F, 1F };
        boolean allSame = true;
        if (offsets != 0)
        {
            byte[] relOff = FramedCollapsibleBlockEntity.unpackOffsets(offsets);
            allSame = relOff[0] == relOff[1] && relOff[0] == relOff[2] && relOff[0] == relOff[3];
            for (int i = 0; i < 4; i++)
            {
                vertexPos[i] = Math.max(1F - ((float) relOff[i] / 16F), allSame ? MIN_DEPTH : 0F);
            }
        }

        if (quadDir == collapsedFace)
        {
            boolean planar = true;
            if (!allSame)
            {
                Vector3f v0 = new Vector3f(0, vertexPos[0], 0);
                Vector3f v1 = new Vector3f(0, vertexPos[1], 1);
                Vector3f v2 = new Vector3f(1, vertexPos[2], 1);
                Vector3f v3 = new Vector3f(1, vertexPos[3], 0);

                Vector3f v10 = v1.sub(v0);
                Vector3f v20 = v2.sub(v0);
                Vector3f n1 = v10.cross(v20);

                planar = Mth.equal(0F, v3.sub(v0).dot(n1));
            }
            if (planar)
            {
                QuadModifier.of(quad).apply(Modifiers.setPosition(vertexPos)).export(quadMap, null);
                return;
            }

            float diff02 = Math.abs(vertexPos[0] - vertexPos[2]);
            float diff13 = Math.abs(vertexPos[1] - vertexPos[3]);
            boolean rotate = (diff13 > diff02) != rotSplitLine;

            float[] vertexPosTwo = new float[] { 1F, 1F, 1F, 1F };
            System.arraycopy(vertexPos, 0, vertexPosTwo, 0, vertexPos.length);
            if (rotate)
            {
                vertexPos[2] = vertexPos[1] + vertexPos[3] - vertexPos[0];
                vertexPosTwo[0] = vertexPosTwo[1] + vertexPosTwo[3] - vertexPosTwo[2];
            }
            else
            {
                vertexPos[3] = vertexPos[0] + vertexPos[2] - vertexPos[1];
                vertexPosTwo[1] = vertexPosTwo[0] + vertexPosTwo[2] - vertexPosTwo[3];
            }

            if (DirUtils.isY(collapsedFace))
            {
                rotate ^= collapsedFace == Direction.DOWN;
                float left = rotate ? 0F : 1F;
                float right = rotate ? 1F : 0F;

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.EAST, left, right))
                        .apply(Modifiers.setPosition(vertexPos))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(Direction.WEST, left, right))
                        .apply(Modifiers.setPosition(vertexPosTwo))
                        .export(quadMap, null);
            }
            else
            {
                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getCounterClockWise(), rotate ? 1F : 0F, rotate ? 0F : 1F))
                        .apply(Modifiers.setPosition(vertexPos))
                        .export(quadMap, null);

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(quadDir.getClockWise(), rotate ? 0F : 1F, rotate ? 1F : 0F))
                        .apply(Modifiers.setPosition(vertexPosTwo))
                        .export(quadMap, null);
            }
        }
        else
        {
            if (DirUtils.isY(collapsedFace))
            {
                boolean top = collapsedFace == Direction.UP;
                int idxOne = getYCollapsedIndexOffset(quadDir);
                int idxTwo = Math.floorMod(idxOne + (top ? 1 : -1), 4);
                float posOne = vertexPos[idxOne];
                float posTwo = vertexPos[idxTwo];

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(collapsedFace, posOne, posTwo))
                        .export(quadMap, quadDir);
            }
            else if (DirUtils.isY(quadDir))
            {
                boolean top = quad.direction() == Direction.UP;
                float posOne = vertexPos[top ? 0 : 1];
                float posTwo = vertexPos[top ? 3 : 2];

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(collapsedFace, posOne, posTwo))
                        .export(quadMap, quadDir);
            }
            else
            {
                boolean right = collapsedFace == quadDir.getClockWise();
                float posTop = vertexPos[right ? 3 : 0];
                float posBot = vertexPos[right ? 2 : 1];

                QuadModifier.of(quad)
                        .apply(Modifiers.cut(collapsedFace, posTop, posBot))
                        .export(quadMap, quadDir);
            }
        }
    }

    @Override
    public boolean useBaseModel()
    {
        return true;
    }

    @Override
    public BlockStateModel getBaseModel(BlockStateModel baseModel, boolean useAltModel)
    {
        return useAltModel ? altBaseModel : baseModel;
    }

    @Override
    @Nullable
    public Object computeCacheKeyUserData(BlockAndTintGetter level, BlockPos pos, RandomSource random, ModelData data)
    {
        return data.get(PackedCollapsibleBlockOffsets.PROPERTY);
    }

    private int getYCollapsedIndexOffset(Direction quadFace)
    {
        boolean top = collapsedFace == Direction.UP;
        return switch (quadFace)
        {
            case NORTH -> top ? 3 : 2;
            case EAST -> top ? 2 : 3;
            case SOUTH -> top ? 1 : 0;
            case WEST -> top ? 0 : 1;
            case DOWN, UP -> throw new IllegalArgumentException("Invalid facing for y face collapse!");
        };
    }
}
