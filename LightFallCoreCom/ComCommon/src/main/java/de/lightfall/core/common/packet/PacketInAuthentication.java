package de.lightfall.core.common.packet;

import de.lightfall.core.api.ClientType;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PacketInAuthentication extends Packet {

    private ClientType type;
    private String key;
    private String comment;
    
    @Override
    public void read(ByteBuf byteBuf) {
        autoRead(this, byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        autoWrite(this, byteBuf);
    }
}
