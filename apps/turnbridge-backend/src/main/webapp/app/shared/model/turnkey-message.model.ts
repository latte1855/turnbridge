import dayjs from 'dayjs';
import { IInvoice } from 'app/shared/model/invoice.model';
import { MessageFamily } from 'app/shared/model/enumerations/message-family.model';

export interface ITurnkeyMessage {
  id?: number;
  messageId?: string;
  messageFamily?: keyof typeof MessageFamily;
  type?: string;
  code?: string | null;
  message?: string | null;
  payloadPath?: string | null;
  receivedAt?: dayjs.Dayjs | null;
  invoice?: IInvoice | null;
}

export const defaultValue: Readonly<ITurnkeyMessage> = {};
