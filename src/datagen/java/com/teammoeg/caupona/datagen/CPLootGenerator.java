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

package com.teammoeg.caupona.datagen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.Contents.CPBlocks;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class CPLootGenerator extends LootTableProvider {

	public CPLootGenerator(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}
    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables() {
        return Arrays.asList(Pair.of(()->new LTBuilder(), LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((name, table) -> LootTables.validate(validationtracker, name, table));
    }

    private static class LTBuilder extends BlockLoot {
		@Override
        protected void addTables() {
        	dropSelf(CPBlocks.stew_pot);
        	dropSelf(CPBlocks.stove1);
        	dropSelf(CPBlocks.stove2);
        }
		ArrayList<Block> added=new ArrayList<>();
		@Override
		protected Iterable<Block> getKnownBlocks() {
			return added;
		}

		@Override
		public void dropOther(Block blockIn, ItemLike drop) {
			added.add(blockIn);
			super.dropOther(blockIn, drop);
		}

    }
}