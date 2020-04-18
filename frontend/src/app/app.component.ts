import { IEventListener, EventBrokerService } from './services/events/event-broker.service';
import { AuthService } from './services/auth/auth.service';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';

import { ChangeDetectorRef, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit/*, AfterViewInit*/ {

  title = 'Frontend';

  logged = false;
  activeTab: string;

  newProposals = 0;

  private myEventListener: IEventListener;

  constructor(private router: Router, private authService: AuthService,
              private eventBroker: EventBrokerService,
              private cdr: ChangeDetectorRef) {
                /*this.myEventListener = eventBroker.listen('refresh', () => {
                  this.ngOnInit();
                });*/
                this.authService.isUserLoggedIn.subscribe( value => {
                  this.logged = value;
                  this.activeTab = this.authService.getActiveTab();
                  // get number of new proposals

                });
              }

  ngOnInit(): void {

    this.activeTab = this.authService.getActiveTab();

    if (this.authService.isAuthenticated()) {
      this.logged = true;
    } else {
      this.logged = false;
    }

  }

  logout() {
    this.authService.logOut();
    this.ngOnInit();
    this.router.navigate(['/login']);
  }

  login() {
    this.router.navigate(['/login']);
  }

  /*register() {
    this.router.navigate(['/register']);
  }*/

  navigate(tabName: string, path: string) {

    this.authService.setActiveTab(tabName);
    this.activeTab = tabName;

    this.router.navigateByUrl(path);

    return false;

  }
}
