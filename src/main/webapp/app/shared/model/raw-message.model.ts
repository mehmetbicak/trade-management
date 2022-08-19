import dayjs from 'dayjs';
import { MessageTypeEnum } from 'app/shared/model/enumerations/message-type-enum.model';
import { ExecutionTypeEnum } from 'app/shared/model/enumerations/execution-type-enum.model';

export interface IRawMessage {
  id?: number;
  messageType?: MessageTypeEnum | null;
  executionType?: ExecutionTypeEnum | null;
  kafkaKey?: string | null;
  companyId?: string | null;
  executionId?: string | null;
  message?: string | null;
  creationTime?: string | null;
}

export const defaultValue: Readonly<IRawMessage> = {};
