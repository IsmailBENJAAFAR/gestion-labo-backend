import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { MailerModule } from '@nestjs-modules/mailer';
import { MailSenderSetupService } from './mail-sender/mail-sender-setup.service';
import { WbsocketGateway } from './websocket/wbsocket/wbsocket.gateway';

@Module({
  imports: [
    MailerModule.forRoot({
      transport: {
        host: 'smtp.gmail.com',
        port: 587,
        secure: false, // true for port 465, false for other ports
        tls: {
          rejectUnauthorized: false,
        },
        auth: {
          ...MailSenderSetupService.getCreds(),
          accessToken: MailSenderSetupService.getToken(),
        },
      },
    }),
  ],
  controllers: [AppController],
  providers: [AppService, WbsocketGateway],
})
export class AppModule {
  hello: string;
}
