import { REPR_API } from './../globals';
import { HttpClient } from '@angular/common/http';
import { Representative } from './../Representative';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RepresentativeService {

  constructor(private http: HttpClient) { }

  getRepr(reprId: string): Observable<any> {
    return this.http.get(REPR_API + reprId);
  }

}
