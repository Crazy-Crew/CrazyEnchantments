package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Crops;

public class NMS_v1_12_2_Down implements NMSSupport {
    
    @Override
    public void fullyGrowPlant(Block block) {
        if (Version.isNewer(Version.v1_8_R3)) {
            if (block.getState().getData() instanceof Crops) {
                Crops crop = (Crops) block.getState().getData();
                CropState state = crop.getState();
                if (state != CropState.RIPE) {
                    crop.setState(CropState.RIPE);
                    block.setData(crop.getData());
                }
            } else if (block.getType() == Material.MELON_STEM || block.getType() == Material.PUMPKIN_STEM ||
            block.getType() == Material.BEETROOT_BLOCK) {
                block.setData((byte) 7);
            } else if (block.getType() == Material.NETHER_WARTS) {
                block.setData((byte) 3);
            } else if (block.getType() == Material.COCOA) {
                block.setData((byte) 11);
            }
        } else {
            if (block.getType() == Material.POTATO || block.getType() == Material.CARROT ||
            block.getType() == Material.MELON_STEM || block.getType() == Material.PUMPKIN_STEM ||
            block.getType() == Material.CROPS) {
                block.setData((byte) 7);
            } else if (block.getType() == Material.NETHER_WARTS) {
                block.setData((byte) 3);
            } else if (block.getType() == Material.COCOA) {
                block.setData((byte) 11);
            }
        }
    }
    
    @Override
    public boolean isFullyGrown(Block block) {
        if (Version.isNewer(Version.v1_8_R3)) {
            if (block.getState().getData() instanceof Crops) {
                return ((Crops) block.getState().getData()).getState() == CropState.RIPE;
            } else if (block.getType() == Material.MELON_STEM || block.getType() == Material.PUMPKIN_STEM ||
            block.getType() == Material.BEETROOT_BLOCK) {
                return block.getData() == 7;
            } else if (block.getType() == Material.NETHER_WARTS) {
                return block.getData() == 3;
            } else if (block.getType() == Material.COCOA) {
                return block.getData() == 11;
            }
        } else {
            if (block.getType() == Material.POTATO || block.getType() == Material.CARROT ||
            block.getType() == Material.MELON_STEM || block.getType() == Material.PUMPKIN_STEM ||
            block.getType() == Material.CROPS) {
                return block.getData() == 7;
            } else if (block.getType() == Material.NETHER_WARTS) {
                return block.getData() == 3;
            } else if (block.getType() == Material.COCOA) {
                return block.getData() == 11;
            }
        }
        return true;
    }
    
    @Override
    public void hydrateSoil(Block soil) {
        if (soil.getType() == Material.SOIL) {
            soil.setData((byte) 7);
        }
    }
    
}