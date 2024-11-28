import { JobDTO } from './jobDTO';

export interface ScheduleDTO {
  id?: number;
  jobId?: number;
  job?: JobDTO;
  machineType: string;
  dueDate: string;
  startTime: string;
  duration?: number;
  status: string;
}
