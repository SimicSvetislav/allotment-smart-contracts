import { ProposeComponent } from './propose/propose.component';
import { NgbModule, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { EventBrokerService } from './services/events/event-broker.service';
import { JwtInterceptor } from './jwt.interceptor';
import { TokenInterceptor } from './token.interceptor';
import { AuthService } from './services/auth/auth.service';
import { Routes, RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ContractFormComponent } from './contract-form/contract-form.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { LoginComponent } from './login/login.component';
import { ProposalsComponent } from './proposals/proposals.component';
import { ProfileComponent } from './profile/profile.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { ContractsComponent } from './contracts/contracts.component';

const appRoutes: Routes = [
  { path: 'sc-form', component: ContractFormComponent },
  { path: 'login', component: LoginComponent },
  { path: 'proposals', component: ProposalsComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'contracts', component: ContractsComponent },
  { path: 'propose', component: ProposeComponent },
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    ContractFormComponent,
    LoginComponent,
    ProposalsComponent,
    ProfileComponent,
    NotFoundComponent,
    ContractsComponent,
    ProposeComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true }
    ),
    ToastrModule.forRoot({
      timeOut: 4000,
      closeButton: true,
      positionClass: 'toast-top-right',
    }),
    HttpClientModule,
    BrowserAnimationsModule,
    NgbModule,
    NgbDatepickerModule,
    NgMultiSelectDropDownModule.forRoot(),
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    },
    // AuthService,
    EventBrokerService,
  ],
  bootstrap: [AppComponent]
})

export class AppModule { }
