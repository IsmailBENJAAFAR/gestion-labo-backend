import { Injectable } from '@nestjs/common';
import { google } from 'googleapis';
import { AuthenticationTypeOAuth2 } from 'nodemailer/lib/smtp-connection';

@Injectable()
export class MailSenderSetupService {
  private static accessTokenO2: string;
  private static oauthCred: AuthenticationTypeOAuth2 = {
    type: 'OAuth2',
    user: 'labo.sai.engineer@gmail.com',
    clientId: process.env.CLIENT_ID,
    clientSecret: process.env.CLIENT_SECRET,
    refreshToken: process.env.REFRESH_TOKEN,
  };

  private static async getAuthToken() {
    const OAuth2 = google.auth.OAuth2;
    const oauth2client = new OAuth2(
      this.oauthCred.clientId, // ClientID
      this.oauthCred.clientSecret, // Client Secret
      process.env.REDIRECT_URL, // Redirect URL
    );
    oauth2client.setCredentials({
      refresh_token: process.env.REFRESH_TOKEN,
    });

    const accessToken = oauth2client.getAccessToken();

    accessToken.then((value) => {
      this.accessTokenO2 = value.token;
    });
  }

  static getCreds() {
    return this.oauthCred;
  }

  static getToken() {
    this.getAuthToken();
    return this.accessTokenO2;
  }
}
