import { IImportFile } from 'app/shared/model/import-file.model';
import { IInvoice } from 'app/shared/model/invoice.model';
import { ImportItemStatus } from 'app/shared/model/enumerations/import-item-status.model';

export interface IImportFileItem {
  id?: number;
  lineIndex?: number;
  rawData?: string;
  rawHash?: string | null;
  sourceFamily?: string | null;
  normalizedFamily?: string | null;
  normalizedJson?: string | null;
  status?: keyof typeof ImportItemStatus;
  errorCode?: string | null;
  errorMessage?: string | null;
  importFile?: IImportFile;
  invoice?: IInvoice | null;
}

export const defaultValue: Readonly<IImportFileItem> = {};
