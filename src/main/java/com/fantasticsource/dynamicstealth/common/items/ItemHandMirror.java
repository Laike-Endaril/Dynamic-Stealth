package com.fantasticsource.dynamicstealth.common.items;

import com.fantasticsource.dynamicstealth.common.BlocksAndItems;
import com.fantasticsource.mctools.cliententity.Camera;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static com.fantasticsource.dynamicstealth.DynamicStealth.MODID;

public class ItemHandMirror extends Item
{
    public ItemHandMirror()
    {
        setCreativeTab(BlocksAndItems.creativeTab);

        setUnlocalizedName(MODID + ":handmirror");
        setRegistryName("handmirror");
    }

    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);


        //Success / failure conditions (execute both sides)
//        return new ActionResult<>(EnumActionResult.FAIL, itemstack);


        //Sided execution
        if (world.isRemote)
        {
            Camera.allowControl = true;
            Camera.playerRenderMode = Camera.PLAYER_RENDER_IF_THIRD_PERSON;
            Camera.followOffsetLR = 0;
            Camera.getCamera().activate(player, -1);
        }
        else
        {
            //TODO handle server-side sight sense position offset
        }


        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
        World world = player.world;

        //Sided execution
        if (world.isRemote)
        {
            Camera.followOffsetLR = Tools.min((double) (getMaxItemUseDuration(stack) - count) / 20, 1);
            if (player.getActiveHand() == EnumHand.OFF_HAND) Camera.followOffsetLR = -Camera.followOffsetLR;
        }
        else
        {
            //TODO handle sight sense
        }
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft)
    {
        //Sided execution
        if (world.isRemote)
        {
            Camera.getCamera().deactivate();
        }
        else
        {
            //TODO handle sight sense
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add("Lets you peek around corners");
    }
}
