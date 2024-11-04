import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { Configuration } from './configuration';
import { HttpClient } from '@angular/common/http';

import { JobControllerService } from './api/jobController.service';
import { JsonControllerService } from './api/jsonController.service';
import { MachineControllerService } from './api/machineController.service';
import { MachineTypeControllerService } from './api/machineTypeController.service';
import { ScheduleControllerService } from './api/scheduleController.service';
import { UserControllerService } from './api/userController.service';

@NgModule({
  declarations: [],
  imports: [],
  exports: [],
  providers: [
    JobControllerService,
    JsonControllerService,
    MachineControllerService,
    MachineTypeControllerService,
    ScheduleControllerService,
    UserControllerService ]
})
export class ApiModule {
    public static forRoot(configurationFactory: () => Configuration): ModuleWithProviders<ApiModule> {
        return {
            ngModule: ApiModule,
            providers: [ { provide: Configuration, useFactory: configurationFactory } ]
        };
    }

    constructor( @Optional() @SkipSelf() parentModule: ApiModule,
                 @Optional() http: HttpClient) {
        if (parentModule) {
            throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
        }
        if (!http) {
            throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
            'See also https://github.com/angular/angular/issues/20575');
        }
    }
}
