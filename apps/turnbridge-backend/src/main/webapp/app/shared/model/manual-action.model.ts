import dayjs from 'dayjs';
import { ITenant } from 'app/shared/model/tenant.model';
import { IInvoice } from 'app/shared/model/invoice.model';
import { IImportFile } from 'app/shared/model/import-file.model';
import { ManualActionType } from 'app/shared/model/enumerations/manual-action-type.model';
import { ApprovalStatus } from 'app/shared/model/enumerations/approval-status.model';

export interface IManualAction {
  id?: number;
  actionType?: keyof typeof ManualActionType;
  reason?: string;
  status?: keyof typeof ApprovalStatus;
  requestedBy?: string | null;
  requestedAt?: dayjs.Dayjs | null;
  approvedBy?: string | null;
  approvedAt?: dayjs.Dayjs | null;
  resultMessage?: string | null;
  tenant?: ITenant;
  invoice?: IInvoice | null;
  importFile?: IImportFile | null;
}

export const defaultValue: Readonly<IManualAction> = {};
