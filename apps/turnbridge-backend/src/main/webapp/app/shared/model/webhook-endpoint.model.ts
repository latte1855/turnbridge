import { ITenant } from 'app/shared/model/tenant.model';
import { WebhookStatus } from 'app/shared/model/enumerations/webhook-status.model';

export interface IWebhookEndpoint {
  id?: number;
  name?: string;
  targetUrl?: string;
  secret?: string | null;
  events?: string;
  status?: keyof typeof WebhookStatus;
  tenant?: ITenant | null;
}

export const defaultValue: Readonly<IWebhookEndpoint> = {};
