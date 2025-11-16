import dayjs from 'dayjs';
import { IWebhookEndpoint } from 'app/shared/model/webhook-endpoint.model';
import { DeliveryResult } from 'app/shared/model/enumerations/delivery-result.model';

export interface IWebhookDeliveryLog {
  id?: number;
  deliveryId?: string;
  event?: string;
  payload?: string | null;
  status?: keyof typeof DeliveryResult;
  httpStatus?: number | null;
  attempts?: number | null;
  lastError?: string | null;
  deliveredAt?: dayjs.Dayjs | null;
  webhookEndpoint?: IWebhookEndpoint;
}

export const defaultValue: Readonly<IWebhookDeliveryLog> = {};
