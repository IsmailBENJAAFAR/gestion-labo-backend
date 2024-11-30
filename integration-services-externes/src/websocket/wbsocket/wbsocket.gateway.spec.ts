import { Test, TestingModule } from '@nestjs/testing';
import { WbsocketGateway } from './wbsocket.gateway';

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
});
