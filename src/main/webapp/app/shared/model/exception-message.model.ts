import dayjs from 'dayjs';
import { StatusTypeEnum } from 'app/shared/model/enumerations/status-type-enum.model';

export interface IExceptionMessage {
  id?: number;
  kafkaKey?: string | null;
  exceptionCode?: string | null;
  exceptionMessage?: string | null;
  retryTopic?: string | null;
  rawMessage?: string | null;
  retryCount?: number | null;
  creationTime?: string | null;
  lastExecutionTime?: string | null;
  statusType?: StatusTypeEnum | null;
}

export const defaultValue: Readonly<IExceptionMessage> = {};
