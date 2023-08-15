package com.teammoeg.caupona.client.util;

import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.model.data.ModelData;

public class ModelUtils {

	public static void renderQuads(List<BakedQuad> quads, VertexConsumer renderer, PoseStack transform,
			int color, int light, int overlay) {
		float red = 1;
		float green = 1;
		float blue = 1;
		if (color >= 0) {
			red = (color >> 16 & 255) / 255F;
			green = (color >> 8 & 255) / 255F;
			blue = (color & 255) / 255F;
		}
		for (BakedQuad quad : quads)
			renderer.putBulkData(transform.last(), quad, red, green, blue, light, overlay);
	}
	public static void tesellate(BlockEntity be,BakedModel model, VertexConsumer renderer, PoseStack transform, int overlay,ModelData data) {
		Minecraft.getInstance().getBlockRenderer().getModelRenderer()
		.tesselateBlock(be.getLevel(),model, be.getBlockState(), be.getBlockPos(), transform, renderer, true, DynamicBlockModelReference.getRandomSource(),42L, overlay,data, null);
	}
	public static void tesellate(BlockEntity be,DynamicBlockModelReference model, VertexConsumer renderer, PoseStack transform, int overlay,ModelData data) {
		tesellate(be,model.get(), renderer, transform, overlay,data);
	}
	public static void tesellateModel(BlockEntity be,DynamicBlockModelReference model, VertexConsumer renderer, PoseStack transform, int overlay) {
		tesellate(be, model,renderer, transform, overlay,ModelData.EMPTY);
	}
	public static void tesellateModelGroups(BlockEntity be,DynamicBlockModelReference model, VertexConsumer renderer,ImmutableSet<String> groups, PoseStack transform, int overlay) {
		tesellate(be, model,renderer, transform, overlay,ModelData.builder().with(DisplayGroupProperty.PROPERTY,groups).build());
	}
	public static DynamicBlockModelReference getModel(String name) {
		return new DynamicBlockModelReference(name);
	}
	public static DynamicBlockModelReference getModel(String modid,String name) {
		return new DynamicBlockModelReference(new ResourceLocation(modid, "block/dynamic/"+name));
	}
	public static void renderModelGroups(DynamicBlockModelReference model, VertexConsumer renderer,ImmutableSet<String> groups,PoseStack transform,
			int color, int light, int overlay) {
		renderQuads(model.apply(ModelData.builder().with(DisplayGroupProperty.PROPERTY,groups).build()),renderer,transform,color,light,overlay);
	}
	public static void renderModel(DynamicBlockModelReference model, VertexConsumer renderer, PoseStack transform,
			int color, int light, int overlay) {
		renderQuads(model.getAllQuads(),renderer,transform,color,light,overlay);
	}
	public static void renderModelGroups(DynamicBlockModelReference model, VertexConsumer renderer,ImmutableSet<String> groups,PoseStack transform, int light, int overlay) {
		renderModelGroups(model, renderer, groups, transform, -1, light, overlay);
	}
	public static void renderModel(DynamicBlockModelReference model, VertexConsumer renderer, PoseStack transform, int light, int overlay) {
		renderModel(model, renderer, transform, -1, light, overlay);
	}
	
}
