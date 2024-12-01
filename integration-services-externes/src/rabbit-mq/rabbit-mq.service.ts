import { RabbitSubscribe } from '@golevelup/nestjs-rabbitmq';
import { MailerService } from '@nestjs-modules/mailer';
import { Injectable } from '@nestjs/common';

@Injectable()
export class RabbitMqService {
  constructor(private mailSenderService: MailerService) {}

  @RabbitSubscribe({
    exchange: 'main_exchange',
    routingKey: 'user.*',
    queue: 'user_info',
    allowNonJsonMessages: true,
  })
  public async pubSubHandler(msg: any) {
    try {
      this.mailSenderService.sendMail({
        from: 'labo.sai.engineer@gmail.com',
        to: 'moghitmi2@gmail.com',
        subject: 'testing the mail',
        text: `Hello world? with OAuth2, ${msg}`,
      });
      console.log(`Sent email: ${msg}`);
      //   return new Nack(false);
    } catch {
      console.log(`Could not send email: ${msg}`);
    }
  }
}
