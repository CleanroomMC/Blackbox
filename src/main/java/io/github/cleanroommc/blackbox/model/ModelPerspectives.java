package io.github.cleanroommc.blackbox.model;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Matrix4f;
import java.util.EnumMap;

import static net.minecraftforge.client.model.ForgeBlockStateV1.Transforms.convert;
import static net.minecraftforge.client.model.ForgeBlockStateV1.Transforms.leftify;

/**
 * Used for {@link net.minecraft.client.renderer.block.model.IBakedModel#handlePerspective(ItemCameraTransforms.TransformType)}
 */
public class ModelPerspectives {

	public static final ModelPerspectives BLOCK;
	public static final ModelPerspectives NORMAL_ITEM;
	public static final ModelPerspectives HANDHELD_ITEM;

	static {
		EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> block = new EnumMap<>(ItemCameraTransforms.TransformType.class);
		block.put(ItemCameraTransforms.TransformType.GUI, convert(0, 0, 0, 30, 225, 0, 0.625F));
		block.put(ItemCameraTransforms.TransformType.GROUND, convert(0, 3, 0, 0, 0, 0, 0.25F));
		block.put(ItemCameraTransforms.TransformType.FIXED, convert(0, 0, 0, 0, 0, 0, 0.5F));
		TRSRTransformation thirdPersonBlock = convert(0, 2.5F, 0, 75, 45, 0, 0.375F);
		block.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPersonBlock);
		block.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPersonBlock));
		block.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, convert(0, 0, 0, 0, 45, 0, 0.4F));
		block.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, convert(0, 0, 0, 0, 225, 0, 0.4F));
		BLOCK = new ModelPerspectives(block);

		EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> item = new EnumMap<>(ItemCameraTransforms.TransformType.class);
		item.put(ItemCameraTransforms.TransformType.GROUND, convert(0, 2, 0, 0, 0, 0, 0.5F));
		item.put(ItemCameraTransforms.TransformType.HEAD, convert(0, 13, 7, 0, 180, 0, 1));
		TRSRTransformation thirdPersonItem = convert(0, 3, 1, 0, 0, 0, 0.55F);
		item.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPersonItem);
		item.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPersonItem));
		TRSRTransformation firstPersonItem = convert(1.13F, 3.2F, 1.13F, 0, -90, 25, 0.68F);
		item.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstPersonItem);
		item.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstPersonItem));
		item.put(ItemCameraTransforms.TransformType.FIXED, convert(0, 0, 0, 0, 180, 0, 1));
		NORMAL_ITEM = new ModelPerspectives(item);

		EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> handheld = new EnumMap<>(ItemCameraTransforms.TransformType.class);
		handheld.putAll(item);
		handheld.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, convert(0, 4, 0.5F, 0, -90, 55, 0.85F));
		handheld.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,  convert(0, 4, 0.5F, 0, 90, -55, 0.85F));
		handheld.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, convert(1.13F, 3.2F, 1.13F, 0, -90, 25, 0.68F));
		handheld.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,  convert(1.13F, 3.2F, 1.13F, 0, 90, -25, 0.68F));
		HANDHELD_ITEM = new ModelPerspectives(handheld);
	}

	private final EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> map;

	public ModelPerspectives(EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> map) {
		this.map = map;
	}

	public TRSRTransformation resolveTransformation(ItemCameraTransforms.TransformType transformType) {
		return map.get(transformType);
	}

	public Matrix4f resolveMatrix(ItemCameraTransforms.TransformType transformType) {
		return resolveTransformation(transformType).getMatrix();
	}

}
