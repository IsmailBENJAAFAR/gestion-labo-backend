import { Test, TestingModule } from '@nestjs/testing';
import { MailSenderSetupService } from './mail-sender-setup.service';

describe('MailSenderSetupService', () => {
  let service: MailSenderSetupService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [MailSenderSetupService],
    }).compile();

    service = module.get<MailSenderSetupService>(MailSenderSetupService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('MailSenderSetupServiceTest', () => {
    beforeEach(async () => {
      const module: TestingModule = await Test.createTestingModule({
        providers: [MailSenderSetupService],
      }).compile();

      service = module.get<MailSenderSetupService>(MailSenderSetupService);
    });

    it('should be defined', () => {
      expect(service).toBeDefined();
    });

    it('should return OAuth credentials', () => {
      const creds = MailSenderSetupService.getCreds();
      expect(creds).toEqual({
        type: 'OAuth2',
        user: 'labo.sai.engineer@gmail.com',
        clientId: process.env.CLIENT_ID,
        clientSecret: process.env.CLIENT_SECRET,
        refreshToken: process.env.REFRESH_TOKEN,
      });
    });
  });
});
