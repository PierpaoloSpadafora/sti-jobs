export interface ScheduleDTO {
  id?: number;
  jobId?: number;
  machineType: string;
  dueDate: string;
  startTime: string;
  duration?: number;
  status: string;
}


