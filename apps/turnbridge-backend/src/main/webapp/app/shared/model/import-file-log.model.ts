import { IImportFile } from 'app/shared/model/import-file.model';

export interface IImportFileLog {
  id?: number;
  lineIndex?: number;
  field?: string | null;
  errorCode?: string;
  message?: string | null;
  rawLine?: string | null;
  sourceFamily?: string | null;
  normalizedFamily?: string | null;
  importFile?: IImportFile;
}

export const defaultValue: Readonly<IImportFileLog> = {};
