// import { Test, TestingModule } from '@nestjs/testing';
// import { INestApplication } from '@nestjs/common';
// import * as request from 'supertest';
// import { AppModule } from './app.module';
// import { AmqpConnection } from '@golevelup/nestjs-rabbitmq';

// describe('Main (e2e) <- idk how I did not even see this', () => {
//   let app: INestApplication<any>;

//   beforeAll(async () => {
//     const moduleFixture: TestingModule = await Test.createTestingModule({
//       imports: [AppModule],
//       providers: [
//         {
//           provide: AmqpConnection,
//           useValue: {
//             managedConnection: jest.fn(),
//           },
//         },
//       ],
//     }).compile();
//     app = moduleFixture.createNestApplication();
//     await app.init();
//   });

//   it('should start the application and listen on port 3000', async () => {
//     const response = await request(app.getHttpServer()).get('');
//     expect(response.text).toEqual('blyat');
//     expect(response.status).not.toEqual(404);
//   });

//   afterAll(async () => {
//     await app.close();
//   });
// });
