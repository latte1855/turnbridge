import { IInvoice } from 'app/shared/model/invoice.model';

export interface IInvoiceItem {
  id?: number;
  description?: string;
  quantity?: number | null;
  unitPrice?: number | null;
  amount?: number | null;
  sequence?: number | null;
  invoice?: IInvoice;
}

export const defaultValue: Readonly<IInvoiceItem> = {};
