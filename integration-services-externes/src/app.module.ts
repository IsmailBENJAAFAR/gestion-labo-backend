import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { MailerModule } from '@nestjs-modules/mailer';
import { MailSenderSetupService } from './mail-sender/mail-sender-setup.service';
import { RabbitMqService } from './rabbit-mq/rabbit-mq.service';
import { RabbitMQModule } from '@golevelup/nestjs-rabbitmq';
import { AdminWbsocketGateway } from './websocket/admin-wbsocket/admin-wbsocket.gateway';
import { StandardWbsocketGateway } from './websocket/wbsocket/user-wbsocket.gateway';

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
  providers: [
    AppService,
    AdminWbsocketGateway,
    StandardWbsocketGateway,
    RabbitMqService,
    AdminWbsocketGateway,
  ],
  exports: [RabbitMQModule],
})
export class AppModule {
  hello: string;
}
