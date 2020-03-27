import { Observable } from 'rxjs';
import { ORG_API } from './../globals';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {

  constructor(private http: HttpClient) { }

  getAgencies(): Observable<any> {
    return this.http.get(ORG_API + 'agencies');
  }

  getAccomodations(): Observable<any> {
    return this.http.get(ORG_API + 'accomodations');
  }

}
