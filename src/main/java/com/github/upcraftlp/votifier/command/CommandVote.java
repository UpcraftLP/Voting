package com.github.upcraftlp.votifier.command;

import com.github.upcraftlp.votifier.api.reward.Reward;
import com.github.upcraftlp.votifier.config.VotifierConfig;
import com.github.upcraftlp.votifier.reward.store.RewardStoreWorldSavedData;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.*;

public class CommandVote extends CommandBase {

    @Override
    public String getName() {
        return "vote";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/vote [claim|get]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP playerMP = getCommandSenderAsPlayer(sender);
        if(args.length == 0) {
            playerMP.sendMessage(TextComponentUtils.processComponent(server, ITextComponent.Serializer.jsonToComponent(Reward.replace(VotifierConfig.voteCommand, sender, "")), playerMP));
            return;
        } else if(args.length == 1) {
            if("claim".equalsIgnoreCase(args[0])) {
                RewardStoreWorldSavedData wsd = RewardStoreWorldSavedData.get(playerMP.getServerWorld());
                if(wsd.getOutStandingRewardCount(playerMP.getName()) == 0) throw new CommandException("You have no outstanding rewards!");
                else wsd.claimRewards(playerMP.getName());
                return;
            }
            else if("get".equalsIgnoreCase(args[0])) {
                int outstandingRewards = RewardStoreWorldSavedData.get(playerMP.getServerWorld()).getOutStandingRewardCount(playerMP.getName());
                if(outstandingRewards == 0) sender.sendMessage(new TextComponentString("You have no outstanding rewards!"));
                else {
                    sender.sendMessage(getOutstandingRewardsText(outstandingRewards));
                }
                return;
            }
        }
        throw new WrongUsageException(getUsage(sender));
    }

    public static ITextComponent getOutstandingRewardsText(int rewardsOutstanding) {
        ITextComponent comp1 = new TextComponentString("You have " + rewardsOutstanding + " rewards outstanding, use ");
        ITextComponent comp2 = new TextComponentString("/vote claim").setStyle(new Style().setColor(TextFormatting.GREEN));
        ITextComponent comp3 = new TextComponentString(" to claim or ");
        ITextComponent clickToVote = new TextComponentString("click to vote").setStyle(new Style().setColor(TextFormatting.AQUA).setItalic(false));
        ITextComponent comp4 = new TextComponentString("click here").setStyle(new Style().setColor(TextFormatting.AQUA).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickToVote)).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote claim")));
        ITextComponent comp5 = new TextComponentString(" to claim them!");
        comp1.appendSibling(comp2).appendSibling(comp3).appendSibling(comp4).appendSibling(comp5);
        return comp1;
    }
}