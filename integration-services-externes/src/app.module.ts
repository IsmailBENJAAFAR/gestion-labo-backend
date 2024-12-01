import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { MailerModule } from '@nestjs-modules/mailer';
import { MailSenderSetupService } from './mail-sender/mail-sender-setup.service';
import { WbsocketGateway } from './websocket/wbsocket/wbsocket.gateway';
import { RabbitMqService } from './rabbit-mq/rabbit-mq.service';
import { RabbitMQModule } from '@golevelup/nestjs-rabbitmq';

@Module({
  imports: [
    MailerModule.forRoot({
      transport: {
        host: 'smtp.gmail.com',
        port: 587,
        secure: false,
        tls: {
          rejectUnauthorized: false,
        },
        auth: {
          ...MailSenderSetupService.getCreds(),
          accessToken: MailSenderSetupService.getToken(),
        },
      },
    }),
    RabbitMQModule.forRoot(RabbitMQModule, {
      uri: 'amqp://user:password@localhost:5672',
      exchanges: [{ name: 'main_exchange', type: 'topic' }],
      queues: [
        {
          name: 'user_info',
          createQueueIfNotExists: true,
          options: {
            durable: true,
          },
          exchange: 'main_exchange',
          routingKey: 'user.*',
        },
        {
          name: 'test',
          createQueueIfNotExists: true,
          options: {
            durable: true,
          },
          exchange: 'main_exchange',
          routingKey: 'test.*',
        },
        RabbitMQModule,
      ],
    }),
  ],
  controllers: [AppController],
  providers: [AppService, WbsocketGateway, RabbitMqService],
  exports: [RabbitMQModule],
})
export class AppModule {
  hello: string;
}
