import dayjs from 'dayjs';
import { IImportFile } from 'app/shared/model/import-file.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { MessageFamily } from 'app/shared/model/enumerations/message-family.model';
import { InvoiceStatus } from 'app/shared/model/enumerations/invoice-status.model';

export interface IInvoice {
  id?: number;
  invoiceNo?: string;
  messageFamily?: keyof typeof MessageFamily;
  buyerId?: string | null;
  buyerName?: string | null;
  sellerId?: string | null;
  sellerName?: string | null;
  salesAmount?: number | null;
  taxAmount?: number | null;
  totalAmount?: number | null;
  taxType?: string | null;
  normalizedJson?: string | null;
  invoiceStatus?: keyof typeof InvoiceStatus;
  issuedAt?: dayjs.Dayjs | null;
  legacyType?: string | null;
  tbCode?: string | null;
  tbCategory?: string | null;
  tbCanAutoRetry?: boolean | null;
  tbRecommendedAction?: string | null;
  tbSourceCode?: string | null;
  tbSourceMessage?: string | null;
  tbResultCode?: string | null;
  importFile?: IImportFile;
  tenant?: ITenant | null;
}

export const defaultValue: Readonly<IInvoice> = {
  tbCanAutoRetry: false,
};
