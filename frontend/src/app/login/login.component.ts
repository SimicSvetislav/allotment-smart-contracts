import { JwtResponse } from './../../types';
import { User, AuthLoginInfo } from '../../types';
import { AuthService } from '../services/auth/auth.service';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {


  user: User = new User();
  constructor(private router: Router, private authService: AuthService,
              private autService: AuthService, private toastr: ToastrService) { }
  str = '';

  private loginInfo: AuthLoginInfo;
  isLoggedIn = false;
  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/profile']);
    }
  }

  home() {
    this.router.navigate(['/profile']);
  }

  login() {
    // alert("Usao!")
    this.loginInfo = new AuthLoginInfo(this.user.username, this.user.password);
    this.authService.attemptLogIn(this.loginInfo).subscribe(data => {

      if (data.accessToken === undefined) {
        alert('Nesto nije u redu!');
      } else {

        this.authService.setToken(data as JwtResponse);
        this.authService.isUserLoggedIn.next(true);
        this.authService.saveUser(data.id);

        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.router.navigate(['/profile']);
      }

    });
  }

}
