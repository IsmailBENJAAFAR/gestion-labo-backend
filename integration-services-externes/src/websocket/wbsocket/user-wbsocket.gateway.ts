import { RabbitSubscribe } from '@golevelup/nestjs-rabbitmq';
import {
  ConnectedSocket,
  MessageBody,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';

@WebSocketGateway({ cors: { origin: '*' }, namespace: '/standard' })
export class StandardWbsocketGateway {
  @WebSocketServer()
  private server: Server;

  @SubscribeMessage('connect')
  userToJoinRoom(
    @MessageBody() message: string,
    @ConnectedSocket() client: Socket,
  ) {
    // TODO: might need some verification here for the role from the auth server
    const room = Buffer.from(message).toString('base64');
    client.join(room);
    return 'allowed';
  }

  // notifications to be sent will be controlled thru rabbitMq message publishing
  // publish to these cred to publish a message to normal users
  @RabbitSubscribe({
    exchange: 'mainExchange',
    routingKey: 'user.notify.*',
    queue: 'user_info',
    allowNonJsonMessages: true,
    batchOptions: {
      size: 1000,
      timeout: 5000,
    },
  })
  handleMessage(jsonMessage: string): void {
    const jsonPayload = JSON.parse(jsonMessage);
    // either throu patient id or email ( I think )
    const room = Buffer.from(jsonPayload.patientId).toString('base64');
    const notification = Buffer.from(jsonPayload.msg).toString('base64');
    this.server.to(room).emit('notification', notification);
  }
}
