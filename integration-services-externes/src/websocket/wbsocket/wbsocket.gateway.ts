import {
  ConnectedSocket,
  MessageBody,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';

@WebSocketGateway({ cors: { origin: '*' } })
export class WbsocketGateway {
  @WebSocketServer()
  private server: Server;

  @SubscribeMessage('whoAreYou')
  userToJoinRoom(
    @MessageBody() message: string,
    @ConnectedSocket() client: Socket,
  ) {
    client.join(message);
    return 'allowed';
  }

  handleMessage(room: string, data: any): void {
    // room here will probably be some hashed user data (like for example their email and id)
    this.server.to(room).emit('notification', data);
  }
}
