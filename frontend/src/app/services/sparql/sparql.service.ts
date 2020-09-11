import { SW_API } from './../../globals';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { QueryDTO } from 'src/types';

@Injectable({
  providedIn: 'root'
})
export class SparqlService {

  constructor(private http: HttpClient) { }

  submitQuery(dto: QueryDTO): Observable<any> {
    return this.http.post(SW_API + 'query', dto, {responseType: 'text'});
  }

}
