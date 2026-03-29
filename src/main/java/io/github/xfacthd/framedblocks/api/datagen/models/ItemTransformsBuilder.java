package io.github.xfacthd.framedblocks.api.datagen.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class ItemTransformsBuilder {
    private ItemTransform thirdPersonLeftHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform thirdPersonRightHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform firstPersonLeftHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform firstPersonRightHand = ItemTransform.NO_TRANSFORM;
    private ItemTransform head = ItemTransform.NO_TRANSFORM;
    private ItemTransform gui = ItemTransform.NO_TRANSFORM;
    private ItemTransform ground = ItemTransform.NO_TRANSFORM;
    private ItemTransform fixed = ItemTransform.NO_TRANSFORM;
    private ItemTransform fixedFromBottom = ItemTransform.NO_TRANSFORM;
    private final Map<ItemDisplayContext, ItemTransform> moddedTransforms = new EnumMap<>(ItemDisplayContext.class);

    ItemTransformsBuilder() { }

    public ItemTransformsBuilder thirdPersonLeftHand(UnaryOperator<TransformBuilder> operator) {
        this.thirdPersonLeftHand = operator.apply(new TransformBuilder()).build();
        return this;
    }

    public ItemTransformsBuilder thirdPersonRightHand(UnaryOperator<TransformBuilder> operator) {
        this.thirdPersonRightHand = operator.apply(new TransformBuilder()).build();
        return this;
    }

    public ItemTransformsBuilder firstPersonLeftHand(UnaryOperator<TransformBuilder> operator) {
        this.firstPersonLeftHand = operator.apply(new TransformBuilder()).build();
        return this;
    }

    public ItemTransformsBuilder firstPersonRightHand(UnaryOperator<TransformBuilder> operator) {
        this.firstPersonRightHand = operator.apply(new TransformBuilder()).build();
        return this;
    }

    public ItemTransformsBuilder head(UnaryOperator<TransformBuilder> operator) {
        this.head = operator.apply(new TransformBuilder()).build();
        return this;
    }

    public ItemTransformsBuilder gui(UnaryOperator<TransformBuilder> operator) {
        this.gui = operator.apply(new TransformBuilder()).build();
        return this;
    }

    public ItemTransformsBuilder ground(UnaryOperator<TransformBuilder> operator) {
        this.ground = operator.apply(new TransformBuilder()).build();
        return this;
    }

    public ItemTransformsBuilder fixed(UnaryOperator<TransformBuilder> operator) {
        this.fixed = operator.apply(new TransformBuilder()).build();
        return this;
    }

    public ItemTransformsBuilder fixedFromBottom(UnaryOperator<TransformBuilder> operator) {
        this.fixedFromBottom = operator.apply(new TransformBuilder()).build();
        return this;
    }

    public ItemTransforms build() {
        return new ItemTransforms(thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, firstPersonRightHand, head, gui, ground, fixed, fixedFromBottom, ImmutableMap.of());
    }

    public static final class TransformBuilder {
        private static final float MAX_TRANSLATION = ItemTransform.Deserializer.MAX_TRANSLATION * 16F;

        private Vector3f rotation = ItemTransform.Deserializer.DEFAULT_ROTATION;
        private Vector3f translation = ItemTransform.Deserializer.DEFAULT_TRANSLATION;
        private Vector3f scale = ItemTransform.Deserializer.DEFAULT_SCALE;
        private Vector3f rightRotation = ItemTransform.Deserializer.DEFAULT_ROTATION;

        public TransformBuilder rotation(float x, float y, float z) {
            this.rotation = new Vector3f(x, y, z);
            return this;
        }

        public TransformBuilder translation(float x, float y, float z) {
            Preconditions.checkArgument(Math.abs(x) <= MAX_TRANSLATION, "Translation must be -80 <= x <= 80");
            Preconditions.checkArgument(Math.abs(y) <= MAX_TRANSLATION, "Translation must be -80 <= y <= 80");
            Preconditions.checkArgument(Math.abs(z) <= MAX_TRANSLATION, "Translation must be -80 <= z <= 80");
            this.translation = new Vector3f(x / 16F, y / 16F, z / 16F);
            return this;
        }

        public TransformBuilder scale(float scale) {
            return scale(scale, scale, scale);
        }

        public TransformBuilder scale(float x, float y, float z) {
            Preconditions.checkArgument(Math.abs(x) <= ItemTransform.Deserializer.MAX_SCALE, "Scale must be -4 <= x <= 4");
            Preconditions.checkArgument(Math.abs(y) <= ItemTransform.Deserializer.MAX_SCALE, "Scale must be -4 <= y <= 4");
            Preconditions.checkArgument(Math.abs(z) <= ItemTransform.Deserializer.MAX_SCALE, "Scale must be -4 <= z <= 4");
            this.scale = new Vector3f(x, y, z);
            return this;
        }

        public TransformBuilder rightRotation(float x, float y, float z) {
            this.rightRotation = new Vector3f(x, y, z);
            return this;
        }

        public ItemTransform build() {
            return new ItemTransform(rotation, translation, scale, rightRotation);
        }
    }
}
