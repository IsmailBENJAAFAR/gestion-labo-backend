import { Test, TestingModule } from '@nestjs/testing';
import { AppService } from './app.service';
import { MailerService } from '@nestjs-modules/mailer';

describe('AppService', () => {
  let appService: AppService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AppService,
        {
          provide: MailerService,
          useValue: { sendMail: jest.fn() },
        },
      ],
    }).compile();

    appService = module.get<AppService>(AppService);
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
