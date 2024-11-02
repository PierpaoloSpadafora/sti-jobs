import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HeaderComponent } from './components/header/header.component';
import { NgOptimizedImage } from '@angular/common';
import { CreateComponent } from './components/create/create.component';
import { ScheduleComponent } from './components/schedule/schedule.component';
import { GraphsComponent } from './components/graphs/graphs.component';
import { PageCardComponent } from './components/page-card/page-card.component';
import { JsonHandlerComponent } from './components/json-handler/json-handler.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    NavbarComponent,
    HeaderComponent,
    CreateComponent,
    ScheduleComponent,
    GraphsComponent,
    PageCardComponent,
    JsonHandlerComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgOptimizedImage,
    CommonModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
