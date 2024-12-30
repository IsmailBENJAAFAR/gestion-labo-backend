import { WbsocketGateway } from './websocket/wbsocket/wbsocket.gateway';
import { MailerService } from '@nestjs-modules/mailer';
import { Injectable } from '@nestjs/common';

@Injectable()
export class AppService {
  constructor(
    private mailSenderService: MailerService,
    private wb: WbsocketGateway,
  ) {}

  emitToUser(room: string, message: any) {
    this.wb.handleMessage(room, message);
  }

  sendEmail(to: string, subject: string, text: string) {
    this.mailSenderService.sendMail({
      from: 'labo.sai.engineer@gmail.com',
      to,
      subject,
      text,
    });
  }
}
