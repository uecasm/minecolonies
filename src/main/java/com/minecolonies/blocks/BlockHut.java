package com.minecolonies.blocks;

import com.minecolonies.configuration.Configurations;
import com.minecolonies.creativetab.ModCreativeTabs;
import com.minecolonies.entity.EntityCitizen;
import com.minecolonies.entity.PlayerProperties;
import com.minecolonies.lib.Constants;
import com.minecolonies.lib.IColony;
import com.minecolonies.tileentities.TileEntityBuildable;
import com.minecolonies.tileentities.TileEntityHut;
import com.minecolonies.tileentities.TileEntityHutWorker;
import com.minecolonies.tileentities.TileEntityTownHall;
import com.minecolonies.util.LanguageHandler;
import com.minecolonies.util.Utils;
import cpw.mods.fml.common.registry.GameRegistry;
import ibxm.Player;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class BlockHut extends Block implements IColony, ITileEntityProvider
{
    protected int workingRange;

    private IIcon[] icons = new IIcon[6];// 0 = top, 1 = bot, 2-5 = sides;

    public BlockHut()
    {
        super(Material.wood);
        setBlockName(getName());
        setCreativeTab(ModCreativeTabs.MINECOLONIES);
        setResistance(1000f);
        GameRegistry.registerBlock(this, getName());
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        icons[0] = iconRegister.registerIcon(Constants.MODID + ":" + getName() + "Top");
        icons[1] = icons[0];
        for(int i = 2; i <= 5; i++)
        {
            icons[i] = iconRegister.registerIcon(Constants.MODID + ":" + "sideChest");
        }
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return icons[side];
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack)
    {
        if(world.isRemote) return;

        if(entityLivingBase instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entityLivingBase;
            TileEntityHut hut = (TileEntityHut) world.getTileEntity(x, y, z);
            if(hut instanceof TileEntityTownHall)
            {
                TileEntityTownHall townhall = (TileEntityTownHall) hut;
                townhall.onBlockAdded();
                townhall.setInfo(world, player.getUniqueID(), x, z);
                townhall.setCityName(LanguageHandler.format("com.minecolonies.gui.townhall.defaultName", player.getDisplayName()));
                PlayerProperties.get(player).placeTownhall(x, y, z);
            }
            else
            {
                TileEntityTownHall townhall = Utils.getTownhallByOwner(world, player);

                hut.setTownHall(townhall);
                townhall.addHut(hut.xCoord, hut.yCoord, hut.zCoord);

                if(hut instanceof TileEntityHutWorker)
                {
                    ((TileEntityHutWorker) hut).attemptToAddIdleCitizen(townhall);
                }
            }
        }
    }
}
