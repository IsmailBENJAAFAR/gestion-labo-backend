import { Test, TestingModule } from '@nestjs/testing';
import { WbsocketGateway } from './wbsocket.gateway';
import { Server, Socket } from 'socket.io';

describe('WbsocketGateway', () => {
  let gateway: WbsocketGateway;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [WbsocketGateway],
    }).compile();

    gateway = module.get<WbsocketGateway>(WbsocketGateway);
  });

  it('should be defined', () => {
    expect(gateway).toBeDefined();
  });

  describe('WbsocketGatewayTest', () => {
    let gateway: WbsocketGateway;
    let server: Server;

    beforeEach(async () => {
      const module: TestingModule = await Test.createTestingModule({
        providers: [WbsocketGateway],
      }).compile();

      gateway = module.get<WbsocketGateway>(WbsocketGateway);
      server = gateway['server'] = new Server();
    });

    it('should be defined', () => {
      expect(gateway).toBeDefined();
    });

    it('should allow user to join room and return "allowed"', () => {
      const client = { join: jest.fn() } as unknown as Socket;
      const message = 'test-room';

      const result = gateway.userToJoinRoom(message, client);

      expect(client.join).toHaveBeenCalledWith(message);
      expect(result).toBe('allowed');
    });

    it('should handle message and emit notification', () => {
      const room = 'test-room';
      const data = { message: 'test' };
      const toSpy = jest.spyOn(server, 'to').mockReturnValue({
        emit: jest.fn(),
      } as any);

      gateway.handleMessage(room, data);

      expect(toSpy).toHaveBeenCalledWith(room);
      const emitSpy = toSpy.mock.results[0].value.emit;
      expect(emitSpy).toHaveBeenCalledWith('notification', data);
    });
  });
});
