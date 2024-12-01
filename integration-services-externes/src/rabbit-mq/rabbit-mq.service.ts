// import { AmqpConnection } from '@golevelup/nestjs-rabbitmq';
import { RabbitSubscribe } from '@golevelup/nestjs-rabbitmq';
import { Injectable } from '@nestjs/common';

@Injectable()
export class RabbitMqService {
  @RabbitSubscribe({
    exchange: 'main_exchange',
    routingKey: 'user.*',
    queue: 'user_info',
  })
  public async pubSubHandler(msg: any) {
    console.log(`Received pub/sub message: ${msg}`);
  }
}
