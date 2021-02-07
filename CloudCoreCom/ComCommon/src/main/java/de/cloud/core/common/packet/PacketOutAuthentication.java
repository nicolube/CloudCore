package de.cloud.core.common.packet;

import de.cloud.core.common.ConnectionStatus;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PacketOutAuthentication extends Packet {

    private ConnectionStatus status;

    @Override
    public void read(ByteBuf byteBuf) {
        autoRead(this, byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        autoWrite(this, byteBuf);
    }
}
