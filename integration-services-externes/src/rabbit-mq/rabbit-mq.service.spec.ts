/* eslint-disable @typescript-eslint/no-unused-vars */
import { Test, TestingModule } from '@nestjs/testing';
import { RabbitMqService } from './rabbit-mq.service';
import { MailerService } from '@nestjs-modules/mailer';
import { AmqpConnection, Nack } from '@golevelup/nestjs-rabbitmq';

describe('RabbitMqService', () => {
  let service: RabbitMqService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        RabbitMqService,
        {
          provide: MailerService,
          useValue: {
            sendMail: jest.fn(),
          },
        },
        {
          provide: AmqpConnection,
          useValue: {},
        },
      ],
    }).compile();

    service = module.get<RabbitMqService>(RabbitMqService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('RabbitMqServiceTest', () => {
    let service: RabbitMqService;
    let mailerService: MailerService;

    beforeEach(async () => {
      const module: TestingModule = await Test.createTestingModule({
        providers: [
          RabbitMqService,
          {
            provide: MailerService,
            useValue: {
              sendMail: jest.fn(),
            },
          },
          {
            provide: AmqpConnection,
            useValue: {},
          },
        ],
      }).compile();

      service = module.get<RabbitMqService>(RabbitMqService);
      mailerService = module.get<MailerService>(MailerService);
    });

    it('should be defined', () => {
      expect(service).toBeDefined();
    });

    it('should send an email', async () => {
      const msg = {
        email: 'amidrissiog@gmail.com',
        subject: 'idk something',
        text: '(._. )',
      };
      jest.spyOn(mailerService, 'sendMail').mockResolvedValueOnce(undefined);

      await service.pubSubHandler(JSON.stringify(msg));

      expect(mailerService.sendMail).toHaveBeenCalledWith({
        from: 'labo.sai.engineer@gmail.com',
        to: msg.email,
        subject: msg.subject,
        text: `Hello user, ${msg.text}`,
      });
      expect(service['counter']).toBe(0);
    });

    it('should retry sending email up to 100 times on failure', async () => {
      const msg = {
        email: 'amidrissiog@gmail.com',
        subject: 'idk something',
        text: 'mibomboclat',
      };
      jest
        .spyOn(mailerService, 'sendMail')
        .mockRejectedValue(new Error('Failed to send email'));

      const result = await service.pubSubHandler(JSON.stringify(msg));

      expect(mailerService.sendMail).toHaveBeenCalledTimes(1);
      expect(service['counter']).toBe(1);
      expect(result).toEqual(new Nack(true));
    });

    it('should stop retrying after 100 times', async () => {
      const msg = {
        email: 'amidrissiog@gmail.com',
        subject: 'idk something',
        text: 'mibomboclat',
      };
      jest
        .spyOn(mailerService, 'sendMail')
        .mockRejectedValue(new Error('Failed to send email'));

      service['counter'] = 100;
      const result = await service.pubSubHandler(JSON.stringify(msg));

      expect(mailerService.sendMail).toHaveBeenCalledTimes(1);
      expect(service['counter']).toBe(100);
      expect(result).toEqual(new Nack(false));
    });
  });
});
