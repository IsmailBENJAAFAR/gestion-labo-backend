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
    console.log('WHO ARE YOU');
    console.log(client.rooms);
    client.join(message);
    console.log(client.rooms);
    return 'good';
  }

  handleMessage(room: string, data: string): void {
    this.server.to(room).emit('notification', data);
  }
}
