import { API } from './../../globals';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject } from 'rxjs';
import { JwtResponse, Token, AuthLoginInfo } from './../../../types';
import { Injectable } from '@angular/core';
import { HttpRequest, HttpClient } from '@angular/common/http';

@Injectable()
export class AuthService {

  public isUserLoggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  cachedRequests: Array<HttpRequest<any>> = [];
  refresh = false;

  constructor(private router: Router, private http: HttpClient) { }

  public setToken(token: JwtResponse) {
    localStorage.setItem('token', JSON.stringify(token));
  }

  public getToken(): string {
    const storedData: JwtResponse = JSON.parse(localStorage.getItem('token'));
    return storedData != null ? storedData.accessToken : null;
  }

  public deleteToken() {
    localStorage.removeItem('token');
    // localStorage.setItem('token', null);
  }

  public isAuthenticated(): boolean {
    return this.getToken() != null;
    // return tokenNotExpired(null, token);
  }

  public logOut() {
    this.deleteToken();
    this.isUserLoggedIn.next(false);
    this.router.navigate(['/login']);
  }

  public collectFailedRequest(request): void {
    this.cachedRequests.push(request);
  }

  public retryFailedRequests(): void {
    while (this.cachedRequests.length > 0) {
      const request: HttpRequest<any> = this.cachedRequests.pop();
      this.router.navigate([request.url]);
      break;
    }
    this.cachedRequests = [];
  }

  public attemptLogIn(info: AuthLoginInfo): Observable<any> {
    return this.http.post(API + 'auth/signin', info);
  }

  public checkLoggedIn() {
    if (!this.isAuthenticated()) {
      this.router.navigate(['/login']);
    }
  }

  public setRefresh(value: boolean) {
    this.refresh = value;
  }

  public getRefresh(): boolean {
    return this.refresh;
  }

}
