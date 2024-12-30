import { Test, TestingModule } from '@nestjs/testing';
import { AppService } from './app.service';
import { WbsocketGateway } from './websocket/wbsocket/wbsocket.gateway';
import { MailerService } from '@nestjs-modules/mailer';

describe('AppService', () => {
  let appService: AppService;
  let wbGateway: WbsocketGateway;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AppService,
        {
          provide: WbsocketGateway,
          useValue: { handleMessage: jest.fn() },
        },
        {
          provide: MailerService,
          useValue: { sendMail: jest.fn() },
        },
      ],
    }).compile();

    appService = module.get<AppService>(AppService);
    wbGateway = module.get<WbsocketGateway>(WbsocketGateway);
  });

  it('should call handleMessage on WbsocketGateway with correct arguments', () => {
    const room = 'testRoom';
    const message = { text: 'testMessage' };

    appService.emitToUser(room, message);

    expect(wbGateway.handleMessage).toHaveBeenCalledWith(room, message);
  });

  it('should call sendMail on MailerService with correct arguments', () => {
    const to = 'kubo@sama.com';
    const subject = 'you have been bomboclated';
    const text = ':)';

    appService.sendEmail(to, subject, text);

    expect(appService['mailSenderService'].sendMail).toHaveBeenCalledWith({
      from: 'labo.sai.engineer@gmail.com',
      to,
      subject,
      text,
    });
  });
});
