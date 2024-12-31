import { RabbitSubscribe } from '@golevelup/nestjs-rabbitmq';
import {
  ConnectedSocket,
  MessageBody,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';

@WebSocketGateway({ cors: { origin: '*' }, namespace: '/admin' })
export class AdminWbsocketGateway {
  @WebSocketServer()
  private server: Server;

  @SubscribeMessage('connectAdmin')
  adminToJoinRoom(
    @MessageBody() message: string,
    @ConnectedSocket() client: Socket,
  ) {
    // TODO: Needs a checks for admin role
    const room = Buffer.from(message).toString('base64');
    client.join(room);
    return 'allowed';
  }

  // same as user but with different creds
  // publish to these cred to send notifications to an admin(s)
  @RabbitSubscribe({
    exchange: 'mainExchange',
    routingKey: 'admin.notify.*',
    queue: 'adminNotify',
    allowNonJsonMessages: true,
    batchOptions: {
      size: 1000,
      timeout: 5000,
    },
  })
  handleMessage(jsonMessage: string): void {
    const jsonPayload = JSON.parse(jsonMessage);
    const room = Buffer.from(jsonPayload.adminId).toString('base64');
    const notification = Buffer.from(jsonPayload.msg).toString('base64');
    this.server.to(room).emit('notification', notification);
  }
}
