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
 *//* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent }                           from '@angular/common/http';
import { CustomHttpUrlEncodingCodec }                        from '../encoder';

import { Observable }                                        from 'rxjs';

import { ScheduleDTO } from '../model/scheduleDTO';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable()
export class ScheduleControllerService {

    protected basePath = 'http://localhost:7001/sti-jobs';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        if (basePath) {
            this.basePath = basePath;
        }
        if (configuration) {
            this.configuration = configuration;
            this.basePath = basePath || configuration.basePath || this.basePath;
        }
    }

    /**
     * @param consumes string[] mime-types
     * @return true: consumes contains 'multipart/form-data', false: otherwise
     */
    private canConsumeForm(consumes: string[]): boolean {
        const form = 'multipart/form-data';
        for (const consume of consumes) {
            if (form === consume) {
                return true;
            }
        }
        return false;
    }


    /**
     * 
     * 
     * @param machineId 
     * @param startTime 
     * @param endTime 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public checkTimeSlotAvailability(machineId: number, startTime: Date, endTime: Date, observe?: 'body', reportProgress?: boolean): Observable<boolean>;
    public checkTimeSlotAvailability(machineId: number, startTime: Date, endTime: Date, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<boolean>>;
    public checkTimeSlotAvailability(machineId: number, startTime: Date, endTime: Date, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<boolean>>;
    public checkTimeSlotAvailability(machineId: number, startTime: Date, endTime: Date, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (machineId === null || machineId === undefined) {
            throw new Error('Required parameter machineId was null or undefined when calling checkTimeSlotAvailability.');
        }

        if (startTime === null || startTime === undefined) {
            throw new Error('Required parameter startTime was null or undefined when calling checkTimeSlotAvailability.');
        }

        if (endTime === null || endTime === undefined) {
            throw new Error('Required parameter endTime was null or undefined when calling checkTimeSlotAvailability.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (machineId !== undefined && machineId !== null) {
            queryParameters = queryParameters.set('machineId', <any>machineId);
        }
        if (startTime !== undefined && startTime !== null) {
            queryParameters = queryParameters.set('startTime', <any>startTime.toISOString());
        }
        if (endTime !== undefined && endTime !== null) {
            queryParameters = queryParameters.set('endTime', <any>endTime.toISOString());
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<boolean>('get',`${this.basePath}/api/v1/schedules/availability`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param body 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public createSchedule(body: ScheduleDTO, observe?: 'body', reportProgress?: boolean): Observable<ScheduleDTO>;
    public createSchedule(body: ScheduleDTO, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<ScheduleDTO>>;
    public createSchedule(body: ScheduleDTO, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<ScheduleDTO>>;
    public createSchedule(body: ScheduleDTO, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (body === null || body === undefined) {
            throw new Error('Required parameter body was null or undefined when calling createSchedule.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        return this.httpClient.request<ScheduleDTO>('post',`${this.basePath}/api/v1/schedules`,
            {
                body: body,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param id 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public deleteSchedule(id: number, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public deleteSchedule(id: number, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public deleteSchedule(id: number, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public deleteSchedule(id: number, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling deleteSchedule.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<any>('delete',`${this.basePath}/api/v1/schedules/${encodeURIComponent(String(id))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getAllSchedules(observe?: 'body', reportProgress?: boolean): Observable<Array<ScheduleDTO>>;
    public getAllSchedules(observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<ScheduleDTO>>>;
    public getAllSchedules(observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<ScheduleDTO>>>;
    public getAllSchedules(observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<ScheduleDTO>>('get',`${this.basePath}/api/v1/schedules`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param until 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getPastSchedules(until?: Date, observe?: 'body', reportProgress?: boolean): Observable<Array<ScheduleDTO>>;
    public getPastSchedules(until?: Date, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<ScheduleDTO>>>;
    public getPastSchedules(until?: Date, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<ScheduleDTO>>>;
    public getPastSchedules(until?: Date, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {


        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (until !== undefined && until !== null) {
            queryParameters = queryParameters.set('until', <any>until.toISOString());
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<ScheduleDTO>>('get',`${this.basePath}/api/v1/schedules/past`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param jobId 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getSchedulesByJobId(jobId: number, observe?: 'body', reportProgress?: boolean): Observable<Array<ScheduleDTO>>;
    public getSchedulesByJobId(jobId: number, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<ScheduleDTO>>>;
    public getSchedulesByJobId(jobId: number, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<ScheduleDTO>>>;
    public getSchedulesByJobId(jobId: number, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (jobId === null || jobId === undefined) {
            throw new Error('Required parameter jobId was null or undefined when calling getSchedulesByJobId.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<ScheduleDTO>>('get',`${this.basePath}/api/v1/schedules/job/${encodeURIComponent(String(jobId))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param machineId 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getSchedulesByMachineId(machineId: number, observe?: 'body', reportProgress?: boolean): Observable<Array<ScheduleDTO>>;
    public getSchedulesByMachineId(machineId: number, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<ScheduleDTO>>>;
    public getSchedulesByMachineId(machineId: number, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<ScheduleDTO>>>;
    public getSchedulesByMachineId(machineId: number, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (machineId === null || machineId === undefined) {
            throw new Error('Required parameter machineId was null or undefined when calling getSchedulesByMachineId.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<ScheduleDTO>>('get',`${this.basePath}/api/v1/schedules/machine/${encodeURIComponent(String(machineId))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param status 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getSchedulesByStatus(status: string, observe?: 'body', reportProgress?: boolean): Observable<Array<ScheduleDTO>>;
    public getSchedulesByStatus(status: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<ScheduleDTO>>>;
    public getSchedulesByStatus(status: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<ScheduleDTO>>>;
    public getSchedulesByStatus(status: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (status === null || status === undefined) {
            throw new Error('Required parameter status was null or undefined when calling getSchedulesByStatus.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<ScheduleDTO>>('get',`${this.basePath}/api/v1/schedules/status/${encodeURIComponent(String(status))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param startTime 
     * @param endTime 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getSchedulesInTimeRange(startTime: Date, endTime: Date, observe?: 'body', reportProgress?: boolean): Observable<Array<ScheduleDTO>>;
    public getSchedulesInTimeRange(startTime: Date, endTime: Date, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<ScheduleDTO>>>;
    public getSchedulesInTimeRange(startTime: Date, endTime: Date, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<ScheduleDTO>>>;
    public getSchedulesInTimeRange(startTime: Date, endTime: Date, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (startTime === null || startTime === undefined) {
            throw new Error('Required parameter startTime was null or undefined when calling getSchedulesInTimeRange.');
        }

        if (endTime === null || endTime === undefined) {
            throw new Error('Required parameter endTime was null or undefined when calling getSchedulesInTimeRange.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (startTime !== undefined && startTime !== null) {
            queryParameters = queryParameters.set('startTime', <any>startTime.toISOString());
        }
        if (endTime !== undefined && endTime !== null) {
            queryParameters = queryParameters.set('endTime', <any>endTime.toISOString());
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<ScheduleDTO>>('get',`${this.basePath}/api/v1/schedules/timeRange`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param from 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getUpcomingSchedules(from?: Date, observe?: 'body', reportProgress?: boolean): Observable<Array<ScheduleDTO>>;
    public getUpcomingSchedules(from?: Date, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<ScheduleDTO>>>;
    public getUpcomingSchedules(from?: Date, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<ScheduleDTO>>>;
    public getUpcomingSchedules(from?: Date, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {


        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (from !== undefined && from !== null) {
            queryParameters = queryParameters.set('from', <any>from.toISOString());
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<ScheduleDTO>>('get',`${this.basePath}/api/v1/schedules/upcoming`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param body 
     * @param id 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public updateSchedule(body: ScheduleDTO, id: number, observe?: 'body', reportProgress?: boolean): Observable<ScheduleDTO>;
    public updateSchedule(body: ScheduleDTO, id: number, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<ScheduleDTO>>;
    public updateSchedule(body: ScheduleDTO, id: number, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<ScheduleDTO>>;
    public updateSchedule(body: ScheduleDTO, id: number, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (body === null || body === undefined) {
            throw new Error('Required parameter body was null or undefined when calling updateSchedule.');
        }

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling updateSchedule.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        return this.httpClient.request<ScheduleDTO>('put',`${this.basePath}/api/v1/schedules/${encodeURIComponent(String(id))}`,
            {
                body: body,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * 
     * 
     * @param id 
     * @param status 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public updateScheduleStatus(id: number, status: string, observe?: 'body', reportProgress?: boolean): Observable<ScheduleDTO>;
    public updateScheduleStatus(id: number, status: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<ScheduleDTO>>;
    public updateScheduleStatus(id: number, status: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<ScheduleDTO>>;
    public updateScheduleStatus(id: number, status: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling updateScheduleStatus.');
        }

        if (status === null || status === undefined) {
            throw new Error('Required parameter status was null or undefined when calling updateScheduleStatus.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (status !== undefined && status !== null) {
            queryParameters = queryParameters.set('status', <any>status);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<ScheduleDTO>('get',`${this.basePath}/api/v1/schedules/${encodeURIComponent(String(id))}`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
