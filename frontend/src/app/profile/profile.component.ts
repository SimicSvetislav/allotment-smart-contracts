import { EventBrokerService } from './../services/events/event-broker.service';
import { AuthService } from './../services/auth/auth.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  constructor(private authService: AuthService, private eventBroker: EventBrokerService) { }

  ngOnInit() {
      this.authService.checkLoggedIn();
      this.authService.isUserLoggedIn.next(true);

  }

}
