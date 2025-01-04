import { Test, TestingModule } from '@nestjs/testing';
import { StandardWbsocketGateway } from './user-wbsocket.gateway';
import { Server, Socket } from 'socket.io';

describe('StandardWbsocketGateway', () => {
  let gateway: StandardWbsocketGateway;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [StandardWbsocketGateway],
    }).compile();

    gateway = module.get<StandardWbsocketGateway>(StandardWbsocketGateway);
  });

  it('should be defined', () => {
    expect(gateway).toBeDefined();
  });

  describe('StandardWbsocketGatewayTest', () => {
    let gateway: StandardWbsocketGateway;
    let server: Server;

    beforeEach(async () => {
      const module: TestingModule = await Test.createTestingModule({
        providers: [StandardWbsocketGateway],
      }).compile();

      gateway = module.get<StandardWbsocketGateway>(StandardWbsocketGateway);
      server = gateway['server'] = new Server();
    });

    it('should be defined', () => {
      expect(gateway).toBeDefined();
    });

    it('should allow user to join room and return "allowed"', () => {
      const client = { join: jest.fn() } as unknown as Socket;
      const message = 'bozo';

      const result = gateway.userToJoinRoom(message, client);
      const actualMessage = Buffer.from(message).toString('base64');

      expect(client.join).toHaveBeenCalledWith(actualMessage);
      expect(result).toBe('allowed');
    });

    it('should handle message and emit notification', () => {
      const data = { patientId: '1', msg: 'I feel sick' };
      const toSpy = jest.spyOn(server, 'to').mockReturnValue({
        emit: jest.fn(),
      } as any);

      gateway.handleMessage(JSON.stringify(data));

      const room = Buffer.from(data.patientId).toString('base64');
      const notification = Buffer.from(data.msg).toString('base64');

      expect(toSpy).toHaveBeenCalledWith(room);
      const emitSpy = toSpy.mock.results[0].value.emit;
      expect(emitSpy).toHaveBeenCalledWith('notification', notification);
    });
  });
});
