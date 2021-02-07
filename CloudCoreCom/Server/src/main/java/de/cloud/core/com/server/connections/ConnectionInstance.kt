package de.cloud.core.com.server.connections

import de.cloud.core.api.ClientType
import de.cloud.core.common.ConnectionStatus
import io.netty.channel.Channel

class ConnectionInstance(channel: Channel, clientType: ClientType) {
    var connectionStats: ConnectionStatus = ConnectionStatus.AUTH_PENDING
}
