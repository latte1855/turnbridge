import dayjs from 'dayjs';
import { TrackRangeStatus } from 'app/shared/model/enumerations/track-range-status.model';

export interface ITrackRange {
  id?: number;
  sellerId?: string;
  period?: string;
  prefix?: string;
  startNo?: number;
  endNo?: number;
  currentNo?: number;
  status?: keyof typeof TrackRangeStatus;
  version?: number;
  lockOwner?: string | null;
  lockAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ITrackRange> = {};
