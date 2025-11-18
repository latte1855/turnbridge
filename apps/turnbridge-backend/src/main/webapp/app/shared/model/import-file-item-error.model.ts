import dayjs from 'dayjs';
import { IImportFileItem } from 'app/shared/model/import-file-item.model';

export interface IImportFileItemError {
  id?: number;
  columnIndex?: number;
  fieldName?: string;
  errorCode?: string;
  message?: string | null;
  severity?: string | null;
  occurredAt?: dayjs.Dayjs | null;
  importFileItem?: IImportFileItem;
}

export const defaultValue: Readonly<IImportFileItemError> = {};
