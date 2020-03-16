import { AuthService } from './auth/auth.service';
import { API } from './../globals';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TestService {

  constructor(private http: HttpClient, private authService: AuthService) { }

  testAddDummyUser() {
    return this.http.get(API + 'auth/signup');
  }

  testLogIn() {
    return this.http.get(API + 'auth/signin');
  }

  testTokenSending(): Observable<string> {
    let headers: HttpHeaders = new HttpHeaders();
    headers = headers.append(`Authorization`, `Bearer ${this.authService.getToken}`);
    return this.http.get('http://localhost:8080/api/repr/test',
      {headers, responseType: 'text'});
  }

  testNoToken(): Observable<string> {

    return this.http.get('http://localhost:8080/api/repr/test',
      {responseType: 'text'});
  }


}
