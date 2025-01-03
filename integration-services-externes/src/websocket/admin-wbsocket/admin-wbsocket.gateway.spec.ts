import { Test, TestingModule } from '@nestjs/testing';
import { Server, Socket } from 'socket.io';
import { AdminWbsocketGateway } from './admin-wbsocket.gateway';

describe('AdminWbsocketGateway', () => {
  let gateway: AdminWbsocketGateway;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [AdminWbsocketGateway],
    }).compile();

    gateway = module.get<AdminWbsocketGateway>(AdminWbsocketGateway);
  });

  it('should be defined', () => {
    expect(gateway).toBeDefined();
  });

  describe('AdminWbsocketGatewayTest', () => {
    let gateway: AdminWbsocketGateway;
    let server: Server;

    beforeEach(async () => {
      const module: TestingModule = await Test.createTestingModule({
        providers: [AdminWbsocketGateway],
      }).compile();

      gateway = module.get<AdminWbsocketGateway>(AdminWbsocketGateway);
      server = gateway['server'] = new Server();
    });

    it('should be defined', () => {
      expect(gateway).toBeDefined();
    });

    it('should allow amin to join room and return "allowed"', () => {
      const client = { join: jest.fn() } as unknown as Socket;
      const message = 'admin-bozo';

      const result = gateway.adminToJoinRoom(message, client);
      const actualMessage = Buffer.from(message).toString('base64');

      expect(client.join).toHaveBeenCalledWith(actualMessage);
      expect(result).toBe('allowed');
    });

    it('should handle message and emit notification to admin', () => {
      const data = { adminId: '69', msg: 'I also feel sick' };
      const toSpy = jest.spyOn(server, 'to').mockReturnValue({
        emit: jest.fn(),
      } as any);

      gateway.handleMessage(JSON.stringify(data));

      const room = Buffer.from(data.adminId).toString('base64');
      const notification = Buffer.from(data.msg).toString('base64');

      expect(toSpy).toHaveBeenCalledWith(room);
      const emitSpy = toSpy.mock.results[0].value.emit;
      expect(emitSpy).toHaveBeenCalledWith('notification', notification);
    });
  });
});
