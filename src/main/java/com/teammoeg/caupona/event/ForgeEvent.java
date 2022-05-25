/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.event;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;

import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.api.CauponaApi;
import com.teammoeg.caupona.data.RecipeReloadListener;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.fluid.SoupFluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEvent {
	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new RecipeReloadListener(event.getServerResources()));
	}
	private static ResourceLocation container=new ResourceLocation(Main.MODID,"container");
	@SubscribeEvent
	public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack is = event.getItemStack();
		if (is.getItem() == Items.BOWL) {
			Player playerIn = event.getPlayer();
			Level worldIn = event.getWorld();
			BlockPos blockpos = event.getPos();
			BlockEntity te = worldIn.getBlockEntity(blockpos);
			if (te != null) {
				te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace())
						.ifPresent(handler -> {
							FluidStack stack = handler.drain(250, FluidAction.SIMULATE);
							BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(stack.getFluid());
							if (recipe != null && stack.getAmount() == 250) {
								stack = handler.drain(stack, FluidAction.EXECUTE);
								if (stack.getAmount() == 250) {

									ItemStack ret = recipe.handle(stack);
									event.setCanceled(true);
									event.setCancellationResult(InteractionResult.sidedSuccess(worldIn.isClientSide));
									if (is.getCount() > 1) {
										is.shrink(1);
										if (!playerIn.addItem(ret)) {
											playerIn.drop(ret, false);
										}
									} else
										playerIn.setItemInHand(event.getHand(), ret);
								}
							}
						});
			}

		}
	}

	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
		ItemStack is = event.getItemStack();
		if (is.getItem() == Items.BOWL) {
			Level worldIn = event.getWorld();
			Player playerIn = event.getPlayer();
			BlockHitResult ray = Item.getPlayerPOVHitResult(worldIn, playerIn, Fluid.SOURCE_ONLY);
			if (ray.getType() == Type.BLOCK) {
				BlockPos blockpos = ray.getBlockPos();
				BlockState blockstate1 = worldIn.getBlockState(blockpos);
				net.minecraft.world.level.material.Fluid f = blockstate1.getFluidState().getType();
				if (f != Fluids.EMPTY) {
					BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(f);

					ItemStack ret = recipe.handle(f);
					event.setCanceled(true);
					event.setCancellationResult(InteractionResult.sidedSuccess(worldIn.isClientSide));
					if (is.getCount() > 1) {
						is.shrink(1);
						if (!playerIn.addItem(ret)) {
							playerIn.drop(ret, false);
						}
					} else
						playerIn.setItemInHand(event.getHand(), ret);
				}
			}
		}
	}
	@SubscribeEvent
	public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntityLiving() != null && !event.getEntityLiving().level.isClientSide
				&& event.getEntityLiving() instanceof ServerPlayer) {
			ItemStack stack= event.getItem();
			LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
			if (cap.isPresent()&&stack.getTags().anyMatch(t->t.location().equals(container))) {
				IFluidHandlerItem data = cap.resolve().get();
				if(data.getFluidInTank(0).getFluid() instanceof SoupFluid)
					CauponaApi.applyStew(event.getEntityLiving().level,event.getEntityLiving(),SoupFluid.getInfo(data.getFluidInTank(0)));
			}
		}
	}
}