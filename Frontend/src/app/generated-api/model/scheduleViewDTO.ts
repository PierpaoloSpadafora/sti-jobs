/**
 * OpenAPI definition
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: v0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

export interface ScheduleViewDTO { 
    id?: number;
    jobId?: number;
    jobName?: string;
    machineType?: string;
    startTime?: Date;
    endTime?: Date;
    duration?: number;
    priority?: ScheduleViewDTO.PriorityEnum;
}
export namespace ScheduleViewDTO {
    export type PriorityEnum = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
    export const PriorityEnum = {
        LOW: 'LOW' as PriorityEnum,
        MEDIUM: 'MEDIUM' as PriorityEnum,
        HIGH: 'HIGH' as PriorityEnum,
        URGENT: 'URGENT' as PriorityEnum
    };
}