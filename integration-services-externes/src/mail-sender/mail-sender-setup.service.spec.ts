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
});
