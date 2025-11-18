import dayjs from 'dayjs';
import { IImportFile } from 'app/shared/model/import-file.model';

export interface IImportFileLog {
  id?: number;
  eventCode?: string;
  level?: string;
  message?: string | null;
  detail?: string | null;
  occurredAt?: dayjs.Dayjs | null;
  importFile?: IImportFile;
}

export const defaultValue: Readonly<IImportFileLog> = {};
