
import { JobDTO } from './jobDTO';
import { MachineDTO } from './machineDTO';
import { MachineTypeDTO } from './machineTypeDTO';
import { ScheduleDTO } from './scheduleDTO';

export interface JsonDTO {
    jobs?: Array<JobDTO>;
    machines?: Array<MachineDTO>;
    machineTypes?: Array<MachineTypeDTO>;
    schedules?: Array<ScheduleDTO>;
}
