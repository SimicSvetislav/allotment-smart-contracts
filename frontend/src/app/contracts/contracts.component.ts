import { AuthService } from './../services/auth/auth.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-contracts',
  templateUrl: './contracts.component.html',
  styleUrls: ['./contracts.component.scss']
})
export class ContractsComponent implements OnInit {

  constructor(private authService: AuthService) { }

  ngOnInit() {

    this.authService.checkLoggedIn();
    this.authService.isUserLoggedIn.next(true);

    this.authService.setActiveTab('Profile');

  }

}
