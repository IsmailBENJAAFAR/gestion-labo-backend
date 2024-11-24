import { Controller, Get } from '@nestjs/common';
import { AppService } from './app.service';

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get('')
  getHello(): string {
    return this.appService.getHello();
  }

  // note: the app password will apparently be depricated by 2025
  @Get('testmail')
  sendTestMail() {
    try {
      this.appService.sendEmail(
        'moghitmi2@gmail.com',
        'testing the mail',
        'Hello world?',
      );
      return 'seems to be sent??';
    } catch {
      return 'there was some error';
    }
  }
}
