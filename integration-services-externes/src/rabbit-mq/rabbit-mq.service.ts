import {
  AmqpConnection,
  Nack,
  RabbitSubscribe,
} from '@golevelup/nestjs-rabbitmq';
import { MailerService } from '@nestjs-modules/mailer';
import { Injectable } from '@nestjs/common';

@Injectable()
export class RabbitMqService {
  private counter = 0;

  constructor(
    private mailSenderService: MailerService,
    private readonly amqpConnection: AmqpConnection,
  ) {}

  @RabbitSubscribe({
    exchange: 'mainExchange',
    routingKey: 'user.mail.*',
    queue: 'userInfo',
    allowNonJsonMessages: true,
    batchOptions: {
      size: 1000,
      timeout: 5000,
    },
  })
  public async pubSubHandler(msg: string): Promise<Nack> {
    const jsonResp = JSON.parse(msg);

    const email: string = jsonResp.email;
    const subject: string = jsonResp.subject;
    const text: string = jsonResp.text;

    try {
      await this.mailSenderService.sendMail({
        from: 'labo.sai.engineer@gmail.com',
        to: email,
        subject: subject,
        text: `Hello user, ${text}`,
      });
      this.counter = 0;
    } catch {
      if (this.counter < 100) {
        this.counter++;
        console.log(`Could not send email, retrying...`);
        await new Promise((resolve) => setTimeout(resolve, 1000));
        return new Nack(true);
      } else {
        return new Nack(false);
      }
    }
  }
}
