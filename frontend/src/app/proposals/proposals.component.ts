import { AuthService } from './../services/auth/auth.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-proposals',
  templateUrl: './proposals.component.html',
  styleUrls: ['./proposals.component.scss']
})
export class ProposalsComponent implements OnInit {

  constructor(private authService: AuthService) { }

  ngOnInit() {

    this.authService.checkLoggedIn();
    this.authService.isUserLoggedIn.next(true);

    this.authService.setActiveTab('Proposals');

  }

}
