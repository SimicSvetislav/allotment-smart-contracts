import { Representative } from './../../Representative';
import { API } from './../../globals';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject } from 'rxjs';
import { JwtResponse, Token, AuthLoginInfo } from './../../../types';
import { Injectable } from '@angular/core';
import { HttpRequest, HttpClient } from '@angular/common/http';

const ACTIVE_TAB_KEY = 'activeTab';
const USER_KEY = 'userId';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  public isUserLoggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  cachedRequests: Array<HttpRequest<any>> = [];
  refresh = false;

  constructor(private router: Router, private http: HttpClient) { }

  public setToken(token: string) {
    sessionStorage.setItem('token', token);
  }

  public getToken(): string {
    const storedData = sessionStorage.getItem('token');
    return storedData;
    // return storedData != null ? storedData.accessToken : null;
  }

  public deleteToken() {
    sessionStorage.removeItem('token');
    // sessionStorage.setItem('token', null);
  }

  public isAuthenticated(): boolean {
    return this.getToken() != null;
    // return tokenNotExpired(null, token);
  }

  public logOut() {
    this.deleteToken();
    this.isUserLoggedIn.next(false);
    this.setActiveTab('Profile');
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

  public setActiveTab(value: string) {
    sessionStorage.setItem(ACTIVE_TAB_KEY, value);
  }

  public getActiveTab(): string {
    const at = sessionStorage.getItem(ACTIVE_TAB_KEY);
    return at != null ? at : 'Profile';
  }

  public saveUser(user: Representative) {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public getUser(): Representative {
    return JSON.parse(sessionStorage.getItem(USER_KEY));
  }

}
