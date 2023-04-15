package com.supermartijn642.tesseract.packets;

import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.core.util.Pair;
import com.supermartijn642.tesseract.manager.TesseractReference;
import com.supermartijn642.tesseract.manager.TesseractTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created 14/04/2023 by SuperMartijn642
 */
public class PacketRemoveTesseractReferences implements BasePacket {

    private Collection<Pair<String,BlockPos>> references;

    public PacketRemoveTesseractReferences(Collection<TesseractReference> references){
        this.references = references.stream().map(reference -> Pair.of(reference.getDimension(), reference.getPos())).collect(Collectors.toSet());
    }

    public PacketRemoveTesseractReferences(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeInt(this.references.size());
        for(Pair<String,BlockPos> reference : this.references){
            buffer.writeUtf(reference.left());
            buffer.writeBlockPos(reference.right());
        }
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        int size = buffer.readInt();
        this.references = new ArrayList<>(size);
        for(int i = 0; i < size; i++)
            this.references.add(Pair.of(buffer.readUtf(), buffer.readBlockPos()));
    }

    @Override
    public void handle(PacketContext context){
        if(context.getHandlingSide().isServer())
            return;
        for(Pair<String,BlockPos> reference : this.references)
            TesseractTracker.CLIENT.remove(reference.left(), reference.right());
    }
}
