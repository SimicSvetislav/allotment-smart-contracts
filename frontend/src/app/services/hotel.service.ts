import { Observable } from 'rxjs';
import { HOTEL_API } from './../globals';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HotelService {

  constructor(private http: HttpClient) { }

  getHotelsByAcc(id: number): Observable<any> {
    return this.http.get(HOTEL_API + id);
  }

}
