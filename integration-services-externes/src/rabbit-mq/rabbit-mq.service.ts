import {
  AmqpConnection,
  Nack,
  RabbitSubscribe,
} from '@golevelup/nestjs-rabbitmq';
import { MailerService } from '@nestjs-modules/mailer';
import { Injectable } from '@nestjs/common';
import { Channel, ConsumeMessage } from 'amqplib';

@Injectable()
export class RabbitMqService {
  private counter = 0;

  constructor(
    private mailSenderService: MailerService,
    private readonly amqpConnection: AmqpConnection,
  ) {}

  @RabbitSubscribe({
    exchange: 'main_exchange',
    routingKey: 'user.*',
    queue: 'user_info',
    allowNonJsonMessages: true,
    batchOptions: {
      size: 1000,
      timeout: 5000,
      errorHandler: batchErrorHandler,
    },
  })
  public async pubSubHandler(msg: any) {
    try {
      this.mailSenderService.sendMail({
        from: 'labo.sai.engineer@gmail.com',
        to: 'moghitmi2@gmail.com',
        subject: 'testing the mail',
        text: `Hello world? with OAuth2, ${msg}`,
      });
      console.log(`send email: ${msg} ${this.counter}`);
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
function batchErrorHandler(
  channel: Channel,
  msg: ConsumeMessage[],
  error: any,
): void | Promise<void> {
  console.log(
    `Could not send email with message ${msg.map((m) => m.content)}: error<${error}>`,
  );
}
