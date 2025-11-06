import { IStoredObject } from 'app/shared/model/stored-object.model';
import { UploadJobStatus } from 'app/shared/model/enumerations/upload-job-status.model';

export interface IUploadJob {
  id?: number;
  jobId?: string;
  sellerId?: string;
  sellerName?: string | null;
  period?: string | null;
  profile?: string | null;
  sourceFilename?: string | null;
  sourceMediaType?: string | null;
  status?: keyof typeof UploadJobStatus;
  total?: number;
  accepted?: number;
  failed?: number;
  sent?: number;
  remark?: string | null;
  originalFile?: IStoredObject;
  resultFile?: IStoredObject | null;
}

export const defaultValue: Readonly<IUploadJob> = {};
