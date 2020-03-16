import { Web3jService } from './../services/web3j.service';
import { AuthService } from './../services/auth/auth.service';
import { JwtResponse } from './../../types';
import { TestService } from './../services/test.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-contract-form',
  templateUrl: './contract-form.component.html',
  styleUrls: ['./contract-form.component.scss']
})
export class ContractFormComponent implements OnInit {

  constructor(private testService: TestService, private w3jService: Web3jService,
              private authService: AuthService) { }

  ngOnInit() {

  }

  test() {
    // alert('TEST');

    this.testService.testAddDummyUser().subscribe(data => {
      alert('ADDED');
    }, error => console.log(error));


  }

  test2() {
    // alert('TEST');

    this.testService.testLogIn().subscribe(data => {

      try {
        this.authService.setToken(data as JwtResponse);
      } catch {
        console.error('Token not in valid format');
      }

    }, error => console.error(error));

    console.log('Stored token: ', this.authService.getToken());

  }

  test3() {
    // alert('TEST');

    this.testService.testTokenSending().subscribe(data => {
      // alert('SENDED SUCCESSFULLY');
      console.log(data);
    }, error => console.log(error));


  }

  test4() {
    // alert('TEST');

    this.testService.testNoToken().subscribe(data => {
      // alert('SENDED SUCCESSFULLY');
      console.log(data);
    }, error => console.log(error));


  }

  testw3j() {
    this.w3jService.test().subscribe(data => {
      alert(data);
    });
  }

  logout() {
    this.authService.logOut();
  }

}
