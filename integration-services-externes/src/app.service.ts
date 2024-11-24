import { MailerService } from '@nestjs-modules/mailer';
import { Injectable } from '@nestjs/common';

@Injectable()
export class AppService {
  constructor(private mailSenderService: MailerService) {}

  getHello(): string {
    return 'Hello World!';
  }

  sendEmail(to: string, subject: string, text: string) {
    this.mailSenderService.sendMail({
      from: process.env.GMAIL_ACC,
      to,
      subject,
      text,
    });
  }
}
