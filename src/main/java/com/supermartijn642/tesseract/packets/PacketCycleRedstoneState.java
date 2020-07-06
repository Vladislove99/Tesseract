package com.supermartijn642.tesseract.packets;

import com.supermartijn642.tesseract.TesseractTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created 7/5/2020 by SuperMartijn642
 */
public class PacketCycleRedstoneState {

    private BlockPos pos;

    public PacketCycleRedstoneState(BlockPos pos){
        this.pos = pos;
    }

    public void encode(PacketBuffer buffer){
        buffer.writeBlockPos(this.pos);
    }

    public static PacketCycleRedstoneState decode(PacketBuffer buffer){
        return new PacketCycleRedstoneState(buffer.readBlockPos());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().setPacketHandled(true);
        PlayerEntity player = ctx.get().getSender();
        if(player == null)
            return;
        World world = player.world;
        if(world == null)
            return;
        ctx.get().enqueueWork(() -> {
            TileEntity tile = world.getTileEntity(this.pos);
            if(tile instanceof TesseractTile)
                ((TesseractTile)tile).cycleRedstoneState();
        });
    }
}
