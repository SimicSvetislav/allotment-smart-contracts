import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { WEB3J_API } from './../globals';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Web3jService {

  constructor(private http: HttpClient) { }

  public test(): Observable<string> {
    return this.http.get(WEB3J_API + 'test', {responseType: 'text'});
  }
}
