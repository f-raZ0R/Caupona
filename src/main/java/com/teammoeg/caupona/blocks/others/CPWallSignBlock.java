package com.teammoeg.caupona.blocks.others;

import com.teammoeg.caupona.CPBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class CPWallSignBlock extends WallSignBlock {

	public CPWallSignBlock(Properties propertiesIn, WoodType woodTypeIn) {
		super(propertiesIn, woodTypeIn);
		CPBlocks.signs.add(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new CPSignTileEntity(pPos, pState);
	}
}