import { RepresentativeService } from './../services/representative.service';
import { Representative } from './../Representative';
import { EventBrokerService } from './../services/events/event-broker.service';
import { AuthService } from './../services/auth/auth.service';
import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  isCollapsedReprInfo = false;
  isCollapsedOrgInfo = false;

  repr = new Representative();
  asd = 'dasdas';

  constructor(private authService: AuthService, 
              private eventBroker: EventBrokerService,
              private reprService: RepresentativeService) { }

  ngOnInit() {
      this.authService.checkLoggedIn();
      this.authService.isUserLoggedIn.next(true);

      this.reprService.getRepr(this.authService.getUser()).subscribe(data =>{
        this.repr = data;
      }, error => {
        console.log(error);
      });

  }

}
