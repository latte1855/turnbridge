import { ITenant } from 'app/shared/model/tenant.model';
import { ImportType } from 'app/shared/model/enumerations/import-type.model';
import { ImportStatus } from 'app/shared/model/enumerations/import-status.model';

export interface IImportFile {
  id?: number;
  importType?: keyof typeof ImportType;
  originalFilename?: string;
  sha256?: string;
  totalCount?: number;
  successCount?: number | null;
  errorCount?: number | null;
  status?: keyof typeof ImportStatus;
  legacyType?: string | null;
  tenant?: ITenant | null;
}

export const defaultValue: Readonly<IImportFile> = {};
