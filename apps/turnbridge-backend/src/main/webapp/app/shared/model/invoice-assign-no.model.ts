import { ITenant } from 'app/shared/model/tenant.model';

export interface IInvoiceAssignNo {
  id?: number;
  track?: string;
  period?: string;
  fromNo?: string;
  toNo?: string;
  usedCount?: number | null;
  rollSize?: number | null;
  status?: string | null;
  tenant?: ITenant;
}

export const defaultValue: Readonly<IInvoiceAssignNo> = {};
