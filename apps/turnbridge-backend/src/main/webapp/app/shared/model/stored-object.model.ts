import { StoragePurpose } from 'app/shared/model/enumerations/storage-purpose.model';

export interface IStoredObject {
  id?: number;
  bucket?: string;
  objectKey?: string;
  mediaType?: string;
  contentLength?: number;
  sha256?: string;
  purpose?: keyof typeof StoragePurpose;
  filename?: string | null;
  storageClass?: string | null;
  encryption?: string | null;
  metadata?: string | null;
}

export const defaultValue: Readonly<IStoredObject> = {};
