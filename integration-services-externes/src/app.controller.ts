import { Controller, Get } from '@nestjs/common';
import { AppService } from './app.service';
import { AmqpConnection } from '@golevelup/nestjs-rabbitmq';

@Controller()
export class AppController {
  constructor(
    private readonly appService: AppService,
    private readonly amqpConnection: AmqpConnection,
  ) {}

  @Get('')
  getHello(): string {
    this.amqpConnection.publish('main_exchange', 'user.pro', 'hello_world');
    return this.appService.getHello();
  }

  @Get('testmail')
  sendTestMail() {
    try {
      this.appService.sendEmail(
        'moghitmi2@gmail.com',
        'testing the mail',
        'Hello world? with OAuth2',
      );
      return 'seems to be sent??';
    } catch {
      return 'there was some error';
    }
  }
}
