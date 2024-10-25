import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app/app.routes';
import { Configuration, UserControllerService } from './app/generated-api'; 

const apiConfiguration = new Configuration({
  basePath: 'http://localhost:7001/sti-jobs'
});

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    {
      provide: Configuration,
      useValue: apiConfiguration
    },
    UserControllerService
  ]
}).catch(err => console.error(err));
