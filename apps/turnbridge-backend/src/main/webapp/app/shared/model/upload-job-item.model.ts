import dayjs from 'dayjs';
import { IUploadJob } from 'app/shared/model/upload-job.model';
import { JobItemStatus } from 'app/shared/model/enumerations/job-item-status.model';
import { TaxType } from 'app/shared/model/enumerations/tax-type.model';

export interface IUploadJobItem {
  id?: number;
  lineNo?: number;
  traceId?: string;
  status?: keyof typeof JobItemStatus;
  resultCode?: string | null;
  resultMsg?: string | null;
  buyerId?: string | null;
  buyerName?: string | null;
  currency?: string | null;
  amountExcl?: number | null;
  taxAmount?: number | null;
  amountIncl?: number | null;
  taxType?: keyof typeof TaxType | null;
  invoiceDate?: dayjs.Dayjs | null;
  invoiceNo?: string | null;
  assignedPrefix?: string | null;
  rawPayload?: string | null;
  rawHash?: string | null;
  profileDetected?: string | null;
  job?: IUploadJob;
}

export const defaultValue: Readonly<IUploadJobItem> = {};
